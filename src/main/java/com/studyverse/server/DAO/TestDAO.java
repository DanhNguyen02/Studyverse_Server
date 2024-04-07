package com.studyverse.server.DAO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyverse.server.Model.*;
import com.studyverse.server.SafeConvert;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TestDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public boolean createTest(Map<String, Object> body) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String name = (String) body.get("name");
            String description = (String) body.get("description");
            int time = SafeConvert.safeConvertToInt(body.get("time"));
            int questionCountToPass = SafeConvert.safeConvertToInt(body.get("questionCountToPass"));
            int parentId = SafeConvert.safeConvertToInt(body.get("parentId"));
            String startDateString = (String) body.get("startDate");
            String endDateString = (String) body.get("endDate");

            Instant startDateInstant = Instant.parse(startDateString);
            Instant endDateInstant = Instant.parse(endDateString);

            LocalDateTime startDate = LocalDateTime.ofInstant(startDateInstant, ZoneId.of("UTC"));
            LocalDateTime endDate = LocalDateTime.ofInstant(endDateInstant, ZoneId.of("UTC"));

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

            // Convert tagList string to list
            List<Integer> tags = new ArrayList<>();
            if (body.get("tags") instanceof String tagsString) {
                tagsString = tagsString.replaceAll("\\[|\\]|\\s", "");
                String[] tagList = tagsString.split(",");
                for (String id : tagList) {
                    tags.add(Integer.parseInt(id.trim()));
                }
            } else if (body.get("tags") instanceof List<?>) {
                for (Object id : (List<?>) body.get("tags")) {
                    tags.add((Integer) id);
                }
            }

            // Insert test to session
            Test test = new Test();
            test.setName(name);
            test.setDescription(description);
            test.setTime(time);
            test.setQuestionCountToPass(questionCountToPass);
            test.setStartDate(startDate);
            test.setEndDate(endDate);
            test.setParentId(parentId);

            session.save(test);

            // Insert record into children_do_test table
            for (Integer childrenId : childrenIds) {
                String sql = "insert into children_do_test (children_id, test_id) values (:childrenId, :testId)";

                session.createNativeQuery(sql)
                        .setParameter("childrenId", childrenId)
                        .setParameter("testId", test.getId())
                        .executeUpdate();
            }

            // Insert record into test_have_tag table
            for (Integer tag : tags) {
                String sql = "insert into test_have_tag (tag_id, test_id) values (:tag, :testId)";

                session.createNativeQuery(sql)
                        .setParameter("tag", tag)
                        .setParameter("testId", test.getId())
                        .executeUpdate();
            }

            // Insert questions with choices into database
            List<Question> questions = new ArrayList<>();
            if (body.get("questions") instanceof String questionsString) {
                questions = convertStringToQuestions(questionsString, session, test.getId());
            } else if (body.get("questions") instanceof List<?>) {
                for (Object questionObj : (List<?>) body.get("questions")) {
                    Map<String, Object> questionMap = (Map<String, Object>) questionObj;
                    Question question = convertMapToQuestion(questionMap, session, test.getId());
                    questions.add(question);
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

    public List<Question> convertStringToQuestions(String questionsString, Session session, int testId) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Question> questions = objectMapper.readValue(questionsString, new TypeReference<List<Question>>(){});

            for (Question question : questions) {
                question.setTestId(testId);
                session.save(question);

                for (Integer tag : question.getTags()) {
                    String sql = "insert into question_have_tag (tag_id, question_id) values (:tag, :questionId)";

                    session.createNativeQuery(sql)
                            .setParameter("tag", tag)
                            .setParameter("questionId", question.getId())
                            .executeUpdate();
                }

                int answerIndex = question.getAnswerId();
                int index = 0;
                for (Choice choice : question.getChoices()) {
                    choice.setQuestionId(question.getId());

                    session.save(choice);
                    if (answerIndex == index) question.setAnswerId(choice.getId());
                    index++;
                }
            }

            return questions;
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Question convertMapToQuestion(Map<String, Object> questionMap, Session session, int testId) {
        try {
            Question question = new Question();
            question.setName((String) questionMap.get("name"));
            question.setDescription((String) questionMap.get("description"));
            question.setSuggest((String) questionMap.get("suggest"));
            String questionImageDataBase64 = (String) questionMap.get("image");
            if (questionImageDataBase64 != null && !questionImageDataBase64.isEmpty()) {
                byte[] imageBytes = Base64.getDecoder().decode(questionImageDataBase64);
                question.setImage(imageBytes);
            }
            else question.setImage(new byte[0]);
            int answerIndex = questionMap.get("answerId") != null ? SafeConvert.safeConvertToInt(questionMap.get("answerId")) : -1;
            question.setType(SafeConvert.safeConvertToInt(questionMap.get("type")));
            question.setTestId(testId);

            // Convert tagList string to list
            List<Integer> tags = new ArrayList<>();
            if (questionMap.get("tags") instanceof String tagsString) {
                tagsString = tagsString.replaceAll("\\[|\\]|\\s", "");
                String[] tagList = tagsString.split(",");
                for (String id : tagList) {
                    tags.add(Integer.parseInt(id.trim()));
                }
            } else if (questionMap.get("tags") instanceof List<?>) {
                for (Object id : (List<?>) questionMap.get("tags")) {
                    tags.add((Integer) id);
                }
            }

            question.setTags(tags);

            session.save(question);

            for (Integer tag : question.getTags()) {
                String sql = "insert into question_have_tag (tag_id, question_id) values (:tag, :questionId)";

                session.createNativeQuery(sql)
                        .setParameter("tag", tag)
                        .setParameter("questionId", question.getId())
                        .executeUpdate();
            }

            List<Map<String, Object>> choicesList = questionMap.get("choices") != null ?
                    (List<Map<String, Object>>) questionMap.get("choices") :
                    new ArrayList<>();
            List<Choice> choices = new ArrayList<>();

            int index = 0;
            for (Map<String, Object> choiceMap : choicesList) {
                Choice choice = new Choice();
                choice.setContent((String) choiceMap.get("content"));
                String choiceImageDataBase64 = (String) questionMap.get("image");
                if (choiceImageDataBase64 != null && !choiceImageDataBase64.isEmpty()) {
                    byte[] imageBytes = Base64.getDecoder().decode(choiceImageDataBase64);
                    choice.setImage(imageBytes);
                }
                else choice.setImage(new byte[0]);

                choice.setQuestionId(question.getId());

                session.save(choice);

                if (answerIndex == index) question.setAnswerId(choice.getId());
                index++;
                choices.add(choice);
            }

            question.setChoices(choices);

            return question;
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    public Map<Integer, List<Test>> getAllTests(Integer familyId) {
        Map<Integer, List<Test>> listMap = new HashMap<>();
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String getChildrenIdSql = "select * from user where family_id = :familyId and role = 0";

            List<Object[]> results = session.createNativeQuery(getChildrenIdSql)
                    .setParameter("familyId", familyId)
                    .getResultList();

            for (Object[] row : results) {
                int id = (Integer) row[0];

                String getTestIdListSql = "select test_id from children_do_test where children_id = :id";

                List<Object[]> testIds = session.createNativeQuery(getTestIdListSql)
                        .setParameter("id", id)
                        .getResultList();

                List<Integer> childrenIdList = new ArrayList<>();

                for (Object[] childrenDoTestRow : results) {
                    childrenIdList.add((Integer) childrenDoTestRow[0]);
                }

                List<Test> tests = new ArrayList<>();

                if (!childrenIdList.isEmpty()) {
                    String getTestsSql = "SELECT * FROM test t " +
                            "WHERE t.id IN (:testIds)";

                    String getQuestionsSql = "SELECT * FROM question q " +
                            "WHERE q.test_id IN (:testIds)";

                    String getChoicesSql = "SELECT * FROM choice c " +
                            "WHERE c.question_id IN (SELECT q.id FROM question q WHERE q.test_id IN (:testIds))";

                    tests = session.createNativeQuery(getTestsSql, Test.class)
                            .setParameter("testIds", testIds)
                            .getResultList();

                    List<Question> questions = session.createNativeQuery(getQuestionsSql, Question.class)
                            .setParameter("testIds", testIds)
                            .getResultList();

                    List<Choice> choices = session.createNativeQuery(getChoicesSql, Choice.class)
                            .setParameter("testIds", testIds)
                            .getResultList();

                    for (Question question : questions) {
                        int questionId = question.getId();

                        question.setChoices(choices.stream()
                                .filter(choice -> questionId == choice.getQuestionId())
                                .collect(Collectors.toList()));

                        if (question.getType() == 1) {
                            List<Choice> correctChoices = question.getChoices().stream()
                                    .filter(choice -> question.getAnswerId() == choice.getId())
                                    .toList();

                            question.setCorrectChoice(correctChoices.get(0));
                        }

                        String getTagsSql = "select tag_id from question_have_tag where question_id = :questionId";

                        List<Integer> tagList = session.createNativeQuery(getTagsSql)
                                .setParameter("questionId", question)
                                .getResultList();

                        question.setTags(tagList);
                    }

                    for (Test test : tests) {
                        int testId = test.getId();

                        test.setQuestions(questions.stream().
                                filter(question -> testId == question.getTestId()).
                                collect(Collectors.toList()));

                        String getTagsSql = "select tag_id from test_have_tag where test_id = :testId";

                        List<Integer> tagList = session.createNativeQuery(getTagsSql)
                                .setParameter("testId", testId)
                                .getResultList();

                        test.setTags(tagList);

                        // Get submissions
                        Query query = session.createQuery("FROM Submission WHERE testId = :testId")
                                .setParameter("testId", testId);
                        List<Submission> submissions = query.list();

                        for (Submission submission : submissions) {
                            String choiceSubmissionSql = "select c.* from choice_in_submission cis " +
                                    "inner join choice c on cis.choice_id = c.id where cis.submission_id = :submissionId";

                            List choiceSubmissionResults = session.createNativeQuery(choiceSubmissionSql)
                                    .setParameter("submissionId", submission.getId())
                                    .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                                    .list();

                            String answerSubmissionSql = "select * from answer_in_submission ais where ais.submission_id = :submissionId";

                            List answerSubmissionResults = session.createNativeQuery(answerSubmissionSql)
                                    .setParameter("submissionId", submission.getId())
                                    .setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
                                    .list();

                            Map<Integer, Object> answers = new HashMap<>();

                            for (Object choiceSubmission : choiceSubmissionResults) {
                                Map choiceSubmissionRow = (Map) choiceSubmission;

                                Choice choice = new Choice();
                                choice.setId(SafeConvert.safeConvertToInt(choiceSubmissionRow.get("id")));
                                choice.setContent((String) choiceSubmissionRow.get("content"));
                                int questionId = SafeConvert.safeConvertToInt(choiceSubmissionRow.get("question_id"));

                                answers.put(questionId, choice);
                            }

                            for (Object answerSubmission : answerSubmissionResults) {
                                Map answerSubmissionRow = (Map) answerSubmission;

                                EssayAnswer essayAnswer = new EssayAnswer();
                                essayAnswer.setAnswer((String) answerSubmissionRow.get("answer"));
                                essayAnswer.setIsPass(SafeConvert.safeConvertToInt(answerSubmissionRow.get("is_pass")));
                                int questionId = SafeConvert.safeConvertToInt(answerSubmissionRow.get("question_id"));

                                answers.put(questionId, essayAnswer);
                            }

                            submission.setAnswers(answers);
                        }

                        test.setSubmissions(submissions);
                    }
                }

                listMap.put(id, tests);
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

    public boolean submitTest(Map<String, Object> body) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String startDateString = (String) body.get("startDate");
            String endDateString = (String) body.get("endDate");

            Instant startDateInstant = Instant.parse(startDateString);
            Instant endDateInstant = Instant.parse(endDateString);

            LocalDateTime startDate = LocalDateTime.ofInstant(startDateInstant, ZoneId.of("UTC"));
            LocalDateTime endDate = LocalDateTime.ofInstant(endDateInstant, ZoneId.of("UTC"));

            int time = SafeConvert.safeConvertToInt(body.get("time"));
            int testId = SafeConvert.safeConvertToInt(body.get("testId"));
            int childrenId = SafeConvert.safeConvertToInt(body.get("childrenId"));

            Submission submission = new Submission();

            submission.setStartDate(startDate);
            submission.setEndDate(endDate);
            submission.setTime(time);
            submission.setTestId(testId);
            submission.setChildrenId(childrenId);

            session.save(submission);

            if (body.get("questions") instanceof String questionsString) {
                JSONArray questions = new JSONArray(questionsString);

                for (int i = 0; i < questions.length(); i++) {
                    JSONObject question = questions.getJSONObject(i);

                    int questionId = SafeConvert.safeConvertToInt(question.get("id"));
                    if (question.has("choiceId")) {
                        int choiceId = SafeConvert.safeConvertToInt(question.get("choiceId"));

                        String sql = "insert into choice_in_submission (submission_id, question_id, choice_id) values (:submissionId, :questionId, :choiceId)";

                        session.createNativeQuery(sql)
                                .setParameter("submissionId", submission.getId())
                                .setParameter("questionId", questionId)
                                .setParameter("choiceId", choiceId)
                                .executeUpdate();
                    }
                    else if (question.has("answer")) {
                        String answer = (String) question.get("answer");

                        String sql = "insert into answer_in_submission (submission_id, question_id, answer) values (:submissionId, :questionId, :answer)";

                        session.createNativeQuery(sql)
                                .setParameter("submissionId", submission.getId())
                                .setParameter("questionId", questionId)
                                .setParameter("answer", answer)
                                .executeUpdate();
                    }
                }
            }
            else if (body.get("questions") instanceof List) {
                List<Map<String, Object>> questionsList = (List<Map<String, Object>>) body.get("questions");

                for (Map<String, Object> questionMap : questionsList) {
                    int questionId = SafeConvert.safeConvertToInt(questionMap.get("id").toString());
                    if (questionMap.containsKey("choiceId")) {
                        int choiceId = SafeConvert.safeConvertToInt(questionMap.get("choiceId").toString());

                        String sql = "insert into choice_in_submission (submission_id, question_id, choice_id) values (:submissionId, :questionId, :choiceId)";

                        session.createNativeQuery(sql)
                                .setParameter("submissionId", submission.getId())
                                .setParameter("questionId", questionId)
                                .setParameter("choiceId", choiceId)
                                .executeUpdate();
                    }
                    else if (questionMap.containsKey("answer")) {
                        String answer = (String) questionMap.get("answer");

                        String sql = "insert into answer_in_submission (submission_id, question_id, answer) values (:submissionId, :questionId, :answer)";

                        session.createNativeQuery(sql)
                                .setParameter("submissionId", submission.getId())
                                .setParameter("questionId", questionId)
                                .setParameter("answer", answer)
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

    public boolean updateTest(Integer id, Map<String, Object> body) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String name = (String) body.get("name");
            String description = (String) body.get("description");
            int time = SafeConvert.safeConvertToInt(body.get("time"));
            int questionCountToPass = SafeConvert.safeConvertToInt(body.get("questionCountToPass"));
            String startDateString = (String) body.get("startDate");
            String endDateString = (String) body.get("endDate");

            Instant startDateInstant = Instant.parse(startDateString);
            Instant endDateInstant = Instant.parse(endDateString);

            LocalDateTime startDate = LocalDateTime.ofInstant(startDateInstant, ZoneId.of("UTC"));
            LocalDateTime endDate = LocalDateTime.ofInstant(endDateInstant, ZoneId.of("UTC"));

            Test test = session.get(Test.class, id);

            if (test != null) {
                test.setName(name);
                test.setDescription(description);
                test.setTime(time);
                test.setQuestionCountToPass(questionCountToPass);
                test.setStartDate(startDate);
                test.setEndDate(endDate);

                session.update(test);
            }

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

            // Update records into children_do_test table
            session.createNativeQuery("delete from children_do_test where test_id = :testId")
                    .setParameter("testId", id)
                    .executeUpdate();

            for (Integer childrenId : childrenIds) {
                String sql = "insert into children_do_test (children_id, test_id) values (:childrenId, :testId)";

                session.createNativeQuery(sql)
                        .setParameter("childrenId", childrenId)
                        .setParameter("testId", id)
                        .executeUpdate();
            }

            // Convert tagList string to list
            List<Integer> tags = new ArrayList<>();
            if (body.get("tags") instanceof String tagsString) {
                tagsString = tagsString.replaceAll("\\[|\\]|\\s", "");
                String[] tagList = tagsString.split(",");
                for (String tagId : tagList) {
                    tags.add(Integer.parseInt(tagId.trim()));
                }
            } else if (body.get("tags") instanceof List<?>) {
                for (Object tagId : (List<?>) body.get("tags")) {
                    tags.add((Integer) tagId);
                }
            }

            // Update records in test_have_tag table
            session.createNativeQuery("delete from test_have_tag where test_id = :testId")
                    .setParameter("testId", id)
                    .executeUpdate();

            for (Integer tag : tags) {
                String sql = "insert into test_have_tag (tag_id, test_id) values (:tag, :testId)";

                session.createNativeQuery(sql)
                        .setParameter("tag", tag)
                        .setParameter("testId", id)
                        .executeUpdate();
            }

            // Update questions
//            if (body.get("questions") instanceof String questionsString) {
//                updateQuestionsString(questionsString, session, id);
//            } else if (body.get("questions") instanceof List<?>) {
//                for (Object questionObj : (List<?>) body.get("questions")) {
//                    Map<String, Object> questionMap = (Map<String, Object>) questionObj;
//                    updateQuestionMap(questionMap, session, id);
//                }
//            }

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

//    public void updateQuestionsString(String questionString, Session session, Integer testId) {
//        Transaction transaction = session.getTransaction();
//
//        try {
//
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//    }

//    public void updateQuestionMap(Map<String, Object> questionMap, Session session, Integer testId) {
//        Transaction transaction = session.getTransaction();
//
//        try {
//            Integer questionId = SafeConvert.safeConvertToInt(questionMap.get("id"));
//
//            Question question = session.createQuery("FROM Question WHERE id = :id", Question.class)
//                    .setParameter("id", questionId)
//                    .uniqueResult();
//
//            String name = (String) questionMap.get("name");
//            String description = (String) questionMap.get("description");
//            String suggest = (String) questionMap.get("suggest");
//            byte[] image = null;
//            String questionImageDataBase64 = (String) questionMap.get("image");
//            if (questionImageDataBase64 != null && !questionImageDataBase64.isEmpty()) {
//                image = Base64.getDecoder().decode(questionImageDataBase64);
//            }
//            else image = new byte[0];
//            Choice correctChoice = (Choice) questionMap.get("correctChoice");
//
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//        }
//    }

    public boolean deleteTest(Integer id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String hql = "DELETE FROM Test WHERE id = :id";

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
