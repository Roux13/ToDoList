package ru.nehodov.todolist.models;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import ru.nehodov.todolist.utils.DateTimeFormatter;

public class Task implements Serializable {

    private int id;
    private String name;
    private String desc;
    private String created;
    private String done;
    private String photoPath;

    public Task(String name, String desc, String created, String done) {
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.done = done;
        this.photoPath = "";
    }

    public Task(int id, String name, String desc, String created, String done) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.created = created;
        this.done = done;
        this.photoPath = "";
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

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(name, task.name) &&
                Objects.equals(desc, task.desc) &&
                Objects.equals(created, task.created) &&
                Objects.equals(done, task.done) &&
                Objects.equals(photoPath, task.photoPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, desc, created, done, photoPath);
    }
}
