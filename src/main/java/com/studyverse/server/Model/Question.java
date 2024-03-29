package com.studyverse.server.Model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "content")
    private String content;

    @Column(name = "description")
    private String description;

    @Column(name = "suggest")
    private String suggest;

    @Column(name = "image")
    private byte[] image;

    @Column(name = "answer_id")
    private int answerId;

    @Column(name = "type")
    private int type;

    @Column(name = "test_id")
    private int testId;

    @Transient
    private List<Choice> choices;

    @Transient
    private List<Integer> tags;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<Choice> getChoices() {
        return choices;
    }

    public void setChoices(List<Choice> choices) {
        this.choices = choices;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public List<Integer> getTags() {
        return tags;
    }

    public void setTags(List<Integer> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", suggest='" + suggest + '\'' +
                ", image='" + Arrays.toString(image) + '\'' +
                ", answerId=" + answerId +
                ", type=" + type +
                ", answers=" + choices +
                '}';
    }
}
