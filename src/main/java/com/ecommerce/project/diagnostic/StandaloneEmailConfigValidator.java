package com.ecommerce.project.diagnostic;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.Session;
import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Standalone email configuration validator that can run without Spring context
 * Useful for diagnosing email issues when the application fails to start
 */
public class StandaloneEmailConfigValidator {

    public static void main(String[] args) {
        System.out.println("=== STANDALONE EMAIL CONFIGURATION VALIDATOR ===");
        System.out.println();
        
        StandaloneEmailConfigValidator validator = new StandaloneEmailConfigValidator();
        validator.validateEmailConfiguration();
    }

    public void validateEmailConfiguration() {
        // Load environment variables
        Map<String, String> config = loadConfiguration();
        
        // 1. Check configuration properties
        System.out.println("1. CONFIGURATION PROPERTIES:");
        boolean configValid = checkConfigurationProperties(config);
        System.out.println();

        // 2. Test SMTP connectivity (only if configuration is complete)
        if (configValid) {
            System.out.println("2. SMTP CONNECTIVITY TEST:");
            testSmtpConnectivity(config);
        } else {
            System.out.println("2. SMTP CONNECTIVITY TEST: SKIPPED (configuration issues found)");
        }
        System.out.println();

        // 3. Provide recommendations
        System.out.println("3. RECOMMENDATIONS:");
        provideRecommendations(configValid);
        
        System.out.println();
        System.out.println("=== VALIDATION COMPLETE ===");
    }

    private Map<String, String> loadConfiguration() {
        Map<String, String> config = new HashMap<>();
        
        // Try to load from .env file
        try {
            Properties envProps = new Properties();
            envProps.load(new FileInputStream(".env"));
            
            for (String key : envProps.stringPropertyNames()) {
                config.put(key, envProps.getProperty(key));
            }
            System.out.println("✅ Loaded configuration from .env file");
        } catch (IOException e) {
            System.out.println("⚠️  Could not load .env file: " + e.getMessage());
        }
        
        // Override with system environment variables
        Map<String, String> envVars = System.getenv();
        for (String key : Arrays.asList("EMAIL_USER", "EMAIL_PASS", "SMTP_HOST", "SMTP_PORT", 
                                       "FROM_EMAIL", "ADMIN_EMAIL")) {
            if (envVars.containsKey(key)) {
                config.put(key, envVars.get(key));
            }
        }
        
        return config;
    }

    private boolean checkConfigurationProperties(Map<String, String> config) {
        boolean hasIssues = false;
        
        String smtpHost = config.getOrDefault("SMTP_HOST", "");
        String smtpPort = config.getOrDefault("SMTP_PORT", "");
        String emailUser = config.getOrDefault("EMAIL_USER", "");
        String emailPass = config.getOrDefault("EMAIL_PASS", "");
        String fromEmail = config.getOrDefault("FROM_EMAIL", "");
        String adminEmail = config.getOrDefault("ADMIN_EMAIL", "");
        
        System.out.println("   SMTP Host: " + (smtpHost.isEmpty() ? "NOT CONFIGURED" : smtpHost));
        System.out.println("   SMTP Port: " + (smtpPort.isEmpty() ? "NOT CONFIGURED" : smtpPort));
        System.out.println("   Email User: " + (emailUser.isEmpty() ? "NOT CONFIGURED" : maskEmail(emailUser)));
        System.out.println("   Email Pass: " + (emailPass.isEmpty() ? "NOT CONFIGURED" : "[CONFIGURED - " + emailPass.length() + " characters]"));
        System.out.println("   From Email: " + (fromEmail.isEmpty() ? "NOT CONFIGURED" : fromEmail));
        System.out.println("   Admin Email: " + (adminEmail.isEmpty() ? "NOT CONFIGURED" : adminEmail));
        System.out.println();

        System.out.println("   CONFIGURATION ISSUES:");
        
        if (smtpHost.isEmpty()) {
            System.out.println("   ❌ SMTP host is not configured");
            hasIssues = true;
        }
        if (smtpPort.isEmpty()) {
            System.out.println("   ❌ SMTP port is not configured");
            hasIssues = true;
        }
        if (emailUser.isEmpty()) {
            System.out.println("   ❌ Email user is not configured");
            hasIssues = true;
        }
        if (emailPass.isEmpty()) {
            System.out.println("   ❌ Email password is not configured");
            hasIssues = true;
        }
        if (fromEmail.isEmpty()) {
            System.out.println("   ❌ From email is not configured");
            hasIssues = true;
        }
        if (adminEmail.isEmpty()) {
            System.out.println("   ❌ Admin email is not configured");
            hasIssues = true;
        }
        
        // Validate email formats
        if (!fromEmail.isEmpty() && !isValidEmail(fromEmail)) {
            System.out.println("   ❌ From email format is invalid: " + fromEmail);
            hasIssues = true;
        }
        if (!adminEmail.isEmpty() && !isValidEmail(adminEmail)) {
            System.out.println("   ❌ Admin email format is invalid: " + adminEmail);
            hasIssues = true;
        }
        
        if (!hasIssues) {
            System.out.println("   ✅ All required configuration properties are set and valid");
        }
        
        return !hasIssues;
    }

    private void testSmtpConnectivity(Map<String, String> config) {
        String smtpHost = config.get("SMTP_HOST");
        int smtpPort = Integer.parseInt(config.getOrDefault("SMTP_PORT", "587"));
        String emailUser = config.get("EMAIL_USER");
        String emailPass = config.get("EMAIL_PASS");
        
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");

            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");
            
            System.out.println("   Attempting to connect to " + smtpHost + ":" + smtpPort + "...");
            transport.connect(smtpHost, smtpPort, emailUser, emailPass);
            transport.close();

            System.out.println("   ✅ SMTP connectivity successful");
            System.out.println("   ✅ Authentication successful");
        } catch (MessagingException e) {
            System.out.println("   ❌ SMTP connectivity failed: " + e.getMessage());
            
            // Provide specific guidance
            String errorMsg = e.getMessage().toLowerCase();
            if (errorMsg.contains("authentication failed") || errorMsg.contains("invalid credentials")) {
                System.out.println("   💡 Suggestion: Check if you're using an App Password for Gmail");
                System.out.println("   💡 Generate new App Password: https://support.google.com/accounts/answer/185833");
            } else if (errorMsg.contains("connection timed out") || errorMsg.contains("connection refused")) {
                System.out.println("   💡 Suggestion: Check network connectivity and firewall settings");
                System.out.println("   💡 Verify SMTP server is accessible from your network");
            } else if (errorMsg.contains("unknown host")) {
                System.out.println("   💡 Suggestion: Verify SMTP host configuration");
            } else if (errorMsg.contains("ssl") || errorMsg.contains("tls")) {
                System.out.println("   💡 Suggestion: Check SSL/TLS configuration");
            }
        }
    }

    private void provideRecommendations(boolean configValid) {
        if (!configValid) {
            System.out.println("   📋 Fix the configuration issues listed above");
            System.out.println("   📋 Ensure all environment variables are properly set in .env file");
            System.out.println("   📋 For Gmail, use App Password instead of regular password");
            System.out.println("   📋 Verify that EMAIL_USER, EMAIL_PASS, FROM_EMAIL, and ADMIN_EMAIL are set");
        } else {
            System.out.println("   📋 Configuration appears complete");
            System.out.println("   📋 If connectivity test passed, email service should work");
            System.out.println("   📋 Test email sending functionality through the application");
            System.out.println("   📋 Check application logs for runtime errors");
        }
        
        System.out.println();
        System.out.println("   NEXT STEPS:");
        System.out.println("   1. Fix MongoDB connection issue to allow application startup");
        System.out.println("   2. Test email diagnostic endpoint: /api/diagnostic/email");
        System.out.println("   3. Place a test order to verify email triggering");
        System.out.println("   4. Monitor application logs for email-related errors");
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
}