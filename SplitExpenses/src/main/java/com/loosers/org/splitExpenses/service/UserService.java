package com.loosers.org.splitExpenses.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.loosers.org.splitExpenses.model.User;

@Service
public class UserService {
    List<User> users;
    HashMap<String, User> userMap = new HashMap<>();

    public void addUser(User user) {
        users.add(user);
        userMap.put(user.getEmail(),user);
    }

    public List<User> getUsers() {
        return users;
    }

    public HashMap<String, User> getUserMap() {
        return userMap;
    }

    public User getUserById(String userId){
        Optional<User> foundUser = Optional.ofNullable(userMap.get(userId));

        if (foundUser.isEmpty()) {
            throw new RuntimeException("User with ID " + userId + " not found.");
        }
        return foundUser.get();
    }

}
