package com.FarmMinds.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private DAO dao;

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signUpUser(@Validated @RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return new ResponseEntity<>("Sign-up successful", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestParam("email") String email) {
        String response = dao.deleteUser(email);
        if ("User deleted successfully".equals(response)) {
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("Failed to delete user", HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserDTO userDTO) {
        try {
            User user = dao.findUser(userDTO.getEmail()); // Ensure user exists
            if (user != null) {
                // Update fields from DTO, optional fields like password
                if (userDTO.getPassword() != null) {
                    user.setPassword(userDTO.getPassword()); // Update password if provided
                }
                user.setName(userDTO.getName());
                user.setGovtId(userDTO.getGovtId());
                user.setAddress(userDTO.getAddress());
                user.setPhoneNumber(userDTO.getPhoneNumber());

                // Update user in database
                dao.updateUser(user);
                return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
            }
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Log the error for troubleshooting
            e.printStackTrace();
            return new ResponseEntity<>("Error updating user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signInUser(@RequestBody User user) {
        if (userService.loginUser(user.getEmail(), user.getPassword())) {
            return new ResponseEntity<>("Login successful", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@Validated @RequestBody UserDTO userDTO) {
        try {
            User newUser = new User();
            newUser.setEmail(userDTO.getEmail());
            newUser.setName(userDTO.getName());
            newUser.setPassword(userDTO.getPassword());
            newUser.setUserType(userDTO.getUserType());
            newUser.setAddress(userDTO.getAddress());

            // If user is a Farmer, add additional fields
            if (userDTO.getUserType().equalsIgnoreCase("Artisian")) {
                newUser.setGovtId(userDTO.getGovtId());
                newUser.setPhoneNumber(userDTO.getPhoneNumber());
            }

            userService.registerUser(userDTO);
            return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error adding user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
