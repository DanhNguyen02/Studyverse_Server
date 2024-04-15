package com.studyverse.server.DAO;

import com.studyverse.server.SafeConvert;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SubmissionDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public boolean scoringTest(HashMap<String, Object> body) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            int submissionId = SafeConvert.safeConvertToInt(body.get("id"));

            if (body.get("questions") instanceof String questionsString) {
                JSONArray questions = new JSONArray(questionsString);

                for (int i = 0; i < questions.length(); i++) {
                    JSONObject question = questions.getJSONObject(i);

                    int questionId = SafeConvert.safeConvertToInt(question.get("id"));
                    int isPass = SafeConvert.safeConvertToBoolean(question.get("isPass")) ? 1 : -1;

                    String sql = "update answer_in_submission set is_pass = :isPass where submission_id = :submissionId and question_id = :questionId";

                    int rowsAffected = session.createNativeQuery(sql)
                            .setParameter("isPass", isPass)
                            .setParameter("submissionId", submissionId)
                            .setParameter("questionId", questionId)
                            .executeUpdate();

                    if (rowsAffected != 0) return false;
                }
            }

            else if (body.get("questions") instanceof List) {
                List<Map<String, Object>> questionsList = (List<Map<String, Object>>) body.get("questions");

                for (Map<String, Object> questionMap : questionsList) {
                    int questionId = SafeConvert.safeConvertToInt(questionMap.get("id"));
                    int isPass = SafeConvert.safeConvertToBoolean(questionMap.get("isPass")) ? 1 : -1;

                    String sql = "update answer_in_submission set is_pass = :isPass where submission_id = :submissionId and question_id = :questionId";

                    int rowsAffected = session.createNativeQuery(sql)
                            .setParameter("isPass", isPass)
                            .setParameter("submissionId", submissionId)
                            .setParameter("questionId", questionId)
                            .executeUpdate();

                    if (rowsAffected == 0) return false;
                }
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
}
