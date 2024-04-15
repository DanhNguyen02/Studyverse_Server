package com.studyverse.server.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "suggest")
    private String suggest;

    @Column(name = "image")
    private String image;

    @Column(name = "answer_id")
    @JsonIgnore
    private int answerId;

    @Column(name = "type")
    private int type;

    @Column(name = "test_id")
    @JsonIgnore
    private int testId;

    @Transient
    private Choice correctChoice;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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

    public Choice getCorrectChoice() {
        return correctChoice;
    }

    public void setCorrectChoice(Choice correctChoice) {
        this.correctChoice = correctChoice;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", content='" + name + '\'' +
                ", suggest='" + suggest + '\'' +
                ", image='" + image + '\'' +
                ", answerId=" + answerId +
                ", type=" + type +
                ", answers=" + choices +
                '}';
    }
}
