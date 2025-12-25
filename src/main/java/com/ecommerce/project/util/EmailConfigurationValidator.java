package com.ecommerce.project.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to validate email configuration at startup
 * This component validates all email-related configuration properties
 */
@Component
@Slf4j
public class EmailConfigurationValidator {

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

    @PostConstruct
    public void validateConfiguration() {
        log.info("=== EMAIL CONFIGURATION VALIDATION ===");
        
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        // Validate SMTP configuration
        validateSmtpConfiguration(errors, warnings);
        
        // Validate email addresses
        validateEmailAddresses(errors, warnings);
        
        // Validate security settings
        validateSecuritySettings(errors, warnings);
        
        // Log results
        logValidationResults(errors, warnings);
    }

    private void validateSmtpConfiguration(List<String> errors, List<String> warnings) {
        log.info("Validating SMTP configuration...");
        
        if (smtpHost == null || smtpHost.trim().isEmpty()) {
            errors.add("SMTP host is not configured (spring.mail.host)");
        } else {
            log.info("✓ SMTP Host: {}", smtpHost);
            if (!"smtp.gmail.com".equals(smtpHost)) {
                warnings.add("Using non-Gmail SMTP server: " + smtpHost);
            }
        }
        
        if (smtpPort <= 0) {
            errors.add("SMTP port is not configured or invalid (spring.mail.port)");
        } else {
            log.info("✓ SMTP Port: {}", smtpPort);
            if (smtpPort != 587 && smtpPort != 465) {
                warnings.add("Using non-standard SMTP port: " + smtpPort + " (Gmail uses 587 or 465)");
            }
        }
        
        if (smtpUsername == null || smtpUsername.trim().isEmpty()) {
            errors.add("SMTP username is not configured (spring.mail.username)");
        } else {
            log.info("✓ SMTP Username: {}", maskEmail(smtpUsername));
        }
        
        if (smtpPassword == null || smtpPassword.trim().isEmpty()) {
            errors.add("SMTP password is not configured (spring.mail.password)");
        } else {
            log.info("✓ SMTP Password: [CONFIGURED - {} characters]", smtpPassword.length());
            
            // Check if it looks like a Gmail App Password
            if (smtpPassword.length() == 16 && smtpPassword.matches("[a-z]+")) {
                log.info("✓ Password appears to be a Gmail App Password");
            } else if (smtpPassword.length() != 16) {
                warnings.add("Password length is " + smtpPassword.length() + " characters. Gmail App Passwords are typically 16 characters.");
            }
        }
    }

    private void validateEmailAddresses(List<String> errors, List<String> warnings) {
        log.info("Validating email addresses...");
        
        if (fromEmail == null || fromEmail.trim().isEmpty()) {
            errors.add("From email address is not configured (email.from)");
        } else if (!isValidEmail(fromEmail)) {
            errors.add("From email address format is invalid: " + fromEmail);
        } else {
            log.info("✓ From Email: {}", fromEmail);
        }
        
        if (adminEmail == null || adminEmail.trim().isEmpty()) {
            errors.add("Admin email address is not configured (email.admin)");
        } else if (!isValidEmail(adminEmail)) {
            errors.add("Admin email address format is invalid: " + adminEmail);
        } else {
            log.info("✓ Admin Email: {}", adminEmail);
        }
        
        if (fromEmail != null && adminEmail != null && fromEmail.equals(adminEmail)) {
            warnings.add("From email and admin email are the same. This is acceptable for testing but consider using different emails in production.");
        }
    }

    private void validateSecuritySettings(List<String> errors, List<String> warnings) {
        log.info("Validating security settings...");
        
        log.info("✓ SMTP Auth: {}", smtpAuth);
        log.info("✓ STARTTLS Enable: {}", starttlsEnable);
        
        if (!smtpAuth) {
            warnings.add("SMTP authentication is disabled - this may cause issues with Gmail");
        }
        
        if (!starttlsEnable) {
            warnings.add("STARTTLS is disabled - this may cause security issues with Gmail");
        }
        
        // Gmail-specific validations
        if ("smtp.gmail.com".equals(smtpHost)) {
            if (!smtpAuth) {
                errors.add("Gmail requires SMTP authentication to be enabled");
            }
            if (!starttlsEnable) {
                errors.add("Gmail requires STARTTLS to be enabled");
            }
            if (smtpPort != 587 && smtpPort != 465) {
                warnings.add("Gmail typically uses port 587 (STARTTLS) or 465 (SSL). Current port: " + smtpPort);
            }
        }
    }

    private void logValidationResults(List<String> errors, List<String> warnings) {
        log.info("=== VALIDATION RESULTS ===");
        
        if (errors.isEmpty() && warnings.isEmpty()) {
            log.info("✅ EMAIL CONFIGURATION IS VALID - No issues found");
        } else {
            if (!errors.isEmpty()) {
                log.error("❌ CONFIGURATION ERRORS FOUND:");
                for (int i = 0; i < errors.size(); i++) {
                    log.error("   {}. {}", i + 1, errors.get(i));
                }
            }
            
            if (!warnings.isEmpty()) {
                log.warn("⚠️ CONFIGURATION WARNINGS:");
                for (int i = 0; i < warnings.size(); i++) {
                    log.warn("   {}. {}", i + 1, warnings.get(i));
                }
            }
        }
        
        // Provide remediation suggestions
        if (!errors.isEmpty()) {
            log.error("=== REMEDIATION SUGGESTIONS ===");
            log.error("1. Check your .env file and ensure all EMAIL_* variables are set");
            log.error("2. For Gmail, ensure you're using an App Password, not your regular password");
            log.error("3. Verify that 2-Factor Authentication is enabled on your Gmail account");
            log.error("4. Check that the Gmail account allows 'Less secure app access' if needed");
        }
        
        log.info("=== END EMAIL CONFIGURATION VALIDATION ===");
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
     * Get validation status for external use
     */
    public ValidationResult getValidationResult() {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        
        validateSmtpConfiguration(errors, warnings);
        validateEmailAddresses(errors, warnings);
        validateSecuritySettings(errors, warnings);
        
        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return errors; }
        public List<String> getWarnings() { return warnings; }
    }
}