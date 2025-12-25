package com.ecommerce.project.diagnostic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.Session;
import java.util.*;

/**
 * Service for diagnosing email configuration and connectivity issues
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailDiagnosticService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.host:}")
    private String smtpHost;

    @Value("${spring.mail.port:0}")
    private int smtpPort;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    @Value("${spring.mail.password:}")
    private String smtpPassword;

    @Value("${email.from:}")
    private String fromEmail;

    @Value("${email.admin:}")
    private String adminEmail;

    @Value("${spring.mail.properties.mail.smtp.auth:false}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:false}")
    private boolean starttlsEnable;

    /**
     * Performs comprehensive email configuration diagnosis
     */
    public EmailDiagnosticResult diagnoseEmailConfiguration() {
        log.info("Starting email configuration diagnosis...");
        
        EmailDiagnosticResult result = new EmailDiagnosticResult();
        
        // Check configuration properties
        checkConfigurationProperties(result);
        
        // Test SMTP connectivity
        testSmtpConnectivity(result);
        
        // Test email sending
        testEmailSending(result);
        
        log.info("Email diagnosis completed. Overall status: {}", result.isOverallHealthy() ? "HEALTHY" : "UNHEALTHY");
        return result;
    }

    private void checkConfigurationProperties(EmailDiagnosticResult result) {
        log.info("Checking email configuration properties...");
        
        List<String> issues = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Check SMTP host
        if (smtpHost == null || smtpHost.trim().isEmpty()) {
            issues.add("SMTP host is not configured (spring.mail.host)");
        } else {
            log.info("SMTP Host: {}", smtpHost);
        }
        
        // Check SMTP port
        if (smtpPort <= 0) {
            issues.add("SMTP port is not configured or invalid (spring.mail.port)");
        } else {
            log.info("SMTP Port: {}", smtpPort);
        }
        
        // Check username
        if (smtpUsername == null || smtpUsername.trim().isEmpty()) {
            issues.add("SMTP username is not configured (spring.mail.username)");
        } else {
            log.info("SMTP Username: {}", maskEmail(smtpUsername));
        }
        
        // Check password
        if (smtpPassword == null || smtpPassword.trim().isEmpty()) {
            issues.add("SMTP password is not configured (spring.mail.password)");
        } else {
            log.info("SMTP Password: [CONFIGURED - {} characters]", smtpPassword.length());
        }
        
        // Check from email
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            issues.add("From email address is not configured (email.from)");
        } else if (!isValidEmail(fromEmail)) {
            issues.add("From email address format is invalid: " + fromEmail);
        } else {
            log.info("From Email: {}", fromEmail);
        }
        
        // Check admin email
        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            issues.add("Admin email address is not configured (email.admin)");
        } else if (!isValidEmail(adminEmail)) {
            issues.add("Admin email address format is invalid: " + adminEmail);
        } else {
            log.info("Admin Email: {}", adminEmail);
        }
        
        // Check authentication settings
        log.info("SMTP Auth: {}", smtpAuth);
        log.info("STARTTLS Enable: {}", starttlsEnable);
        
        if (!smtpAuth) {
            warnings.add("SMTP authentication is disabled - this may cause issues with Gmail");
        }
        
        if (!starttlsEnable) {
            warnings.add("STARTTLS is disabled - this may cause security issues");
        }
        
        result.setConfigurationIssues(issues);
        result.setConfigurationWarnings(warnings);
        result.setConfigurationValid(issues.isEmpty());
        
        if (!issues.isEmpty()) {
            log.error("Configuration issues found: {}", issues);
        }
        if (!warnings.isEmpty()) {
            log.warn("Configuration warnings: {}", warnings);
        }
    }

    private void testSmtpConnectivity(EmailDiagnosticResult result) {
        log.info("Testing SMTP connectivity...");
        
        if (!result.isConfigurationValid()) {
            result.setConnectivityIssues(List.of("Cannot test connectivity due to configuration issues"));
            result.setConnectivitySuccessful(false);
            return;
        }
        
        try {
            // Create a session with the current properties
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", smtpAuth);
            props.put("mail.smtp.starttls.enable", starttlsEnable);
            props.put("mail.smtp.starttls.required", starttlsEnable);
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            
            Session session = Session.getInstance(props);
            
            // Test connection
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, smtpPort, smtpUsername, smtpPassword);
            transport.close();
            
            result.setConnectivitySuccessful(true);
            result.setConnectivityIssues(new ArrayList<>());
            log.info("SMTP connectivity test successful");
            
        } catch (MessagingException e) {
            result.setConnectivitySuccessful(false);
            String errorMessage = "SMTP connectivity failed: " + e.getMessage();
            result.setConnectivityIssues(List.of(errorMessage));
            log.error("SMTP connectivity test failed", e);
            
            // Provide specific guidance based on error type
            if (e.getMessage().contains("Authentication failed")) {
                result.getConnectivityIssues().add("Suggestion: Check if you're using an App Password for Gmail (not your regular password)");
            } else if (e.getMessage().contains("Connection timed out")) {
                result.getConnectivityIssues().add("Suggestion: Check network connectivity and firewall settings");
            } else if (e.getMessage().contains("Unknown host")) {
                result.getConnectivityIssues().add("Suggestion: Verify SMTP host configuration");
            }
        }
    }

    private void testEmailSending(EmailDiagnosticResult result) {
        log.info("Testing email sending...");
        
        if (!result.isConnectivitySuccessful()) {
            result.setEmailSendingIssues(List.of("Cannot test email sending due to connectivity issues"));
            result.setEmailSendingSuccessful(false);
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(adminEmail); // Send test email to admin
            message.setSubject("Email Configuration Test - " + new Date());
            message.setText("This is a test email to verify email configuration is working correctly.\n\n" +
                          "If you receive this email, the email service is functioning properly.\n\n" +
                          "Timestamp: " + new Date() + "\n" +
                          "From: Email Diagnostic Service");

            mailSender.send(message);
            
            result.setEmailSendingSuccessful(true);
            result.setEmailSendingIssues(new ArrayList<>());
            log.info("Test email sent successfully to: {}", adminEmail);
            
        } catch (Exception e) {
            result.setEmailSendingSuccessful(false);
            String errorMessage = "Email sending failed: " + e.getMessage();
            result.setEmailSendingIssues(List.of(errorMessage));
            log.error("Email sending test failed", e);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return username + "@" + domain;
        }
        
        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@" + domain;
    }

    /**
     * Result class for email diagnostic information
     */
    public static class EmailDiagnosticResult {
        private boolean configurationValid;
        private List<String> configurationIssues = new ArrayList<>();
        private List<String> configurationWarnings = new ArrayList<>();
        
        private boolean connectivitySuccessful;
        private List<String> connectivityIssues = new ArrayList<>();
        
        private boolean emailSendingSuccessful;
        private List<String> emailSendingIssues = new ArrayList<>();
        
        public boolean isOverallHealthy() {
            return configurationValid && connectivitySuccessful && emailSendingSuccessful;
        }
        
        // Getters and setters
        public boolean isConfigurationValid() { return configurationValid; }
        public void setConfigurationValid(boolean configurationValid) { this.configurationValid = configurationValid; }
        
        public List<String> getConfigurationIssues() { return configurationIssues; }
        public void setConfigurationIssues(List<String> configurationIssues) { this.configurationIssues = configurationIssues; }
        
        public List<String> getConfigurationWarnings() { return configurationWarnings; }
        public void setConfigurationWarnings(List<String> configurationWarnings) { this.configurationWarnings = configurationWarnings; }
        
        public boolean isConnectivitySuccessful() { return connectivitySuccessful; }
        public void setConnectivitySuccessful(boolean connectivitySuccessful) { this.connectivitySuccessful = connectivitySuccessful; }
        
        public List<String> getConnectivityIssues() { return connectivityIssues; }
        public void setConnectivityIssues(List<String> connectivityIssues) { this.connectivityIssues = connectivityIssues; }
        
        public boolean isEmailSendingSuccessful() { return emailSendingSuccessful; }
        public void setEmailSendingSuccessful(boolean emailSendingSuccessful) { this.emailSendingSuccessful = emailSendingSuccessful; }
        
        public List<String> getEmailSendingIssues() { return emailSendingIssues; }
        public void setEmailSendingIssues(List<String> emailSendingIssues) { this.emailSendingIssues = emailSendingIssues; }
    }
}