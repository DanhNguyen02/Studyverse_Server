package com.studyverse.server.DAO;

import com.studyverse.server.Model.StudyPlan;
import com.studyverse.server.SafeConvert;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class StudyPlanDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public Map<Integer, List<StudyPlan>> getAllStudyPlans(Integer familyId) {
        Map<Integer, List<StudyPlan>> listMap = new HashMap<>();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return new HashMap<>();
        }
        return listMap;
    }

    public boolean createStudyPlan(HashMap<String, Object> body) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String name = (String) body.get("name");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sdf.parse((String) body.get("startDate"));
            Date endDate = sdf.parse((String) body.get("endDate"));

            int subjectId = SafeConvert.safeConvertToInt(body.get("subjectId"));

            // Convert childrenIdList string to list
            List<Integer> childrenIds = new ArrayList<>();
            if (body.get("childrenIds") instanceof String childrenIdsString) {
                childrenIdsString = childrenIdsString.replaceAll("\\[|\\]|\\s", "");
                String[] ids = childrenIdsString.split(",");
                for (String id : ids) {
                    childrenIds.add(Integer.parseInt(id.trim()));
                }
            } else if (body.get("childrenIds") instanceof List<?>) {
                for (Object id : (List<?>) body.get("childrenIds")) {
                    childrenIds.add((Integer) id);
                }
            }

            StudyPlan studyPlan = new StudyPlan();
            studyPlan.setName(name);
            studyPlan.setStartDate(startDate);
            studyPlan.setEndDate(endDate);
            studyPlan.setSubjectId(subjectId);

            session.save(studyPlan);

            // Insert record into children_join_study_plan table
            for (Integer childrenId : childrenIds) {
                String sql = "insert into children_join_study_plan (study_plan_id, children_id) " +
                        "values (:studyPlanId, :childrenId)";

                session.createNativeQuery(sql)
                        .setParameter("studyPlanId", studyPlan.getId())
                        .setParameter("childrenId", childrenId)
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

    public boolean updateStudyPlan(Integer id, HashMap<String, Object> body) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String name = (String) body.get("name");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date startDate = sdf.parse((String) body.get("startDate"));
            Date endDate = sdf.parse((String) body.get("endDate"));

            int subjectId = SafeConvert.safeConvertToInt(body.get("subjectId"));

            // Convert childrenIdList string to list
            List<Integer> childrenIds = new ArrayList<>();
            if (body.get("childrenIds") instanceof String childrenIdsString) {
                childrenIdsString = childrenIdsString.replaceAll("\\[|\\]|\\s", "");
                String[] ids = childrenIdsString.split(",");
                for (String childrenId : ids) {
                    childrenIds.add(Integer.parseInt(childrenId.trim()));
                }
            } else if (body.get("childrenIds") instanceof List<?>) {
                for (Object childrenId : (List<?>) body.get("childrenIds")) {
                    childrenIds.add((Integer) childrenId);
                }
            }

            StudyPlan studyPlan = session.get(StudyPlan.class, id);

            if (studyPlan != null) {
                studyPlan.setName(name);
                studyPlan.setStartDate(startDate);
                studyPlan.setEndDate(endDate);
                studyPlan.setSubjectId(subjectId);

                session.update(studyPlan);
            }

            // Update records into children_do_test table
            session.createNativeQuery("delete from children_join_study_plan where study_plan_id = :studyPlanId")
                    .setParameter("studyPlanId", id)
                    .executeUpdate();

            for (Integer childrenId : childrenIds) {
                String sql = "insert into children_join_study_plan (study_plan_id, children_id) " +
                        "values (:studyPlanId, :childrenId)";

                session.createNativeQuery(sql)
                        .setParameter("studyPlanId", id)
                        .setParameter("childrenId", childrenId)
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

    public boolean deleteStudyPlan(Integer id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "DELETE FROM StudyPlan WHERE id = :id";

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
