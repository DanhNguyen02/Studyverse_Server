package com.studyverse.server.DAO;

import com.studyverse.server.Model.Event;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class EventDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public List<Event> getEvents() {
        List<Event> events = Collections.emptyList();

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String sql = "select e.id, e.name, e.time_start, e.time_end, e.note, e.user_id, " +
                    "COALESCE(re.time, 0) as time, " +
                    "COALESCE(re.is_success, 0) as is_success " +
                    "from event e left join remind_event re on e.id = re.id";
            NativeQuery<Event> query = session.createNativeQuery(sql, Event.class);
            events = query.list();

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

//            NativeQuery<Event> query = session.createNativeQuery(sql, Event.class);
//            query.setParameter("userId", userId);
//            events = query.getResultList();
            List<Object[]> results = session.createNativeQuery(sql)
                    .setParameter("userId", userId)
                    .getResultList();

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
                events.add(event);
            }

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
            String endDateString = (String) body.get("endDate");
            Boolean isRemind = (Boolean) body.get("isRemind");
            Integer remindTime = (Integer) body.get("remindTime");
            String note = (String) body.get("note");
            Integer userId = (Integer) body.get("userId");

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
            if (isRemind) newEvent.setRemindTime(remindTime);

            session.save(newEvent);

            if (!isRemind) {
                String sql = "delete from remind_event where id = :id";

                session.createNativeQuery(sql)
                        .setParameter("id", newEvent.getId())
                        .executeUpdate();
            }

            int firstId = newEvent.getId();
            int lastId = newEvent.getId();

            // Add data to loop_event table
            if (loopMode != 0) {
                LocalDateTime endLoopDate = LocalDateTime.parse(endDateString + " 00:00:00", formatter);
                while (true) {
                    int count = lastId - firstId + 1;
                    LocalDateTime newStartDateTime = startDateTime.plusDays((long) plusDays * count).plusMonths(plusMonths * count);
                    LocalDateTime newEndDateTime = endDateTime.plusDays((long) plusDays * count).plusMonths(plusMonths * count);

                    if (newEndDateTime.isAfter(endLoopDate)) break;

                    Event event = new Event();

                    event.setName(name);
                    event.setTimeStart(newStartDateTime);
                    event.setTimeEnd(newEndDateTime);
                    event.setNote(note);
                    event.setUserId(userId);
                    if (isRemind) event.setRemindTime(remindTime);

                    session.save(event);

                    lastId++;
                }
                String sql = "insert into loop_event (first_event_id, last_event_id) values(:firstId, :lastId)";

                session.createNativeQuery(sql)
                        .setParameter("firstId", firstId)
                        .setParameter("lastId", lastId)
                        .executeUpdate();
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateEvents(Integer id, HashMap<String, Object> body) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Event event = session.get(Event.class, id);
            if (event != null) {
                String name = (String) body.get("name");
                String timeStart = (String) body.get("timeStart");
                String timeEnd = (String) body.get("timeEnd");
                Boolean isRemind = (Boolean) body.get("isRemind");
                Integer newRemindTime = (Integer) body.get("remindTime");
                String note = (String) body.get("note");
                Integer userId = (Integer) body.get("userId");
                if (userId != event.getUserId()) return false;

                LocalDate date = event.getTimeStart().toLocalDate();
                System.out.println(date);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                event.setName(name);
                event.setTimeStart(LocalDateTime.parse(date + " " + timeStart, formatter));
                event.setTimeEnd(LocalDateTime.parse(date + " " + timeEnd, formatter));
                event.setNote(note);

                session.saveOrUpdate(event);

                int remindTime = event.getRemindTime();
                String sql = "";
                if (isRemind) {
                    if (remindTime > 0) sql = "update remind_event set remind_time = :remindTime where id = :id";
                    else sql = "insert into remind_event (id, remind_time) values(:id, :remindTime)";

                    session.createNativeQuery(sql)
                            .setParameter("id", id)
                            .setParameter("remindTime", newRemindTime)
                            .executeUpdate();
                }
                else if (remindTime > 0) {
                    sql = "delete from remind_event where id = :id";

                    session.createNativeQuery(sql)
                            .setParameter("id", id)
                            .executeUpdate();
                }
            }
            else return false;

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteEvent(Integer id, Boolean deleteLoop) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            if (!deleteLoop) {
                Event event = session.get(Event.class, id);
                if (event != null) {
                    session.delete(event);
                }
                else return false;
            }
            else {
                String getRangeSql = "select * from loop_event where :id BETWEEN first_event_id and last_event_id";
                List<Object[]> resultList = session.createNativeQuery(getRangeSql)
                        .setParameter("id", id)
                        .getResultList();
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

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateStatus(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Event event = session.get(Event.class, id);
            if (event != null) {
                String sql = "update remind_event set is_success = 1 where id = :id and is_success = 0";
                session.createNativeQuery(sql)
                        .setParameter("id", id)
                        .executeUpdate();
            }
            else return false;

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
