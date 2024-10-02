package com.invest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.invest.model.User;
import com.invest.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    // Admin login method
    @PostMapping("/admin/login")
    public ResponseEntity<String> loginAdmin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        User admin = userService.login(email, password);
        if (!"ADMIN".equals(admin.getRole())) {
            return ResponseEntity.status(403).body("Access Denied. Only Admins can log in here.");
        }
        return ResponseEntity.ok("Admin login successful");
    }
 // Endpoint to deduct wallet balance for a user
    @PutMapping("/deduct-wallet/{userId}")
    public ResponseEntity<String> deductWalletBalance(@PathVariable Long userId, @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");
        try {
            userService.deductWalletBalance(userId, amount);
            return ResponseEntity.ok("Wallet balance deducted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    @GetMapping("/wallet-balance")
    public ResponseEntity<Double> getWalletBalance(@RequestParam String email) {
        try {
            Double walletBalance = userService.checkWalletBalance(email);
            return ResponseEntity.ok(walletBalance);
        } catch (Exception e) {
            System.err.println("Error fetching wallet balance: " + e.getMessage());
            return ResponseEntity.status(500).body(null);  // Or any relevant error message
        }
    }
    @PostMapping("/admin/register-advisor")
    public ResponseEntity<String> registerAdvisor(@RequestBody User advisor, @RequestHeader("role") String role) {
        try {
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).body("Only Admins can register Investment Advisors.");
            }
            advisor.setRole("INVESTMENT_ADVISOR");
            userService.registerUser(advisor, role);
            return ResponseEntity.ok("Investment Advisor registered successfully.");
        } catch (RuntimeException e) {
            // Catch the exception and return its message as the response
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            user.setRole("INVESTOR");
            if (user.getWalletBalance() == null) {
                user.setWalletBalance(0.0); // Default wallet balance
            }
            userService.registerUser(user, "INVESTOR");
            return ResponseEntity.ok("Investor registration successful");
        } catch (RuntimeException e) {
            // Catch the exception and return its message as the response
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Add wallet balance for a user
    @PutMapping("/add-wallet/{userId}")
    public ResponseEntity<String> addWalletBalance(@PathVariable Long userId, @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");
        userService.addWalletBalance(userId, amount);
        return ResponseEntity.ok("Wallet balance updated successfully");
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> emailRequest) {
        String email = emailRequest.get("email");
        userService.resetPassword(email);
        return ResponseEntity.ok("Password reset email sent");
    }
    @PostMapping("/advisor/login")
    public ResponseEntity<String> loginAdvisor(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        User advisor = userService.login(email, password);
        if (!"INVESTMENT_ADVISOR".equals(advisor.getRole())) {
            return ResponseEntity.status(403).body("Access Denied. Only Investment Advisors can log in here.");
        }
        return ResponseEntity.ok("Investment Advisor login successful");
    }
    @PutMapping("/change-email/{userId}")
    public ResponseEntity<String> changeEmail(@PathVariable Long userId, @RequestBody Map<String, String> emailRequest) {
        String newEmail = emailRequest.get("email");
        userService.changeEmail(userId, newEmail);
        return ResponseEntity.ok("Email updated successfully");
    }
    @PutMapping("/change-password/{userId}")
    public ResponseEntity<String> changePassword(@PathVariable Long userId, @RequestBody Map<String, String> passwordRequest) {
        String newPassword = passwordRequest.get("password");
        userService.changePassword(userId, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }
    @DeleteMapping("/admin/remove-advisor/{id}")
    public ResponseEntity<String> removeAdvisor(@PathVariable Long id, @RequestHeader("role") String role) {
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Only Admins can remove Investment Advisors.");
        }
        boolean removed = userService.removeInvestmentAdvisor(id);
        if (removed) {
            return ResponseEntity.ok("Investment Advisor removed successfully.");
        } else {
            return ResponseEntity.status(404).body("User is not an Investment Advisor or does not exist.");
        }
    }
    
 // Investor login method
    @PostMapping("/investor/login")
    public ResponseEntity<String> loginInvestor(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        try {
            User investor = userService.login(email, password);
            if (!"INVESTOR".equals(investor.getRole())) {
                return ResponseEntity.status(403).body("Access Denied. Only Investors can log in here.");
            }
            return ResponseEntity.ok("Investor login successful");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body("Invalid login credentials");
        }
    }
    
    
    
    
    
 // View all Investment Advisors (Admin Only)
    @GetMapping("/admin/advisors")
    public ResponseEntity<List<User>> getAllInvestmentAdvisors(@RequestHeader("role") String role) {
        // Check if the user performing the request is an admin
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).build();  // Access Denied
        }

        // Fetch and return the list of all Investment Advisors
        List<User> advisors = userService.getAllInvestmentAdvisors();
        return ResponseEntity.ok(advisors);
    }

}

