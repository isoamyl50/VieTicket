package com.example.VieTicketSystem.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.example.VieTicketSystem.model.service.PasswordResetService;
import com.example.VieTicketSystem.model.service.VerifyEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final ObjectMapper mapper;
    private final VerifyEmailService verifyEmailService;

    // Inject the PasswordResetService and ObjectMapper here
    public PasswordResetController(PasswordResetService passwordResetService, ObjectMapper mapper,
            VerifyEmailService verifyEmailService) {
        this.passwordResetService = passwordResetService;
        this.mapper = mapper;
        this.verifyEmailService = verifyEmailService;
    }

    @PostMapping("/auth/password-reset")
    public ResponseEntity<?> passwordReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String returnedFromService;
        if (email == null) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("success", false);
            errorNode.put("message", "Email is required");
            return ResponseEntity.badRequest().body(errorNode);
        }

        try {
            // Handle the password reset request
            if (verifyEmailService.isUnverified(email)) {
                returnedFromService = verifyEmailService.sendOTP(email);
            } else {
                returnedFromService = passwordResetService.handleFormSubmission(email);
            }
        } catch (Exception e) {
            // Handle the exception here
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("success", false);
            errorNode.put("message", e.getLocalizedMessage());
            // errorNode.put("message",
            // "An error occurred while processing the request. Please recheck your input
            // and try again later.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorNode);
        }

        ObjectNode successNode = mapper.createObjectNode();
        successNode.put("success", true);
        successNode.put("message", returnedFromService);
        return ResponseEntity.ok().body(successNode);
    }

    @PostMapping("/auth/verify-otp")
    public ResponseEntity<?> verifyOTP(@RequestBody Map<String, String> body) {
        String otp = body.get("otp");
        String email = body.get("email");
        if (otp == null || email == null) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("success", false);
            errorNode.put("message", "OTP is required");
            return ResponseEntity.badRequest().body(errorNode);
        }
        String resetToken = body.get("resetToken");
        try {
            // Verify the OTP
            if (verifyEmailService.isUnverified(email)) {
                verifyEmailService.validateNewUserOTP(email, otp);
            } else {
                resetToken = passwordResetService.verifyOTP(email, otp);
            }
        } catch (Exception e) {
            // Handle the exception here
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("success", false);
            errorNode.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorNode);
        }

        ObjectNode successNode = mapper.createObjectNode();
        successNode.put("success", true);
        successNode.put("message", "OTP verified successfully");
        successNode.put("token", resetToken);
        return ResponseEntity.ok().body(successNode);
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String newPassword = body.get("newPassword");

        if (token == null || newPassword == null) {
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("success", false);
            errorNode.put("message", "Token and new password are required");
            return ResponseEntity.badRequest().body(errorNode);
        }

        try {
            // Reset the password
            passwordResetService.resetPassword(token, newPassword);
        } catch (Exception e) {
            // Handle the exception here
            ObjectNode errorNode = mapper.createObjectNode();
            errorNode.put("success", false);
            errorNode.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorNode);
        }

        ObjectNode successNode = mapper.createObjectNode();
        successNode.put("success", true);
        successNode.put("message", "Password reset successfully");
        return ResponseEntity.ok().body(successNode);
    }
}