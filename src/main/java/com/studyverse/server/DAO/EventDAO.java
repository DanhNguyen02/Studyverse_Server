package com.studyverse.server.DAO;

import com.studyverse.server.Model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EventDAO {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Event> getEvents() {
        return jdbcTemplate.query(
                "select * from event",
                (rs, rowNum) -> {
                    Event event = new Event();
                    event.setId(rs.getInt("id"));
                    event.setName(rs.getString("name"));
                    event.setTimeStart(rs.getString("time_start"));
                    event.setTimeEnd("time_end");
                    return event;
                }
        );
    }
}
