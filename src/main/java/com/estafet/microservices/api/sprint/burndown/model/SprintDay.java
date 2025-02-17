package com.estafet.microservices.api.sprint.burndown.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "SPRINT_DAY")
public class SprintDay {

    @Id
    @SequenceGenerator(name = "SPRINT_DAY_ID_SEQ", sequenceName = "SPRINT_DAY_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SPRINT_DAY_ID_SEQ")
    @Column(name = "SPRINT_DAY_ID")
    private Integer id;

    @Column(name = "DAY_NO", nullable = false)
    private Integer dayNo;

    @Column(name = "HOURS_TOTAL")
    private Integer hoursTotal;

    @Transient
    private Float idealHours;

    @Column(name = "SPRINT_DAY", nullable = false)
    private String sprintDay;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "SPRINT_ID", nullable = false, referencedColumnName = "SPRINT_ID", foreignKey = @ForeignKey(name = "SPRINT_DAY_TO_SPRINT_FK"))
    private Sprint sprintDaySprint;

    @JsonIgnore
    @OneToMany(mappedBy = "taskUpdateSprintDay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<TaskUpdate> updates = new HashSet<TaskUpdate>();

    void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public Integer getDayNo() {
        return dayNo;
    }

    public String getSprintDay() {
        return sprintDay;
    }

    public Sprint getSprintDaySprint() {
        return sprintDaySprint;
    }

    public Integer getHoursTotal() {
        return hoursTotal;
    }

    public Set<TaskUpdate> getUpdates() {
        return updates;
    }

    public Float getIdealHours() {
        return idealHours;
    }

    public void setIdealHours(Float idealHours) {
        this.idealHours = idealHours;
    }

    public SprintDay setHoursTotal(Integer hoursTotal) {
        this.hoursTotal = hoursTotal;
        return this;
    }

    public SprintDay setDayNo(Integer dayNo) {
        this.dayNo = dayNo;
        return this;
    }

    public SprintDay setSprintDay(String sprintDay) {
        this.sprintDay = sprintDay;
        return this;
    }

    public SprintDay setSprintDaySprint(Sprint sprintDaySprint) {
        this.sprintDaySprint = sprintDaySprint;
        return this;
    }

    private TaskUpdate geTaskUpdate(Integer taskId) {
        for (TaskUpdate update : updates) {
            if (update.getTaskId().equals(taskId)) {
                return update;
            }
        }
        return null;
    }

    public void update(Task task) {
        TaskUpdate update = geTaskUpdate(task.getId());
        if (update == null) {
            update = new TaskUpdate().setTaskId(task.getId()).setTaskUpdateSprintDay(this);
            updates.add(update);
        }
        update.setRemainingHours(task.getRemainingHours());
    }

    public void backfill(Task task) {
        TaskUpdate update = geTaskUpdate(task.getId());
        if (update == null) {
            update = new TaskUpdate().setTaskId(task.getId()).setTaskUpdateSprintDay(this);
            updates.add(update);
            update.setRemainingHours(task.getRemainingHours());
        }
    }

    public void recalculate() {
        if (!updates.isEmpty()) {
            this.hoursTotal = 0;
            for (TaskUpdate update : updates) {
                hoursTotal += update.getRemainingHours();
            }
        }
    }

    public static SprintDay getAPI() {
        SprintDay day = new SprintDay();
        day.id = 1;
        day.dayNo = 1;
        day.hoursTotal = 10;
        day.sprintDay = "2017-10-16 00:00:00";
        return day;
    }

}
