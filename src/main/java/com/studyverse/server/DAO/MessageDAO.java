package com.studyverse.server.DAO;

import com.studyverse.server.Model.Message;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MessageDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public Map<Integer, List<Message>> getMessages(Integer userId) {
        Map<Integer, List<Message>> result = new HashMap<>();
        List<Message> messages;

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "from Message where receiver_id = :userId or sender_id = :senderId";
            Query<Message> query = session.createQuery(hql, Message.class);
            query.setParameter("userId", userId);
            query.setParameter("senderId", userId);

            messages = query.list();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        for (Message message : messages) {
            Integer keyId = message.getSenderId();
            if (keyId.equals(userId)) keyId = message.getReceiverId();

            if (!result.containsKey(keyId)) {
                List<Message> conversation = new ArrayList<>();
                result.put(keyId, conversation);
            }
            result.get(keyId).add(message);
        }

        return result;
    }

    public boolean sendMessage(HashMap<String, String> body) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Message message = new Message();
            message.setContent(body.get("content"));
            message.setRead(false);
            message.setSenderId(Integer.parseInt(body.get("senderId")));
            message.setReceiverId(Integer.parseInt(body.get("receiverId")));
            message.setTime(LocalDateTime.now());

            session.save(message);

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Integer readMessages(Integer receiverId, Integer senderId) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "update Message set is_read = true where sender_id = :senderId and receiver_id = :receiverId and is_read = false";
            Query<?> query = session.createQuery(hql);
            query.setParameter("senderId", senderId);
            query.setParameter("receiverId", receiverId);

            Integer result = query.executeUpdate();

            session.getTransaction().commit();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
