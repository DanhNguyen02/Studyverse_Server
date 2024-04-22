package com.studyverse.server.DAO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyverse.server.Model.Milestone;
import com.studyverse.server.Model.StudyPlan;
import com.studyverse.server.SafeConvert;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class StudyPlanDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public Map<Integer, Map<Integer, List<StudyPlan>>> getAllStudyPlans(Integer familyId) {
        Map<Integer, Map<Integer, List<StudyPlan>>> listMap = new HashMap<>();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String getChildrenIdsSql = "select * from user where family_id = :familyId and role = 0";

            List<Object[]> results = session.createNativeQuery(getChildrenIdsSql)
                    .setParameter("familyId", familyId)
                    .getResultList();

            for (Object[] row : results) {
                Map<Integer, List<StudyPlan>> studyPlanSubjectList = new HashMap<>();

                int childrenId = (Integer) row[0];

                String getStudyPlanIdListSql = "select study_plan_id from children_join_study_plan " +
                        "where children_id = :id";

                List<Object[]> studyPlanIds = session.createNativeQuery(getStudyPlanIdListSql)
                        .setParameter("id", childrenId)
                        .getResultList();

                for (int subjectId = 1; subjectId <= 12; subjectId++) {
                    String getStudyPlansSql = "select * from study_plan where id in (:studyPlanIds) and subject_id = :subjectId";

                    List<StudyPlan> studyPlanList = session.createNativeQuery(getStudyPlansSql, StudyPlan.class)
                            .setParameter("studyPlanIds", studyPlanIds)
                            .setParameter("subjectId", subjectId)
                            .getResultList();

                    studyPlanSubjectList.put(subjectId, studyPlanList);
                }

                listMap.put(childrenId, studyPlanSubjectList);
            }

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

            if (body.get("milestones") instanceof String milestonesString) {
                JSONArray milestones = new JSONArray(milestonesString);

                for (int i = 0; i < milestones.length(); i++) {
                    JSONObject milestoneObject = milestones.getJSONObject(i);

                    Milestone milestone = new Milestone();

                    milestone.setName((String) milestoneObject.get("name"));
                    milestone.setContent((String) milestoneObject.get("content"));
                    milestone.setStartDate(sdf.parse((String) milestoneObject.get("startDate")));
                    milestone.setEndDate(sdf.parse((String) milestoneObject.get("endDate")));
                    milestone.setStudyPlanId(studyPlan.getId());

                    session.save(milestone);

                    Integer testId = milestoneObject.get("testId") == JSONObject.NULL ?
                            null : SafeConvert.safeConvertToInt(milestoneObject.get("testId"));

                    String sql = "insert into test_in_milestone (milestone_id, children_id, test_id) " +
                            "values (:milestoneId, :childrenId, :testId)";

                    for (Integer childrenId : childrenIds) {
                        session.createNativeQuery(sql)
                                .setParameter("milestoneId", milestone.getId())
                                .setParameter("childrenId", childrenId)
                                .setParameter("testId", testId)
                                .executeUpdate();
                    }
                }
            } else if (body.get("milestones") instanceof List<?>) {
                List<Map<String, Object>> milestones = (List<Map<String, Object>>) body.get("milestones");

                for (Map<String, Object> milestoneMap : milestones) {
                    Milestone milestone = new Milestone();

                    milestone.setName((String) milestoneMap.get("name"));
                    milestone.setContent((String) milestoneMap.get("content"));
                    milestone.setStartDate(sdf.parse((String) milestoneMap.get("startDate")));
                    milestone.setEndDate(sdf.parse((String) milestoneMap.get("endDate")));
                    milestone.setStudyPlanId(studyPlan.getId());

                    session.save(milestone);

                    Integer testId = milestoneMap.get("testId") == null ?
                            null : SafeConvert.safeConvertToInt(milestoneMap.get("testId"));

                    String sql = "insert into test_in_milestone (milestone_id, children_id, test_id) " +
                            "values (:milestoneId, :childrenId, :testId)";

                    for (Integer childrenId : childrenIds) {
                        session.createNativeQuery(sql)
                                .setParameter("milestoneId", milestone.getId())
                                .setParameter("childrenId", childrenId)
                                .setParameter("testId", testId)
                                .executeUpdate();
                    }
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
