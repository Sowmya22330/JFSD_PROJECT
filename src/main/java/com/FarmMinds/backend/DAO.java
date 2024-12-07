package com.FarmMinds.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DAO {

    @Autowired
    UserRepository repo;

    // Insert a new user
    public void insert(User user) {
        repo.save(user);
    }

    // Retrieve all users
    public List<User> retrieveAll() {
        return repo.findAll();
    }

    // Find a user by email
    public User findUser(String email) {
        return repo.findByEmail(email);
    }

    // Delete a user by email
    public String deleteUser(String email) {
        User user = findUser(email);
        if (user == null) {
            return "User not found: " + email;
        }
        repo.delete(user);
        return "Deleted user: " + email;
    }

    // Update user details
    public String updateUser(User user) {
        // Check if the user exists first
        User existingUser = findUser(user.getEmail());
        if (existingUser == null) {
            return "User not found: " + user.getEmail();
        }

        // Update fields if the user exists
        existingUser.setName(user.getName());
        existingUser.setPassword(user.getPassword());
        existingUser.setGovtId(user.getGovtId());
        existingUser.setAddress(user.getAddress());
        existingUser.setPhoneNumber(user.getPhoneNumber());

        // Save the updated user
        repo.save(existingUser);
        return "User updated successfully";
    }


    // Pagination method with sorting
    public List<User> page(int page, int limit) {
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        Pageable pageable = PageRequest.of(page, limit, sort);
        return repo.findAll(pageable).toList();
    }
}