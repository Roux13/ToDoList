package ru.nehodov.todolist.stores;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import ru.nehodov.todolist.models.Task;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {21, 22, 23, 24, 25, 26, 27, 28})
public class FileStoreTest {

    private Context context;

    private final String name = "Name";
    private final String desc = "Desc";
    private final String created = "01.01.2020";
    private final String doneDate = "15.01.2020";

    @Before
    public void before() throws NoSuchFieldException, IllegalAccessException {
        context = InstrumentationRegistry.getInstrumentation().getContext();
        Field instance = FileStore.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void addTaskWhenAddThenFileContainsCorrectContent(){
        Task task = new Task(0, name, desc, created, doneDate);
        IStore store = FileStore.getInstance(context);

        store.addTask(task);
        File[] files = context.getFilesDir().listFiles();
        try (BufferedReader reader = new BufferedReader(new FileReader(files[0]))) {

            assertThat(reader.readLine(), is("0"));
            assertThat(reader.readLine(), is(name));
            assertThat(reader.readLine(), is(desc));
            assertThat(reader.readLine(), is(created));
            assertThat(reader.readLine(), is(doneDate));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void getTaskWhenFileExistsThenWeGetTheFile(){
        int taskId = 0;
        File file = new File(context.getFilesDir(),taskId + ".txt");
        if (file.exists()) {
            file.delete();
        }
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.println(taskId);
            writer.println(name);
            writer.println(desc);
            writer.println(created);
            writer.println(doneDate);
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, context.getFilesDir().listFiles().length);
        assertTrue(context.getFilesDir().listFiles()[0].exists());
        assertEquals(taskId + ".txt", context.getFilesDir().listFiles()[0].getName());

    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void getTaskWhenGetThenCorrectContentOfTask(){
        int taskId = 0;
        File file = new File(context.getFilesDir(),taskId + ".txt");
        if (file.exists()) {
            file.delete();
        }
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
            writer.println(taskId);
            writer.println(name);
            writer.println(desc);
            writer.println(created);
            writer.println(doneDate);
        } catch (IOException e) {
            e.printStackTrace();
        }

        IStore store = FileStore.getInstance(context);
        Task actual = store.getTask(taskId);

        assertThat(actual.getName(), is(name));
        assertThat(actual.getDesc(), is(desc));
        assertThat(actual.getCreated(), is(created));
        assertThat(actual.getDoneDate(), is(doneDate));

    }

    @Test
    public void getAllTasksWhen5Tasks() {
        for (int taskId = 0; taskId < 5; taskId++) {
            File file = new File(context.getFilesDir(), taskId + ".txt");
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                writer.println(taskId);
                writer.println(name);
                writer.println(desc);
                writer.println(created);
                writer.println(doneDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        IStore store = FileStore.getInstance(context);
        List<Task> tasks = store.getTasks();

        assertEquals(5, tasks.size());
    }

    @Test
    public void deleteTaskWhenWereFiveTasksAndTaskWithId2Deleted() {
        for (int taskId = 0; taskId < 5; taskId++) {
            File file = new File(context.getFilesDir(), taskId + ".txt");
            if (file.exists()) {
                file.delete();
            }
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                writer.println(taskId);
                writer.println(name);
                writer.println(desc);
                writer.println(created);
                writer.println(doneDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] expected = {"0.txt", "1.txt", "3.txt", "4.txt"};

        IStore store = FileStore.getInstance(context);
        store.deleteTask(2);

        assertArrayEquals(expected, context.getFilesDir().list());
    }

    @Test
    public void deleteAllTasksWhenWereFiveTasks() {
        for (int taskId = 0; taskId < 5; taskId++) {
            File file = new File(context.getFilesDir(), taskId + ".txt");
            if (file.exists()) {
                file.delete();
            }
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                writer.println(taskId);
                writer.println(name);
                writer.println(desc);
                writer.println(created);
                writer.println(doneDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] expected = {};

        IStore store = FileStore.getInstance(context);
        store.deleteAll();

        assertArrayEquals(expected, context.getFilesDir().list());
    }

    @Test
    public void searchTasksWhenWereFiveTasksAndQueryIsNam() {
        String query = "Nam";
        for (int taskId = 0; taskId < 5; taskId++) {
            File file = new File(context.getFilesDir(), taskId + ".txt");
            if (file.exists()) {
                file.delete();
            }
            try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                writer.println(taskId);
                writer.println(name + taskId);
                writer.println(desc);
                writer.println(created);
                writer.println(doneDate);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Task task0 = new Task(0,name + 0, desc, created, doneDate);
        Task task1 = new Task(1,name + 1, desc, created, doneDate);
        Task task2 = new Task(2,name + 2, desc, created, doneDate);
        Task task3 = new Task(3,name + 3, desc, created, doneDate);
        Task task4 = new Task(4,name + 4, desc, created, doneDate);

        List<Task> expected = Arrays.asList(task0, task1, task2, task3, task4);

        IStore store = FileStore.getInstance(context);
        List<Task> actual = store.searchTasks(query);

        assertThat(actual, is(expected));
    }
}