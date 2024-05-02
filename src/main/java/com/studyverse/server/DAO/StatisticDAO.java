package com.studyverse.server.DAO;

import com.studyverse.server.Model.Question;
import com.studyverse.server.Model.Submission;
import com.studyverse.server.Model.Test;
import com.studyverse.server.SafeConvert;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class StatisticDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public Map<String, Object> getTestStatistic(Integer id) {
        Map<String, Object> listMap = new HashMap<>();
        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            List<Integer> testIdObjects = session.createNativeQuery("select test_id from children_do_test " +
                    "where children_id = :childrenId")
                    .setParameter("childrenId", id)
                    .getResultList();

            Integer testCount = 0, testPass = 0;

            for (Integer testId : testIdObjects) {
                testCount++;

                List<Submission> submissions = session.createQuery("FROM Submission WHERE childrenId = :childrenId " +
                                "AND testId = :testId", Submission.class)
                        .setParameter("childrenId", id)
                        .setParameter("testId", testId)
                        .getResultList();

                List<Question> questions = session.createQuery("FROM Question WHERE testId = :testId", Question.class)
                        .setParameter("testId", testId)
                        .getResultList();

                Map<Integer, Question> questionMap = questions.stream()
                        .collect(Collectors.toMap(Question::getId, question -> question));

                for (Submission submission : submissions) {
                    List<Object[]> choiceObjects = session.createNativeQuery("select * from choice_in_submission " +
                                    "where submission_id = :submissionId")
                            .setParameter("submissionId", submission.getId())
                            .getResultList();

                    List<Object[]> essayAnswerObjects = session.createNativeQuery("select * from answer_in_submission " +
                                    "where submission_id = :submissionId")
                            .setParameter("submissionId", submission.getId())
                            .getResultList();

                    int count = 0;
                    boolean canGrade = true;

                    for (Object[] choiceObject : choiceObjects) {
                        Integer questionId = (Integer) choiceObject[1];
                        Integer choiceId = (Integer) choiceObject[2];

                        Integer answerId = questionMap.get(questionId).getAnswerId();

                        if (Objects.equals(choiceId, answerId)) count++;
                    }

                    for (Object[] essayAnswerObject : essayAnswerObjects) {
                        if ((Integer) essayAnswerObject[3] == 1) count++;
                        else if ((Integer) essayAnswerObject[3] == 0) canGrade = false;
                    }

                    Test test = session.get(Test.class, testId);

                    if (canGrade && count >= test.getQuestionCountToPass()) {
                        testPass++;
                        break;
                    }
                }
            }

            listMap.put("pass", testPass);
            listMap.put("count", testCount);

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

    public Map<String, Object> getQuestionStatistic(Integer id) {
        Map<String, Object> listMap = new HashMap<>();
        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
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

    public Map<String, Object> getSubjectStatistic(Integer id) {
        Map<String, Object> listMap = new HashMap<>();
        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
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
}
