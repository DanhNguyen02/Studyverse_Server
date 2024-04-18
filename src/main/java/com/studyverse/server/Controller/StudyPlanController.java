package com.studyverse.server.Controller;

import com.studyverse.server.DAO.StudyPlanDAO;
import com.studyverse.server.Model.StudyPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/studyPlan")
public class StudyPlanController {
    @Autowired
    private StudyPlanDAO studyPlanDAO;

    @GetMapping("/{familyId}")
    public Map<Integer, Map<Integer, List<StudyPlan>>> getAllStudyPlans(@PathVariable("familyId") Integer familyId) {
        return studyPlanDAO.getAllStudyPlans(familyId);
    }

    @PostMapping("/create")
    public Map<String, String> createStudyPlan(@RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", studyPlanDAO.createStudyPlan(body) ? "1" : "0");

        return response;
    }

    @PutMapping("/{id}")
    public Map<String, String> updateStudyPlan(@PathVariable("id") Integer id, @RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", studyPlanDAO.updateStudyPlan(id, body) ? "1" : "0");

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteStudyPlan(@PathVariable("id") Integer id) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", studyPlanDAO.deleteStudyPlan(id) ? "1" : "0");

        return response;
    }
}
