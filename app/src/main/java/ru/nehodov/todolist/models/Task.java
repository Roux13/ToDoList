package ru.nehodov.todolist.models;

import java.io.Serializable;
import java.util.Date;

import ru.nehodov.todolist.utils.DateTimeFormatter;

public class Task implements Serializable {

    private int id;
    private String name;
    private String desc;
    private String created;
    private String done;

    public Task(String name, String desc, String created, String done) {
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.done = done;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Task(String name, String desc, String created) {
        this(name, desc, created, "");
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

    public String getCreated() {
        return created;
    }

    public String getDoneDate() {
        return done;
    }

    public void doTask() {
        this.done = DateTimeFormatter.format(new Date());
    }


}
