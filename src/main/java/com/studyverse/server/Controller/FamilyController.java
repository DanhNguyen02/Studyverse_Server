package com.studyverse.server.Controller;

import com.studyverse.server.DAO.FamilyDAO;
import com.studyverse.server.Model.Family;
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

    @GetMapping("/{id}")
    public Map<String, Object> getFamilyById(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();

        Family family = familyDAO.getFamilyById(id);
        if (family != null) {
            response.put("msg", "1");
            response.put("data", family);
        }
        else response.put("msg", "0");
        return response;
    }

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

        response.put("msg", familyDAO.handleCreateFamily(email) ? "1" : "0");

        return response;
    }

    @PostMapping("/linkFamily")
    public Map<String, String> linkFamily(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String email = body.get("email");
        String familyEmail = body.get("familyEmail");

        response.put("msg", familyDAO.handleLinkFamily(email, familyEmail) ? "1" : "0");

        return response;
    }

    @PostMapping("/unlinkFamily")
    public Map<String, String> unlinkFamily(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String email = body.get("email");

        response.put("msg", familyDAO.handleUnlinkFamily(email) ? "1" : "0");

        return response;
    }

    @GetMapping("/getFamilyMembers")
    public Map<String, Object> getFamilyMembers(@RequestBody HashMap<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        String familyId = body.get("familyId");
//        String email = body.get("email");

        List<User> familyMembers = familyDAO.getFamilyMembers(familyId);
//        List<User> pendingUsers = familyDAO.getPendingUsers(familyId, email);

        response.put("msg", "1");
        response.put("data", familyMembers);
//        if (!pendingUsers.isEmpty()) response.put("pendingUsers", pendingUsers);

        return response;
    }

    @GetMapping("/getPendingUsers")
    public Map<String, Object> getPendingMembers(@RequestBody HashMap<String, String> body) {
        Map<String, Object> response = new HashMap<>();

        String familyId = body.get("familyId");
        String email = body.get("email");

        List<User> pendingUsers = familyDAO.getPendingUsers(familyId, email);

        if (pendingUsers == null) response.put("msg", "0");
        else {
            response.put("msg", "1");
            response.put("data", pendingUsers);
        }
        return response;
    }

    @PostMapping("/approveLinkFamily")
    public Map<String, String> approveLinkFamily(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String email = body.get("email");
        String familyId = body.get("familyId");
        String code = body.get("code");

        response.put("msg", familyDAO.handleApproveLinkFamily(email, familyId, code) ? "1" : "0");
        return response;
    }

    @PostMapping("/kickMember")
    public Map<String, String> kickMember(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String userEmail = body.get("userEmail");
        String memberEmail = body.get("memberEmail");
        String familyId = body.get("familyId");

        response.put("msg", familyDAO.handleKickMember(userEmail, memberEmail, familyId) ? "1" : "0");

        return response;
    }

    @PostMapping("/outFamily")
    public Map<String, String> outFamily(@RequestBody HashMap<String, String> body) {
        Map<String, String> response = new HashMap<>();

        String email = body.get("email");

        response.put("msg", familyDAO.handleOutFamily(email) ? "1" : "0");

        return response;
    }
}
