package com.studyverse.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials = "true")
public class UserController {
//    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Map<String,String> login(@RequestBody HashMap<String,String> user) {
        Map<String,String> response = new HashMap<String,String>();
        response.put("hello", "loconcac");
        return response;
    }
}
