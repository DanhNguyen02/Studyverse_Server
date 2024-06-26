package com.studyverse.server.DAO;

import com.studyverse.server.Model.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class EventDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public List<Event> getEvents() {
        List<Event> events = Collections.emptyList();
        Session session;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            String sql = "select e.id, e.name, e.time_start, e.time_end, e.note, e.user_id, " +
                    "COALESCE(re.time, 0) as time, " +
                    "COALESCE(re.is_success, 0) as is_success " +
                    "from event e left join remind_event re on e.id = re.id";
            NativeQuery<Event> query = session.createNativeQuery(sql, Event.class);
            events = query.list();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return events;
    }

    public List<Event> getEventsUserJoin(Integer userId) {
        List<Event> events = new ArrayList<>();
        Session session;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            String sql = "SELECT e.id, e.name, e.time_start, e.time_end, e.note, e.user_id, " +
                    "COALESCE(re.time, 0) as time, " +
                    "COALESCE(re.is_success, 0) as is_success, " +
                    "CASE " +
                    "WHEN le.first_event_id IS NOT NULL AND e.id BETWEEN le.first_event_id AND le.last_event_id " +
                    "THEN TRUE ELSE FALSE " +
                    "END AS isLoop " +
                    "FROM event e LEFT JOIN remind_event re ON e.id = re.id " +
                    "LEFT JOIN loop_event le ON e.id BETWEEN le.first_event_id AND le.last_event_id " +
                    "WHERE e.user_id = :userId";

            List<Object[]> results = session.createNativeQuery(sql)
                    .setParameter("userId", userId)
                    .getResultList();

            Map<Integer, Event> eventMap = new HashMap<>();

            for (Object[] row : results) {
                Event event = new Event();
                event.setId((Integer) row[0]);
                event.setName((String) row[1]);
                event.setTimeStart(((Timestamp) row[2]).toLocalDateTime());
                event.setTimeEnd(((Timestamp) row[3]).toLocalDateTime());
                event.setNote((String) row[4]);
                event.setUserId((Integer) row[5]);
                event.setRemindTime(((BigInteger) row[6]).intValue());
                event.setSuccess(((BigInteger) row[7]).intValue() == 1);
                event.setLoop(((BigInteger) row[8]).intValue() == 1);

                eventMap.put(event.getId(), event);
            }

            String sqlUserIds = "SELECT event_id, user_id FROM user_involve_event WHERE event_id IN :eventIds";

            List<Object[]> userIdResults = session.createNativeQuery(sqlUserIds)
                    .setParameter("eventIds", eventMap.keySet())
                    .getResultList();

            for (Object[] row : userIdResults) {
                Integer eventId = (Integer) row[0];
                Integer involveUserId = (Integer) row[1];

                Event event = eventMap.get(eventId);
                if (event != null) {
                    event.getTagUsers().add(involveUserId);
                }
            }

            events = new ArrayList<>(eventMap.values());

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return events;
        }
        return events;
    }

    public boolean createEvent(HashMap<String, String> body) {
        Session session;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            String name = body.get("name");
            String day = body.get("day");
            String timeStart = body.get("timeStart") + ":00";
            String timeEnd = body.get("timeEnd") + ":00";
            int loopMode = Integer.parseInt(body.get("loopMode"));
            String endDateString = body.get("endDate");
            boolean isRemind = Boolean.parseBoolean(body.get("isRemind"));
            int remindTime = 0;
            if (!body.get("remindTime").isEmpty()) remindTime = Integer.parseInt(body.get("remindTime"));
            String note = body.get("note");
            int userId = Integer.parseInt(body.get("userId"));
            String tagUsersString = body.get("tagUsers");

            List<Integer> tagUsers = new ArrayList<>();
            if (!tagUsersString.isEmpty() && !tagUsersString.equals("[]")) {
                tagUsersString = tagUsersString.replaceAll("\\[|\\]|\\s", "");

                String[] stringArray = tagUsersString.split(",");

                for (String s : stringArray) {
                    tagUsers.add(Integer.parseInt(s));
                }
            }

            int plusDays = -1;
            int plusMonths = 0;
            switch (loopMode) {
                case 0 -> plusDays = 0;
                case 1 -> plusDays = 1;
                case 2 -> plusDays = 7;
                case 3 -> plusMonths = 1;
                default -> {
                    return false;
                }
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(day + " " + timeStart, formatter);
            LocalDateTime endDateTime = LocalDateTime.parse(day + " " + timeEnd, formatter);

            // Add date to table event
            Event newEvent = new Event();

            newEvent.setName(name);
            newEvent.setTimeStart(startDateTime);
            newEvent.setTimeEnd(endDateTime);
            newEvent.setNote(note);
            newEvent.setUserId(userId);

            session.save(newEvent);

            if (isRemind) {
                String remindSql = "insert into remind_event (id, time, is_success) values (:id, :remindTime, 0)";

                session.createNativeQuery(remindSql)
                        .setParameter("id", newEvent.getId())
                        .setParameter("remindTime", remindTime)
                        .executeUpdate();
            }

            if (!tagUsers.isEmpty()) {
                String tagUsersSql = "insert into user_involve_event (event_id, user_id) values (:eventId, :userId)";

                for (Integer tagUserId : tagUsers) {
                    session.createNativeQuery(tagUsersSql)
                            .setParameter("eventId", newEvent.getId())
                            .setParameter("userId", tagUserId)
                            .executeUpdate();
                }
            }

            int firstId = newEvent.getId();
            int lastId = newEvent.getId() + 1;

            // Add data to loop_event table
            if (loopMode != 0) {
                LocalDateTime endLoopDate = LocalDateTime.parse(endDateString + " 00:00:00", formatter);
                while (true) {
                    int count = lastId - firstId;
                    LocalDateTime newStartDateTime = startDateTime.plusDays((long) plusDays * count).plusMonths(plusMonths * count);
                    LocalDateTime newEndDateTime = endDateTime.plusDays((long) plusDays * count).plusMonths(plusMonths * count);

                    if (newEndDateTime.isAfter(endLoopDate)) {
                        lastId--;
                        break;
                    }

                    Event event = new Event();

                    event.setName(name);
                    event.setTimeStart(newStartDateTime);
                    event.setTimeEnd(newEndDateTime);
                    event.setNote(note);
                    event.setUserId(userId);

                    session.save(event);

                    if (isRemind) {
                        String remindSql = "insert into remind_event (id, time, is_success) values (:id, :remindTime, 0)";

                        session.createNativeQuery(remindSql)
                                .setParameter("id", lastId)
                                .setParameter("remindTime", remindTime)
                                .executeUpdate();
                    }

                    if (!tagUsers.isEmpty()) {
                        String tagUsersSql = "insert into user_involve_event (event_id, user_id) values (:eventId, :userId)";

                        for (Integer tagUserId : tagUsers) {
                            session.createNativeQuery(tagUsersSql)
                                    .setParameter("eventId", lastId)
                                    .setParameter("userId", tagUserId)
                                    .executeUpdate();
                        }
                    }

                    lastId++;
                }
                String sql = "insert into loop_event (first_event_id, last_event_id) values(:firstId, :lastId)";

                session.createNativeQuery(sql)
                        .setParameter("firstId", firstId)
                        .setParameter("lastId", lastId)
                        .executeUpdate();
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateEvents(Integer id, HashMap<String, String> body) {
        Session session;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            String getEventSql = "SELECT e.id, e.name, e.time_start, e.time_end, e.note, e.user_id, " +
                    "COALESCE(re.time, 0) as time, " +
                    "COALESCE(re.is_success, 0) as is_success, " +
                    "CASE " +
                    "WHEN le.first_event_id IS NOT NULL AND e.id BETWEEN le.first_event_id AND le.last_event_id " +
                    "THEN TRUE ELSE FALSE " +
                    "END AS isLoop " +
                    "FROM event e LEFT JOIN remind_event re ON e.id = re.id " +
                    "LEFT JOIN loop_event le ON e.id BETWEEN le.first_event_id AND le.last_event_id " +
                    "WHERE e.id = :eventId";

            List<Object[]> results = session.createNativeQuery(getEventSql)
                    .setParameter("eventId", id)
                    .getResultList();

            Event event = new Event();

            if (results.isEmpty()) event = null;
            else {
                Object[] row = results.get(0);
                event.setId((Integer) row[0]);
                event.setName((String) row[1]);
                event.setTimeStart(((Timestamp) row[2]).toLocalDateTime());
                event.setTimeEnd(((Timestamp) row[3]).toLocalDateTime());
                event.setNote((String) row[4]);
                event.setUserId((Integer) row[5]);
                event.setRemindTime(((BigInteger) row[6]).intValue());
                event.setSuccess(((BigInteger) row[7]).intValue() == 1);
                event.setLoop(((BigInteger) row[8]).intValue() == 1);
            }

            if (event != null) {
                String name = body.get("name");
                String date = body.get("date");
                String timeStart = body.get("timeStart") + ":00";
                String timeEnd = body.get("timeEnd") + ":00";
                boolean isRemind = Boolean.parseBoolean(body.get("isRemind"));
                int newRemindTime = 0;
                if (!body.get("remindTime").isEmpty()) newRemindTime = Integer.parseInt(body.get("remindTime"));
                String note = body.get("note");
                boolean isLoop = Boolean.parseBoolean(body.get("isLoop"));
                int userId = Integer.parseInt(body.get("userId"));
                String tagUsersString = body.get("tagUsers");

                List<Integer> tagUsers = new ArrayList<>();
                if (!tagUsersString.isEmpty() && !tagUsersString.equals("[]")) {
                    tagUsersString = tagUsersString.replaceAll("\\[|\\]|\\s", "");

                    String[] stringArray = tagUsersString.split(",");

                    for (String s : stringArray) {
                        tagUsers.add(Integer.parseInt(s));
                    }
                }
                if (userId != event.getUserId()) return false;

                String getRangeSql = "select * from loop_event where :id BETWEEN first_event_id and last_event_id";
                List<Object[]> resultList = session.createNativeQuery(getRangeSql)
                        .setParameter("id", id)
                        .getResultList();

                Integer firstId = 0, lastId = 0;
                if (resultList.size() == 1) {
                    firstId = (Integer) resultList.get(0)[0];
                    lastId = (Integer) resultList.get(0)[1];
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                if (!isLoop) {
                    event.setName(name);
                    event.setTimeStart(LocalDateTime.parse(date + " " + timeStart, formatter));
                    event.setTimeEnd(LocalDateTime.parse(date + " " + timeEnd, formatter));
                    event.setNote(note);

                    session.saveOrUpdate(event);

                    if (!tagUsers.isEmpty()) {
                        String deleteTagUsersSql = "delete from user_involve_event where event_id = :eventId";
                        session.createNativeQuery(deleteTagUsersSql)
                                .setParameter("eventId", id)
                                .executeUpdate();

                        String tagUsersSql = "insert into user_involve_event (event_id, user_id) values (:eventId, :userId)";

                        for (Integer tagUserId : tagUsers) {
                            session.createNativeQuery(tagUsersSql)
                                    .setParameter("eventId", id)
                                    .setParameter("userId", tagUserId)
                                    .executeUpdate();
                        }
                    }
                }
                else {
                    if (!tagUsers.isEmpty()) {
                        String deleteTagUsersSql = "delete from user_involve_event where event_id between :firstId and :lastId";
                        session.createNativeQuery(deleteTagUsersSql)
                                .setParameter("firstId", firstId)
                                .setParameter("lastId", lastId)
                                .executeUpdate();
                    }

                    int insertId = firstId;
                    while (insertId <= lastId) {
                        String hql = "update Event set name = :name, note = :note where id = :id";
                        session.createQuery(hql)
                                .setParameter("id", insertId)
                                .setParameter("name", name)
                                .setParameter("note", note)
                                .executeUpdate();

                        String tagUsersSql = "insert into user_involve_event (event_id, user_id) values (:eventId, :userId)";

                        for (Integer tagUserId : tagUsers) {
                            session.createNativeQuery(tagUsersSql)
                                    .setParameter("eventId", insertId)
                                    .setParameter("userId", tagUserId)
                                    .executeUpdate();
                        }

                        insertId++;
                    }
                }

                String sql;
                if (isRemind) {
                    if (isLoop) {
                        String deleteSql = "delete from remind_event where id between :firstId and :lastId";

                        session.createNativeQuery(deleteSql)
                                .setParameter("firstId", firstId)
                                .setParameter("lastId", lastId)
                                .executeUpdate();

                        sql = "insert into remind_event (id, time) values(:id, :remindTime)";

                        int insertId = firstId;
                        while (insertId <= lastId) {
                            session.createNativeQuery(sql)
                                    .setParameter("id", insertId)
                                    .setParameter("remindTime", newRemindTime)
                                    .executeUpdate();

                            insertId++;
                        }
                    }
                    else {
                        if (event.getRemindTime() > 0)
                            sql = "update remind_event set time = :remindTime where id = :id";
                        else sql = "insert into remind_event (id, time) values (:id, :remindTime)";

                        session.createNativeQuery(sql)
                                .setParameter("id", id)
                                .setParameter("remindTime", newRemindTime)
                                .executeUpdate();
                    }
                }
                else if (event.getRemindTime() > 0) {
                    if (isLoop) {
                        sql = "delete from remind_event where id between :firstId and :lastId";

                        session.createNativeQuery(sql)
                                .setParameter("firstId", firstId)
                                .setParameter("lastId", lastId)
                                .executeUpdate();
                    }
                    else {
                        sql = "delete from remind_event where id = :id";

                        session.createNativeQuery(sql)
                                .setParameter("id", id)
                                .executeUpdate();
                    }
                }
            }
            else return false;

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteEvent(Integer id, Boolean deleteLoop) {
        Session session;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            String getRangeSql = "select * from loop_event where :id BETWEEN first_event_id and last_event_id";
            List<Object[]> resultList = session.createNativeQuery(getRangeSql)
                    .setParameter("id", id)
                    .getResultList();

            if (!deleteLoop) {
                Event event = session.get(Event.class, id);
                if (event != null) {
                    session.delete(event);

                    if (resultList.size() == 1) {
                        Integer firstId = (Integer) resultList.get(0)[0];
                        Integer lastId = (Integer) resultList.get(0)[1];

                        String hql = "select count(*) from Event where id between :firstId and :lastId";
                        Query<Long> query = session.createQuery(hql, Long.class)
                                .setParameter("firstId", firstId)
                                .setParameter("lastId", lastId);
                        Long count = query.getSingleResult();

                        if (count == 0) {
                            String sql = "delete from loop_event where first_event_id = :firstId and last_event_id = :lastId";

                            session.createNativeQuery(sql)
                                    .setParameter("firstId", firstId)
                                    .setParameter("lastId", lastId)
                                    .executeUpdate();
                        }
                    }
                }
                else return false;
            }
            else {
                if (resultList.size() == 1) {
                    Integer firstId = (Integer) resultList.get(0)[0];
                    Integer lastId = (Integer) resultList.get(0)[1];

                    String hql = "delete from Event where id between :firstId and :lastId";

                    session.createQuery(hql)
                            .setParameter("firstId", firstId)
                            .setParameter("lastId", lastId)
                            .executeUpdate();

                    String sql = "delete from loop_event where first_event_id = :firstId and last_event_id = :lastId";

                    session.createNativeQuery(sql)
                            .setParameter("firstId", firstId)
                            .setParameter("lastId", lastId)
                            .executeUpdate();
                }
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateStatus(Integer id) {
        Session session;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Event event = session.get(Event.class, id);
            if (event != null) {
                String sql = "update remind_event set is_success = 1 where id = :id and is_success = 0";
                session.createNativeQuery(sql)
                        .setParameter("id", id)
                        .executeUpdate();
            }
            else return false;

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
