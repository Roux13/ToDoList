package ru.nehodov.todolist.models;

import java.io.Serializable;
import java.util.Calendar;

public class Task implements Serializable {

    private String name;
    private String desc;
    private Calendar created;
    private Calendar done;

    public Task(String name, String desc, Calendar created) {
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.done = null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Calendar getCreated() {
        return created;
    }

    public Calendar getDoneDate() {
        return done;
    }

    public void doTask() {
        this.done = Calendar.getInstance();
    }
}
