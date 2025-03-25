package com.loosers.org.splitExpenses.service;

import com.loosers.org.splitExpenses.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.loosers.org.splitExpenses.model.User;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void addUser(User user) {
        userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId){
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new RuntimeException("User with ID " + userId + " not found.");
        }
        return foundUser.get();
    }

    public List<User> getUsersById(List<String> userIds) {
        List<User> users = new ArrayList<>();
        for(String userId: userIds) {
            User user = getUserById(userId);
            users.add(user);
        }
        return users;
    }
}
