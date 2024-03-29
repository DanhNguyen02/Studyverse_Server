package com.studyverse.server.DAO;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyverse.server.Model.Choice;
import com.studyverse.server.Model.Question;
import com.studyverse.server.Model.Test;
import com.studyverse.server.SafeConvert;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TestDAO {
    private final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    public boolean createTest(Map<String, Object> body) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String name = (String) body.get("name");
            String description = (String) body.get("description");
            int time = SafeConvert.safeConvertToInt(body.get("time"));
            int questionCount = SafeConvert.safeConvertToInt(body.get("questionCount"));
            int questionCountToPass = SafeConvert.safeConvertToInt(body.get("questionCountToPass"));
            int parentId = SafeConvert.safeConvertToInt(body.get("parentId"));

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
            test.setQuestionCount(questionCount);
            test.setQuestionCountToPass(questionCountToPass);
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

            session.getTransaction().commit();
        } catch (Exception e) {
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
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Question convertMapToQuestion(Map<String, Object> questionMap, Session session, int testId) {
        try {
            Question question = new Question();
            question.setContent((String) questionMap.get("content"));
            question.setDescription((String) questionMap.get("description"));
            question.setSuggest((String) questionMap.get("suggest"));
            String questionImageDataBase64 = (String) questionMap.get("image");
            if (questionImageDataBase64 != null && !questionImageDataBase64.isEmpty()) {
                byte[] imageBytes = Base64.getDecoder().decode(questionImageDataBase64);
                question.setImage(imageBytes);
            }
            else question.setImage(new byte[0]);
            int answerIndex = SafeConvert.safeConvertToInt(questionMap.get("answerId"));
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

            List<Map<String, Object>> choicesList = (List<Map<String, Object>>) questionMap.get("choices");
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
            e.printStackTrace();
            return null;
        }
    }
}