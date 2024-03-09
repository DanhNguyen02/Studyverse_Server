package com.studyverse.server.DAO;

import com.studyverse.server.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class UserDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Map<String, Integer> otpList = new HashMap<>();

    public User handleLogIn(String email, String password) {
        String sql = "select * from user where email = ? and password = ?";
        List<User> users = jdbcTemplate.query(
            sql,
            new Object[]{email, password},
            (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setDob(rs.getDate("dob"));
                user.setPhone(rs.getString("phone"));
                user.setAvatar(rs.getString("avatar"));
                user.setUserStatus(rs.getBoolean("user_status"));
                user.setAccountStatus(rs.getBoolean("account_status"));
                user.setLastLogin(rs.getDate("last_login"));
                user.setFamilyId(rs.getInt("family_id"));
                user.setNickName(rs.getString("nickname"));
                // Set other fields if needed
                return user;
            }
        );
        if (users.isEmpty()) return null;

        User user = users.get(0);
        String childrenSql = "select count(*) from user inner join children on user.id = children.id where user.id = ?";
        Integer count = jdbcTemplate.queryForObject(childrenSql, Integer.class, user.getId());
        user.setRole(count != null && count == 1 ? "children" : "parent");
        return user;
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

    public void updateOTPNumber(String email, Integer otpNumber) {
        otpList.put(email, otpNumber);
    }

    public Integer getOTPNumber(String email) {
        return otpList.getOrDefault(email, null);
    }

    public void updateNewPassword(String email, String newPassword) {
        String sql = "update user set password = ? where email = ?";
        jdbcTemplate.update(sql, newPassword, email);
    }

    public boolean updateUserInfo(HashMap<String, String> body) {
        String email = body.get("email");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");
        String phoneNumber = body.get("phoneNumber");
        String birthday = body.get("birthday");
        String nickName = body.get("nickName");

        List<String> updateFields = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (firstName != null) {
            updateFields.add("firstname = ?");
            params.add(firstName);
        }
        if (lastName != null) {
            updateFields.add("lastname = ?");
            params.add(lastName);
        }
        if (phoneNumber != null) {
            updateFields.add("phone = ?");
            params.add(phoneNumber);
        }
        if (birthday != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date dob = sdf.parse(birthday);
                updateFields.add("dob = ?");
                params.add(dob);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (nickName != null) {
            updateFields.add("nickname = ?");
            params.add(nickName);
        }

        params.add(email);
        Object[] paramsArray = params.toArray();

        String updateFieldsString = String.join(",", updateFields);

        String sql = "update user set " + updateFieldsString + " where email = ?";
        int rowsAffected = jdbcTemplate.update(sql, paramsArray);
        return rowsAffected != 0;
    }
}
