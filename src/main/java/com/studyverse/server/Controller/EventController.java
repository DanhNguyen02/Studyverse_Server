package com.studyverse.server.Controller;

import com.studyverse.server.DAO.EventDAO;
import com.studyverse.server.Model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventDAO eventDAO;

    @GetMapping("/events")
    public List<Event> getEvents() {
        return eventDAO.getEvents();
    }

    @GetMapping("/{userId}")
    public List<Event> getEventsUserJoin(@PathVariable("userId") Integer userId) {
        return eventDAO.getEventsUserJoin(userId);
    }

    @PostMapping("/createEvent")
    public Map<String, String> createEvent(@RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", eventDAO.createEvent(body) ? "1" : "0");

        return response;
    }

    @PutMapping("/{id}")
    public Map<String, String> updateEvents(@PathVariable("id") Integer id ,@RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", eventDAO.updateEvents(id, body) ? "1" : "0");

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteEvent(@PathVariable("id") Integer id, @RequestBody HashMap<String, Object> body) {
        Map<String, String> response = new HashMap<>();

        Boolean deleteLoop = (Boolean) body.get("deleteLoop");
        response.put("msg", eventDAO.deleteEvent(id, deleteLoop) ? "1" : "0");

        return response;
    }

    @PutMapping("/{id}/updateStatus")
    public Map<String, String> updateStatus(@PathVariable("id") Integer id) {
        Map<String, String> response = new HashMap<>();

        response.put("msg", eventDAO.updateStatus(id) ? "1" : "0");

        return response;
    }
}
