package com.studyverse.server.DAO;

import com.studyverse.server.Model.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class EventDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public List<Event> getEvents() {
        List<Event> events = Collections.emptyList();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Query<Event> query = session.createQuery("from Event", Event.class);
            events = query.list();

            for (Event event : events) {
                System.out.println("Event: " + event.getName() + " from " + event.getTimeStart() + " to " + event.getTimeEnd());
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public List<Event> getEventsUserJoin(Integer userId) {
        List<Event> events = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sql = "SELECT e.* FROM event e " +
                    "INNER JOIN user_join_event uje ON e.id = uje.event_id " +
                    "WHERE uje.user_id = :userId";

            NativeQuery<Event> query = session.createNativeQuery(sql, Event.class);
            query.setParameter("userId", userId);
            events = query.getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return events;
        }
        return events;
    }

    public boolean createEvent(HashMap<String, Object> body) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String name = (String) body.get("name");
            String day = (String) body.get("day");
            String timeStart = (String) body.get("timeStart");
            String timeEnd = (String) body.get("timeEnd");
            Integer loopMode = (Integer) body.get("loopMode");
            String endDate = (String) body.get("endDate");
            Boolean isRemind = (Boolean) body.get("isRemind");
            String remindTime = (String) body.get("remindTime");
            String note = (String) body.get("note");
            Integer userId = (Integer) body.get("userId");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            // Add date to event table
            Event newEvent = new Event();

            newEvent.setName(name);
            newEvent.setTimeStart(LocalDateTime.parse(day + " " + timeStart, formatter));
            newEvent.setTimeEnd(LocalDateTime.parse(day + " " + timeEnd, formatter));
            newEvent.setNote(note);

            session.save(newEvent);

            // Add data to loop_event table
            if (loopMode != 0) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String sql = "insert into loop_event (id, loop_mode, end_date) values (:id, :loopMode, :endDate)";

                session.createNativeQuery(sql)
                        .setParameter("id", newEvent.getId())
                        .setParameter("loopMode", loopMode)
                        .setParameter("endDate", simpleDateFormat.parse(endDate))
                        .executeUpdate();
            }

            // Add data to remind_event table
            if (isRemind) {
                String sql = "insert into remind_event (id, remind_time) values (:id, :remindTime)";

                session.createNativeQuery(sql)
                        .setParameter("id", newEvent.getId())
                        .setParameter("remindTime", Time.valueOf(remindTime))
                        .executeUpdate();
            }

            // Add data to user_join_event table
            {
                String sql = "insert into user_join_event (event_id, user_id) values(:eventId, :userId)";

                session.createNativeQuery(sql)
                        .setParameter("eventId", newEvent.getId())
                        .setParameter("userId", userId)
                        .executeUpdate();
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
