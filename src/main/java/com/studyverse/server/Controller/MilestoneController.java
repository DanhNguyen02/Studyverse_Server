package com.studyverse.server.Controller;

import com.studyverse.server.DAO.MilestoneDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/milestone")
public class MilestoneController {
    @Autowired
    private MilestoneDAO milestoneDAO;

    @PostMapping("/add")
    public Map<String, String> addMilestone(@RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", milestoneDAO.addMilestone(body) ? "1" : "0");

        return response;
    }

    @PutMapping("/{id}")
    public Map<String, String> updateMilestone(@PathVariable("id") Integer id, @RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", milestoneDAO.updateMilestone(id, body) ? "1" : "0");

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteMilestone(@PathVariable("id") Integer id) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", milestoneDAO.deleteMilestone(id) ? "1" : "0");

        return response;
    }

    @PostMapping("/complete")
    public Map<String, String> deleteMilestone(@RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        Integer id = (Integer) body.get("id");
        Integer childrenId = (Integer) body.get("childrenId");

        response.put("msg", milestoneDAO.completeMilestone(id, childrenId) ? "1" : "0");

        return response;
    }
}
