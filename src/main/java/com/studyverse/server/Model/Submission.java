package com.studyverse.server.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "submission")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "time")
    private int time;

    @Column(name = "test_id")
    @JsonIgnore
    private int testId;

    @Column(name = "children_id")
    @JsonIgnore
    private int childrenId;

    @Transient
    private Map<Integer, Object> answers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTestId() {
        return testId;
    }

    public void setTestId(int testId) {
        this.testId = testId;
    }

    public int getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(int childrenId) {
        this.childrenId = childrenId;
    }

    public Map<Integer, Object> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<Integer, Object> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", time=" + time +
                ", testId=" + testId +
                ", childrenId=" + childrenId +
                ", answers=" + answers +
                '}';
    }
}
