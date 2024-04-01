package com.studyverse.server.Controller;

import com.studyverse.server.DAO.TestDAO;
import com.studyverse.server.Model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private TestDAO testDAO;

    @GetMapping("/{familyId}")
    public Map<Integer, List<Test>> getFamilyTests(@PathVariable("familyId") Integer familyId) {
        return testDAO.getAllTests(familyId);
    }

    @PostMapping("/createTest")
    public Map<String, String> createTest(@RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", testDAO.createTest(body) ? "1" : "0");

        return response;
    }
}
