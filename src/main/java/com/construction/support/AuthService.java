package com.construction.support;
// Developed & Verified by Weerawansha K.H.H. (IT25103631).

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("An account with email " + user.getEmail() + " already exists!");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("CLIENT");
        }
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(password)) {
            throw new RuntimeException("Invalid email or password!");
        }
        return optionalUser.get();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(Long id, User updatedDetails) {
        Optional<User> opt = userRepository.findById(id);
        if (!opt.isPresent()) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        User user = opt.get();

        user.setFullName(updatedDetails.getFullName());
        user.setPhone(updatedDetails.getPhone());
        user.setAddress(updatedDetails.getAddress());
        if (updatedDetails.getRole() != null) {
            user.setRole(updatedDetails.getRole());
        }
        if (updatedDetails.getPassword() != null && !updatedDetails.getPassword().trim().isEmpty()) {
            user.setPassword(updatedDetails.getPassword());
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }
}
