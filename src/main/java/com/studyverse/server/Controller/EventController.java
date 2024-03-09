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

    @PostMapping("/createEvent")
    public Map<String, String> createEvent(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        return response;
    }
}
