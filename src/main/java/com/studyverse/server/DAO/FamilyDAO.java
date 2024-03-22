package com.studyverse.server.DAO;

import com.studyverse.server.Model.Family;
import com.studyverse.server.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FamilyDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Family getFamilyById(Integer id) {
        String sql = "select * from family where id = ?";
        List<Family> families = jdbcTemplate.query(
            sql,
            new Object[]{id},
            (rs, rowNum) -> {
                Family family = new Family();
                family.setId(rs.getInt("id"));
                family.setName(rs.getString("name"));
                family.setAvatar(rs.getString("avatar"));
                family.setEmail(rs.getString("email"));
                return family;
            }
        );
        if (families.isEmpty()) return null;
        return families.get(0);
    }

    public boolean checkUserExistsAndNotInFamily(String email) {
        User user = getUserByEmail(email);
        if (user == null) return false;
        return user.getFamilyId() == 0;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        List<User> users = jdbcTemplate.query(
            sql,
            new Object[]{email},
            (rs, rowNum) -> {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setFamilyId(rs.getInt("family_id"));
                return user;
            }
        );
        if (users.isEmpty()) return null;
        return users.get(0);
    }

    public Integer countFamily() {
        String sql = "select count(*) from family";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer getUserIdByEmail(String email) {
        String sql = "select id from user where email = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
    }

    public Family getFamilyByEmail(String email) {
        String sql = "select * from family where email = ?";
        List<Family> families = jdbcTemplate.query(
            sql,
            new Object[]{email},
            (rs, rowNum) -> {
                Family family = new Family();
                family.setId(rs.getInt("id"));
                family.setName(rs.getString("name"));
                family.setAvatar(rs.getString("avatar"));
                family.setEmail(rs.getString("email"));
                return family;
            }
        );
        if (families.isEmpty()) return null;
        return families.get(0);
    }

    public Family getFamilyById(String id) {
        String sql = "select * from family where id = ?";
        List<Family> families = jdbcTemplate.query(
            sql,
            new Object[]{id},
            (rs, rowNum) -> {
                Family family = new Family();
                family.setId(rs.getInt("id"));
                family.setName(rs.getString("name"));
                family.setAvatar(rs.getString("avatar"));
                family.setEmail(rs.getString("email"));
                return family;
            }
        );
        if (families.isEmpty()) return null;
        return families.get(0);
    }

    public boolean handleCheckExistFamily(String email) {
        String sql = "select family_id from user where email = ?";
        Integer familyId = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        return familyId != 0;
    }

    public int handleCreateFamily(String email) {
        boolean checkUser = checkUserExistsAndNotInFamily(email);
        if (!checkUser) return 0;

        String sql = "insert into family (id, email) values (?, ?)";
        int id = countFamily() + 1;
        int rowsAffected = jdbcTemplate.update(sql, id, email);

        if (rowsAffected > 0) {
            handleJoinFamily(id, email);
            return id;
        }
        return 0;
    }

    public void handleJoinFamily(int familyId, String email) {
        String sql = "update user set family_id = ? where email = ?";
        jdbcTemplate.update(sql, familyId, email);
    }

    public boolean handleLinkFamily(String email, String familyEmail) {
        Family family = getFamilyByEmail(familyEmail);
        if (family == null || family.getId() == 0) return false;

        User user = getUserByEmail(email);
        if (user == null || user.getFamilyId() != 0) return false;

        String sql = "insert into linking_family (family_id, user_id) values (?, ?)";
        jdbcTemplate.update(sql, family.getId(), user.getId());

        String userSql = "update user set family_id = -1 where email = ?";
        jdbcTemplate.update(userSql, email);
        return true;
    }

    public boolean handleUnlinkFamily(String email) {
        User user = getUserByEmail(email);
        if (user == null || user.getFamilyId() != -1) return false;

        String sql = "delete from linking_family where user_id = ?";
        int affectedRows = jdbcTemplate.update(sql, user.getId());
        if (affectedRows == 0) return false;

        String userSql = "update user set family_id = 0 where email = ?";
        jdbcTemplate.update(userSql, email);
        return true;
    }

    public List<User> getFamilyMembers(String familyId) {
        if (familyId.equals("0")) return new ArrayList<>();
        String sql = "select * from user where family_id = ?";
        return jdbcTemplate.query(
            sql,
            new Object[]{familyId},
            (rs, rowNum) -> {
                User user = new User(rs.getInt("id"),
                    rs.getString("email"),
                    rs.getString("firstname"),
                    rs.getString("lastname"),
                    rs.getString("phone"),
                    rs.getString("avatar"),
                    rs.getString("nickname"),
                    rs.getBoolean("role") ? "parent" : "children",
                    rs.getString("user_status"),
                    rs.getBoolean("account_status"),
                    rs.getDate("dob")
                );
                Timestamp lastLogin = rs.getTimestamp("last_login");
                if (lastLogin != null) user.setLastLogin(lastLogin.toLocalDateTime());
                return user;
            }
        );
    }

    public List<User> getPendingUsers(String familyId, String email) {
        User user = getUserByEmail(email);
        if (user == null || user.getFamilyId() != Integer.parseInt(familyId)) return null;

        Family family = getFamilyById(familyId);
        if (family == null || !user.getEmail().equals(family.getEmail())) return null;

        String linkingFamilySql = "select * " +
                "from user " +
                "inner join linking_family on user.id = linking_family.user_id " +
                "where linking_family.family_id = " + familyId;
        return jdbcTemplate.query(linkingFamilySql, (rs, rowNum) ->
                {
                    User pendingUser = new User(rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getString("phone"),
                            rs.getString("avatar"),
                            rs.getString("nickname"),
                            rs.getBoolean("role") ? "parent" : "children",
                            rs.getString("user_status"),
                            rs.getBoolean("account_status"),
                            rs.getDate("dob")
                    );
                    Timestamp lastLogin = rs.getTimestamp("last_login");
                    if (lastLogin != null) pendingUser.setLastLogin(lastLogin.toLocalDateTime());
                    return pendingUser;
                }
        );
    }

    public boolean handleApproveLinkFamily(String email, String familyId, String code) {
        User user = getUserByEmail(email);
        if (user == null || user.getFamilyId() != -1) return false;

        String linkingSql = "delete from linking_family where family_id = ? and user_id = ?";
        int affectedRows = jdbcTemplate.update(linkingSql, familyId, user.getId());
        if (affectedRows == 0) return false;

        if (code.equals("1")) {
            String addToFamilySql = "update user set family_id = ? where id = ?";
            jdbcTemplate.update(addToFamilySql, familyId, user.getId());
        }
        else if (code.equals("0")) {
            String addToFamilySql = "update user set family_id = 0 where id = ?";
            jdbcTemplate.update(addToFamilySql, user.getId());
        }
        return true;
    }

    public boolean handleKickMember(String userEmail, String memberEmail, String familyId) {
        User hostFamily = getUserByEmail(userEmail);
        if (hostFamily == null || hostFamily.getFamilyId() != Integer.parseInt(familyId)) return false;

        User member = getUserByEmail(memberEmail);
        if (member == null || member.getFamilyId() != Integer.parseInt(familyId)) return false;

        String sql = "update user set family_id = 0 where id = ?";
        int rowsAffected = jdbcTemplate.update(sql, member.getId());

        return rowsAffected != 0;
    }

    public boolean handleOutFamily(String email) {
        User user = getUserByEmail(email);
        if (user == null || user.getFamilyId() == 0) return false;

        Family family = getFamilyById(user.getFamilyId());
        if (family == null) return false;

        if (user.getEmail().equals(family.getEmail())) {
            String updateUserSql = "update user set family_id = 0 where family_id = ?";
            jdbcTemplate.update(updateUserSql, family.getId());

            String deleteFamilySql = "delete from family where id = ?";
            jdbcTemplate.update(deleteFamilySql, family.getId());
        }
        else {
            String updateUserSql = "update user set family_id = 0 where id = ?";
            jdbcTemplate.update(updateUserSql, user.getId());
        }
        return true;
    }
}
