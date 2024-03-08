package com.studyverse.server.Controller;

import com.studyverse.server.DAO.FamilyDAO;
import com.studyverse.server.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/family")
public class FamilyController {
    @Autowired
    private FamilyDAO familyDAO;

    @GetMapping("/checkUserInFamily")
    public Map<String, String> checkExistFamily(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String email = body.get("email");
        response.put("msg", familyDAO.handleCheckExistFamily(email) ? "1" : "0");

        return response;
    }

    @PostMapping("/createFamily")
    public Map<String, String> createFamily(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String email = body.get("email");
        familyDAO.handleCreateFamily(email);

        return response;
    }

    @PostMapping("/linkFamily")
    public Map<String, String> linkFamily(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String userId = body.get("userId");
        String familyEmail = body.get("familyEmail");

        response.put("msg", familyDAO.handleLinkFamily(userId, familyEmail) ? "1" : "0");

        return response;
    }

    @GetMapping("/getFamily")
    public Map<String, Object> getFamily(@RequestBody HashMap<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        String familyId = body.get("familyId");
        String email = body.get("email");

        List<User> familyMembers = familyDAO.getFamilyMembers(familyId);
        List<User> pendingUsers = familyDAO.getPendingUsers(familyId, email);

        response.put("familyMembers", familyMembers);
        if (!pendingUsers.isEmpty()) response.put("pendingUsers", pendingUsers);

        return response;
    }

    @PostMapping("/approveJoinFamily")
    public Map<String, String> approveJoinFamily(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String userId = body.get("userId");
        String familyId = body.get("familyId");
        String code = body.get("code");

        response.put("msg", familyDAO.handleApproveMember(userId, familyId, code) ? "1" : "0");
        return response;
    }
}
