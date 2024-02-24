package com.studyverse.server.DAO;

import com.studyverse.server.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Repository
public class UserDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public boolean handleLogIn(String email, String password) {
        String sql = "select * from user where email = ? and password = ?";
        List<User> users = jdbcTemplate.query(
            sql,
            new Object[]{email, password},
            (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                // Set other fields if needed
                return user;
            }
        );

        return !users.isEmpty();
    }

    public List<User> getAllUsers() {
        return jdbcTemplate.query("select * from user", (rs, rowNum) -> new User(rs.getString("email"), rs.getString("password")));
    }

    public boolean handleSignUp(HashMap<String, String> user) {
        System.out.println(user);
        if (checkUserExists(user.get("email"))) return false;

        int newId = getAllUsers().size() + 1;

        String userSql = "insert into user(id, email, password, firstname, lastname, dob, phone) values(?, ?, ?, ?, ?, ?, ?)";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date dob = sdf.parse(user.get("dob"));
            jdbcTemplate.update(userSql,
                    newId,
                    user.get("email"),
                    user.get("password"),
                    user.get("firstName"),
                    user.get("lastName"),
                    dob,
                    user.get("phoneNumber"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (user.get("signUpType").equals("Parent")) {
            String parentSql = "insert into parent(id) values(?)";
            jdbcTemplate.update(parentSql, newId);
        }
        else {
            String childrenSql = "insert into children(id) values(?)";
            jdbcTemplate.update(childrenSql, newId);
        }
        return true;
    }

    public boolean checkUserExists(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count > 0;
    }
}
