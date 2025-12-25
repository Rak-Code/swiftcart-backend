package com.ecommerce.project.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to validate email configuration properties
 * This test verifies that all required email configuration properties are properly loaded
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
class EmailConfigurationTest {

    @MockBean
    private JavaMailSender mailSender;

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
    void testEmailConfigurationPropertiesAreLoaded() {
        // Test SMTP configuration
        assertNotNull(smtpHost, "SMTP host should not be null");
        assertFalse(smtpHost.trim().isEmpty(), "SMTP host should not be empty");
        assertEquals("smtp.gmail.com", smtpHost, "SMTP host should be Gmail SMTP server");

        assertTrue(smtpPort > 0, "SMTP port should be greater than 0");
        assertEquals(587, smtpPort, "SMTP port should be 587 for Gmail STARTTLS");

        assertNotNull(smtpUsername, "SMTP username should not be null");
        assertFalse(smtpUsername.trim().isEmpty(), "SMTP username should not be empty");

        assertNotNull(smtpPassword, "SMTP password should not be null");
        assertFalse(smtpPassword.trim().isEmpty(), "SMTP password should not be empty");

        // Test email addresses
        assertNotNull(fromEmail, "From email should not be null");
        assertFalse(fromEmail.trim().isEmpty(), "From email should not be empty");
        assertTrue(isValidEmail(fromEmail), "From email should be valid format");

        assertNotNull(adminEmail, "Admin email should not be null");
        assertFalse(adminEmail.trim().isEmpty(), "Admin email should not be empty");
        assertTrue(isValidEmail(adminEmail), "Admin email should be valid format");

        // Test SMTP security settings
        assertTrue(smtpAuth, "SMTP authentication should be enabled");
        assertTrue(starttlsEnable, "STARTTLS should be enabled for Gmail");
    }

    @Test
    void testEmailAddressFormats() {
        assertTrue(isValidEmail(fromEmail), "From email should have valid format: " + fromEmail);
        assertTrue(isValidEmail(adminEmail), "Admin email should have valid format: " + adminEmail);
    }

    @Test
    void testGmailSpecificConfiguration() {
        // Gmail-specific validations
        assertEquals("smtp.gmail.com", smtpHost, "Should use Gmail SMTP server");
        assertEquals(587, smtpPort, "Should use port 587 for Gmail STARTTLS");
        assertTrue(smtpAuth, "Gmail requires authentication");
        assertTrue(starttlsEnable, "Gmail requires STARTTLS");
    }

    @Test
    void testPasswordFormat() {
        // Gmail App Password should be 16 characters
        if (smtpPassword.length() == 16) {
            // Likely an App Password - should not contain spaces
            assertFalse(smtpPassword.contains(" "), "App Password should not contain spaces");
        }
        
        // Password should not be a common default
        assertNotEquals("password", smtpPassword.toLowerCase(), "Should not use default password");
        assertNotEquals("123456", smtpPassword, "Should not use weak password");
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}