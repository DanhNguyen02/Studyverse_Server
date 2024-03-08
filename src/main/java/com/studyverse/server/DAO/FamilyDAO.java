package com.studyverse.server.DAO;

import com.studyverse.server.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FamilyDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Integer countFamily() {
        String sql = "select count(*) from family";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public Integer getUserIdByEmail(String email) {
        String sql = "select id from user where email = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
    }

    public Integer getFamilyIdByEmail(String email) {
        String sql = "select family_id from user where email = ?";
        Integer familyId = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        if (familyId == null) return 0;
        return familyId;
    }

    public boolean handleCheckExistFamily(String email) {
        String sql = "select family_id from user where email = ?";
        Integer familyId = jdbcTemplate.queryForObject(sql, new Object[]{email}, Integer.class);
        return familyId != 0;
    }

    public void handleCreateFamily(String email) {
        String sql = "insert into family (id, email) values (?, ?)";
        int id = countFamily() + 1;
        jdbcTemplate.update(sql, id, email);

        handleJoinFamily(id, email);
    }

    public void handleJoinFamily(int familyId, String email) {
        String sql = "update user set family_id = ? where email = ?";
        jdbcTemplate.update(sql, familyId, email);
    }

    public boolean handleLinkFamily(String userId, String familyEmail) {
        int familyId = getFamilyIdByEmail(familyEmail);
        if (familyId == 0) return false;

        String sql = "insert into linking_family (family_id, user_id) values (?, ?)";
        jdbcTemplate.update(sql, familyId, userId);
        return true;
    }

    public List<User> getFamilyMembers(String familyId) {
        String sql = "select * " +
                "from user " +
                "inner join family on user.family_id = family.id" +
                "where user.family_id = " + familyId;
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new User(rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("phone"),
                        rs.getString("avatar")));
    }

    public List<User> getPendingUsers(String familyId, String email) {
        String checkHostFamilySql = "select * from family where id = ? and email = ?";
        Integer count = jdbcTemplate.queryForObject(
                checkHostFamilySql,
                new Object[]{familyId, email},
                Integer.class);
        if (count != null && count == 0) return new ArrayList<>();

        String linkingFamilySql = "select * " +
                "from user " +
                "inner join linking_family on user.id = linking_family.user_id " +
                "where linking_family.family_id = " + familyId;
        return jdbcTemplate.query(linkingFamilySql, (rs, rowNum) ->
                new User(rs.getInt("id"),
                        rs.getString("email"),
                        rs.getString("firstname"),
                        rs.getString("lastname"),
                        rs.getString("phone"),
                        rs.getString("avatar")));
    }

    public boolean handleApproveMember(String userId, String familyId, String code) {
        String linkingSql = "delete from linking_family where family_id = ? and user_id = ?";
        int affectedRows = jdbcTemplate.update(linkingSql,familyId, userId);
        if (affectedRows == 0) return false;

        if (code.equals("1")) {
            String addToFamilySql = "update user set family_id = ? where id = ?";
            jdbcTemplate.update(addToFamilySql, familyId, userId);
        }
        return true;
    }
}
