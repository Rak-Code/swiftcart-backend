package com.ecommerce.project.controller;

import com.ecommerce.project.diagnostic.EmailDiagnosticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for email diagnostic endpoints
 */
@RestController
@RequestMapping("/api/diagnostic")
@RequiredArgsConstructor
@Slf4j
public class EmailDiagnosticController {

    private final EmailDiagnosticService emailDiagnosticService;

    /**
     * Diagnose email configuration and connectivity
     */
    @GetMapping("/email")
    public ResponseEntity<EmailDiagnosticService.EmailDiagnosticResult> diagnoseEmail() {
        log.info("Email diagnostic endpoint called");
        
        try {
            EmailDiagnosticService.EmailDiagnosticResult result = emailDiagnosticService.diagnoseEmailConfiguration();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error during email diagnosis", e);
            
            // Create error result
            EmailDiagnosticService.EmailDiagnosticResult errorResult = new EmailDiagnosticService.EmailDiagnosticResult();
            errorResult.setConfigurationValid(false);
            errorResult.setConfigurationIssues(java.util.List.of("Diagnostic service error: " + e.getMessage()));
            errorResult.setConnectivitySuccessful(false);
            errorResult.setEmailSendingSuccessful(false);
            
            return ResponseEntity.ok(errorResult);
        }
    }
}