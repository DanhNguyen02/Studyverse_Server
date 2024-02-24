package com.studyverse.server.Controller;

import com.studyverse.server.DAO.UserDAO;
import com.studyverse.server.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserDAO userDAO;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @PostMapping("/login")
    public Map<String, String> logIn(@RequestBody HashMap<String, String> user) {
        System.out.println(user);
        Map<String, String> response = new HashMap<>();
        response.put("msg", userDAO.handleLogIn(user.get("email"), user.get("password")) ? "1" : "0");
        return response;
    }

    @PostMapping("/signup")
    public Map<String, String> signUp(@RequestBody HashMap<String, String> user) {
        Map<String, String> response = new HashMap<>();
        response.put("msg", userDAO.handleSignUp(user) ? "1" : "0");
        return response;
    }
}
