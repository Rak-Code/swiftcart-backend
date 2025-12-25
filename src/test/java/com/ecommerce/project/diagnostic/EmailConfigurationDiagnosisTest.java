package com.ecommerce.project.diagnostic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import jakarta.mail.MessagingException;
import jakarta.mail.Transport;
import jakarta.mail.Session;
import java.util.Properties;

/**
 * Test class to diagnose email configuration issues
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
public class EmailConfigurationDiagnosisTest {

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

    @Test
    public void diagnoseEmailConfiguration() {
        System.out.println("=== EMAIL CONFIGURATION DIAGNOSIS ===");
        System.out.println();
        
        // 1. Check configuration properties
        System.out.println("1. CONFIGURATION PROPERTIES:");
        System.out.println("   SMTP Host: " + (smtpHost.isEmpty() ? "NOT CONFIGURED" : smtpHost));
        System.out.println("   SMTP Port: " + (smtpPort == 0 ? "NOT CONFIGURED" : smtpPort));
        System.out.println("   SMTP Username: " + (smtpUsername.isEmpty() ? "NOT CONFIGURED" : maskEmail(smtpUsername)));
        System.out.println("   SMTP Password: " + (smtpPassword.isEmpty() ? "NOT CONFIGURED" : "[CONFIGURED - " + smtpPassword.length() + " characters]"));
        System.out.println("   From Email: " + (fromEmail.isEmpty() ? "NOT CONFIGURED" : fromEmail));
        System.out.println("   Admin Email: " + (adminEmail.isEmpty() ? "NOT CONFIGURED" : adminEmail));
        System.out.println("   SMTP Auth: " + smtpAuth);
        System.out.println("   STARTTLS Enable: " + starttlsEnable);
        System.out.println();

        // 2. Identify configuration issues
        System.out.println("2. CONFIGURATION ISSUES:");
        boolean hasIssues = false;
        
        if (smtpHost.isEmpty()) {
            System.out.println("   ❌ SMTP host is not configured");
            hasIssues = true;
        }
        if (smtpPort == 0) {
            System.out.println("   ❌ SMTP port is not configured");
            hasIssues = true;
        }
        if (smtpUsername.isEmpty()) {
            System.out.println("   ❌ SMTP username is not configured");
            hasIssues = true;
        }
        if (smtpPassword.isEmpty()) {
            System.out.println("   ❌ SMTP password is not configured");
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
        if (!smtpAuth) {
            System.out.println("   ⚠️  SMTP authentication is disabled");
        }
        if (!starttlsEnable) {
            System.out.println("   ⚠️  STARTTLS is disabled");
        }
        
        if (!hasIssues) {
            System.out.println("   ✅ All required configuration properties are set");
        }
        System.out.println();

        // 3. Test SMTP connectivity (only if configuration is complete)
        if (!hasIssues) {
            System.out.println("3. SMTP CONNECTIVITY TEST:");
            testSmtpConnectivity();
        } else {
            System.out.println("3. SMTP CONNECTIVITY TEST: SKIPPED (configuration issues found)");
        }
        System.out.println();

        // 4. Provide recommendations
        System.out.println("4. RECOMMENDATIONS:");
        if (hasIssues) {
            System.out.println("   📋 Fix the configuration issues listed above");
            System.out.println("   📋 Ensure all environment variables are properly set in .env file");
            System.out.println("   📋 For Gmail, use App Password instead of regular password");
            System.out.println("   📋 Verify that EMAIL_USER, EMAIL_PASS, FROM_EMAIL, and ADMIN_EMAIL are set");
        } else {
            System.out.println("   📋 Configuration appears complete");
            System.out.println("   📋 Test email sending functionality");
            System.out.println("   📋 Check application logs for runtime errors");
        }
        
        System.out.println();
        System.out.println("=== DIAGNOSIS COMPLETE ===");
    }

    private void testSmtpConnectivity() {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", smtpAuth);
            props.put("mail.smtp.starttls.enable", starttlsEnable);
            props.put("mail.smtp.starttls.required", starttlsEnable);
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");

            Session session = Session.getInstance(props);
            Transport transport = session.getTransport("smtp");
            transport.connect(smtpHost, smtpPort, smtpUsername, smtpPassword);
            transport.close();

            System.out.println("   ✅ SMTP connectivity successful");
        } catch (MessagingException e) {
            System.out.println("   ❌ SMTP connectivity failed: " + e.getMessage());
            
            // Provide specific guidance
            if (e.getMessage().contains("Authentication failed")) {
                System.out.println("   💡 Suggestion: Check if you're using an App Password for Gmail");
            } else if (e.getMessage().contains("Connection timed out")) {
                System.out.println("   💡 Suggestion: Check network connectivity and firewall settings");
            } else if (e.getMessage().contains("Unknown host")) {
                System.out.println("   💡 Suggestion: Verify SMTP host configuration");
            }
        }
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