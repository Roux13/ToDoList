package ru.nehodov.todolist.stores;

public final class TaskDbContract {

    private TaskDbContract() {

    }

    public static class TasksTable {

        public static final String TABLE_NAME = "tasks";

        public static final String COLUMN_NAME_ID = "_ID";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESC = "desc";
        public static final String COLUMN_NAME_CREATED = "created";
        public static final String COLUMN_NAME_DONE = "done";
        public static final String COLUMN_NAME_PHOTO_PATH = "photo_uri";

    }

}
