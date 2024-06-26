package com.studyverse.server.Controller;

import com.studyverse.server.DAO.StatisticDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatisticController {
    @Autowired
    private StatisticDAO statisticDAO;

    @GetMapping("/{id}")
    public Map<String, Object> getStatistics(@PathVariable("id") Integer id) {
        Map<String, Object> response = new HashMap<>();

        Map<String, Map<String, Object>> statistics = statisticDAO.getStatistics(id);

        response.put("test", statistics.get("test"));
        response.put("answer", statistics.get("answer"));
        response.put("subject", statistics.get("subject"));

        return response;
    }
}
