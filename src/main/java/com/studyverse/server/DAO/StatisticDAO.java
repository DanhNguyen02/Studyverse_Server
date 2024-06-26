package com.studyverse.server.DAO;

import com.studyverse.server.Model.Question;
import com.studyverse.server.Model.Submission;
import com.studyverse.server.Model.Test;
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

    public Map<String, Map<String, Object>> getStatistics(Integer id) {
        Map<String, Map<String, Object>> listMap = new HashMap<>();
        Session session;
        Transaction transaction = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            Map<String, Object> testStatistics = new HashMap<>();
            Map<String, Object> answerStatistics = new HashMap<>();
            Map<String, Object> allSubjectStatistics = new HashMap<>();

            // Test and answer statistics

            List<Integer> testIds = session.createNativeQuery("select test_id from children_do_test " +
                            "where children_id = :childrenId")
                    .setParameter("childrenId", id)
                    .getResultList();

            Integer testCount = 0, testPass = 0;
            Integer answerCount = 0, answerCorrect = 0;

            for (Integer testId : testIds) {
                boolean isPass = false;
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
                        answerCount++;
                        Integer questionId = (Integer) choiceObject[1];
                        Integer choiceId = (Integer) choiceObject[2];

                        Integer answerId = questionMap.get(questionId).getAnswerId();

                        if (Objects.equals(choiceId, answerId)) {
                            count++;
                            answerCorrect++;
                        }
                    }

                    for (Object[] essayAnswerObject : essayAnswerObjects) {
                        answerCount++;
                        if ((Integer) essayAnswerObject[3] == 1) {
                            count++;
                            answerCorrect++;
                        }
                        else if ((Integer) essayAnswerObject[3] == 0) canGrade = false;
                    }

                    Test test = session.get(Test.class, testId);

                    if (canGrade && count >= test.getQuestionCountToPass()) {
                        isPass = true;
                    }
                }

                if (isPass) testPass++;
            }

            testStatistics.put("pass", testPass);
            testStatistics.put("count", testCount);

            answerStatistics.put("correct", answerCorrect);
            answerStatistics.put("count", answerCount);

            // Subject statistics

            List<Integer> submissionIds = session.createNativeQuery("select id from submission " +
                            "where children_id = :childrenId")
                    .setParameter("childrenId", id)
                    .getResultList();

            String choicesSql = "select cis.choice_id, q.answer_id from choice_in_submission cis inner join " +
                    "(question q inner join question_have_tag qht on " +
                    "q.id = qht.question_id) on cis.question_id = q.id " +
                    "where cis.submission_id IN :submissionIds and qht.tag_id = :tagId";

            String answersSql = "select ais.is_pass from answer_in_submission ais inner join " +
                    "(question q inner join question_have_tag qht on " +
                    "q.id = qht.question_id) on ais.question_id = q.id " +
                    "where ais.submission_id in :submissionIds and qht.tag_id = :tagId";

            for (int tag = 1; tag <= 6; tag++) {
                Integer questionCount = 0, questionCorrect = 0;

                List<Object[]> choiceObjects = session.createNativeQuery(choicesSql)
                        .setParameter("submissionIds", submissionIds)
                        .setParameter("tagId", tag)
                        .getResultList();

                for (Object[] choiceObject : choiceObjects) {
                    questionCount++;
                    if (Objects.equals(choiceObject[0], choiceObject[1])) questionCorrect++;
                }

                List<Integer> isPassObjects = session.createNativeQuery(answersSql)
                        .setParameter("submissionIds", submissionIds)
                        .setParameter("tagId", tag)
                        .getResultList();

                for (Integer isPass : isPassObjects) {
                    questionCount++;
                    if (isPass == 1) questionCorrect++;
                }

                Map<String, Object> subjectStatistics = new HashMap<>();

                subjectStatistics.put("correct", questionCorrect);
                subjectStatistics.put("count", questionCount);

                allSubjectStatistics.put(String.valueOf(tag), subjectStatistics);
            }

            listMap.put("test", testStatistics);
            listMap.put("answer", answerStatistics);
            listMap.put("subject", allSubjectStatistics);

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
