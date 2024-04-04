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

    @PostMapping("/submit")
    public Map<String, String> submitTest(@RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", testDAO.submitTest(body) ? "1" : "0");

        return response;
    }

    @PutMapping("/{id}")
    public Map<String, String> updateTest(@PathVariable("id") Integer id, @RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", testDAO.updateTest(id, body) ? "1" : "0");

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteTest(@PathVariable("id") Integer id) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", testDAO.deleteTest(id) ? "1" : "0");

        return response;
    }
}
