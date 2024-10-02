package com.invest.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.invest.model.User;
import com.invest.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender mailSender;
    // Register a new user (Admins register Investment Advisors, Public can register Investors)
    public User registerUser(User user, String creatorRole) {
        // Check if the email already exists
    	 if (userRepository.findByEmail(user.getEmail()).isPresent()) {
             throw new RuntimeException("User with this email already exists");
         }

        // Investors can register without admin intervention
        if ("INVESTOR".equals(user.getRole())) {
            user.setWalletBalance(user.getWalletBalance() == null ? 0.0 : user.getWalletBalance());
        } else if ("INVESTMENT_ADVISOR".equals(user.getRole()) && !"ADMIN".equals(creatorRole)) {
            throw new RuntimeException("Only Admins can register Investment Advisors");
        }

        return userRepository.save(user);
    }
    // Change email for a user
    public User changeEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(newEmail);
        return userRepository.save(user);
    }
    // Change password for a user
    public User changePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPassword(newPassword);  // Password should ideally be hashed
        return userRepository.save(user);
    }
    // Add wallet balance for a user
    public User addWalletBalance(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Initialize wallet balance if it's null
        if (user.getWalletBalance() == null) {
            user.setWalletBalance(0.0);
        }
        // Add the amount to the wallet balance
        user.setWalletBalance(user.getWalletBalance() + amount);
        return userRepository.save(user);
    }
    // Login logic
    public User login(String email, String password) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new RuntimeException("Invalid login credentials"));
    }
    // Reset password functionality
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Generate a random password
        String randomPassword = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(randomPassword);  // Set new password
        // Save the user with the updated password
        userRepository.save(user);
        // Send email notification with the new password
        sendEmail(user.getEmail(), "Password Reset", "Your new password: " + randomPassword);
    }
    // Helper method to send emails
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    // Remove an Investment Advisor
    public boolean removeInvestmentAdvisor(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Check if the user is an Investment Advisor
        if ("INVESTMENT_ADVISOR".equals(user.getRole())) {
            userRepository.delete(user);
            return true; // Successfully deleted
        } else {
            return false; // Not an Investment Advisor
        }
    }
    
    public Double checkWalletBalance(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Investor not found"));
        if (!"INVESTOR".equals(user.getRole())) {
            throw new RuntimeException("Only investors have a wallet balance");
        }
        // Log the user's balance to ensure it's correctly retrieved
        System.out.println("User's wallet balance: " + user.getWalletBalance());
        return user.getWalletBalance();
    }
 // Method to deduct wallet balance for a user
    public User deductWalletBalance(Long userId, Double amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        // Initialize wallet balance if it's null
        if (user.getWalletBalance() == null) {
            user.setWalletBalance(0.0);
        }
        // Check if sufficient balance is available
        if (user.getWalletBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        // Deduct the amount from the wallet balance
        user.setWalletBalance(user.getWalletBalance() - amount);
        return userRepository.save(user);
    }
    
 // Fetch all Investment Advisors
    public List<User> getAllInvestmentAdvisors() {
        return userRepository.findByRole("INVESTMENT_ADVISOR");
    }

}