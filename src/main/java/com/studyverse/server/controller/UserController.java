package com.studyverse.server.controller;

import com.studyverse.server.model.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class UserController {
    @PostMapping("/login")
    public User test(@RequestBody User user) {
        return user;
    }
}
