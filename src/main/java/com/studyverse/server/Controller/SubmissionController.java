package com.studyverse.server.Controller;

import com.studyverse.server.DAO.SubmissionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/submission")
public class SubmissionController {
    @Autowired
    private SubmissionDAO submissionDAO;
    @PostMapping("/scoring")
    public Map<String, String> scoringTest(@RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", submissionDAO.scoringTest(body) ? "1" : "0");

        return response;
    }
}
