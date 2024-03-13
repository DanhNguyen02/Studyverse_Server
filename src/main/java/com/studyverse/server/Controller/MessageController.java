package com.studyverse.server.Controller;

import com.studyverse.server.DAO.MessageDAO;
import com.studyverse.server.Model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private MessageDAO messageDAO;

    @GetMapping("/{userId}")
    public Map<String, Object> getMessages(@PathVariable("userId") Integer userId) {
        Map<String, Object> response = new HashMap<>();

        Map<Integer, List<Message>> result = messageDAO.getMessages(userId);

        if (result == null) response.put("msg", "0");
        else {
            response.put("msg", "1");
            response.put("data", result);
        }

        return response;
    }

    @PostMapping("/sendMessage")
    public Map<String, Object> sendMessage(@RequestBody HashMap<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer senderId = Integer.parseInt(body.get("senderId"));
            Integer receiverId = Integer.parseInt(body.get("receiverId"));
        } catch (NumberFormatException e) {
            response.put("msg", "0");
        } finally {
            response.put("msg", messageDAO.sendMessage(body) ? "1" : "0");
        }

        return response;
    }

    @PostMapping("/readMessages")
    public Map<String, Object> readMessages(@RequestBody HashMap<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        try {
            Integer userId = Integer.parseInt(body.get("userId"));
            Integer opponentId = Integer.parseInt(body.get("opponentId"));

            Integer result = messageDAO.readMessages(userId, opponentId);
            response.put("data", result);
        } catch (NumberFormatException e) {
            response.put("msg", "0");
        } finally {
            response.put("msg", "1");
        }

        return response;
    }
}
