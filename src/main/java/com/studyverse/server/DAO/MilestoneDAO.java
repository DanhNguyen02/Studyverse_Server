package com.studyverse.server.DAO;

import com.studyverse.server.Model.Milestone;
import com.studyverse.server.SafeConvert;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MilestoneDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public boolean addMilestone(HashMap<String, Object> body) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String name = (String) body.get("name");
            String content = (String) body.get("content");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sdf.parse((String) body.get("startDate"));
            Date endDate = sdf.parse((String) body.get("endDate"));

            Integer testId = body.get("testId") == null ?
                    null : SafeConvert.safeConvertToInt(body.get("testId"));
            int studyPlanId = SafeConvert.safeConvertToInt(body.get("studyPlanId"));

            Milestone milestone = new Milestone();

            milestone.setName(name);
            milestone.setContent(content);
            milestone.setStartDate(startDate);
            milestone.setEndDate(endDate);
            milestone.setStudyPlanId(studyPlanId);

            session.save(milestone);

            List<Object> results = session.createNativeQuery("select children_id from children_join_study_plan " +
                            "where study_plan_id = :studyPlanId")
                    .setParameter("studyPlanId", studyPlanId)
                    .getResultList();

            List<Integer> childrenIds = results.stream()
                    .map(result -> ((Number) result).intValue())
                    .collect(Collectors.toList());
            
            String sql = "insert into test_in_milestone (milestone_id, children_id, test_id) " +
                    "values (:milestoneId, :childrenId, :testId)";

            for (Integer childrenId : childrenIds) {
                session.createNativeQuery(sql)
                        .setParameter("milestoneId", milestone.getId())
                        .setParameter("childrenId", childrenId)
                        .setParameter("testId", testId)
                        .executeUpdate();
            }

            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMilestone(Integer id, HashMap<String, Object> body) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String name = (String) body.get("name");
            String content = (String) body.get("content");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sdf.parse((String) body.get("startDate"));
            Date endDate = sdf.parse((String) body.get("endDate"));

            Integer testId = body.get("testId") == null ?
                    null : SafeConvert.safeConvertToInt(body.get("testId"));

            Milestone milestone = session.get(Milestone.class, id);

            milestone.setName(name);
            milestone.setContent(content);
            milestone.setStartDate(startDate);
            milestone.setEndDate(endDate);

            session.update(milestone);

            session.createNativeQuery("update test_in_milestone set test_id = :testId where milestone_id = :milestoneId")
                    .setParameter("testId", testId)
                    .setParameter("milestoneId", id)
                    .executeUpdate();

            transaction.commit();

            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMilestone(Integer id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "DELETE FROM Milestone WHERE id = :id";

            int rowsAffected = session.createQuery(hql)
                    .setParameter("id", id)
                    .executeUpdate();

            transaction.commit();

            return rowsAffected != 0;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
