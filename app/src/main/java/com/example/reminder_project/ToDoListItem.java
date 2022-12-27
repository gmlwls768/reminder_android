package com.example.reminder_project;

public class ToDoListItem {
    private String title;
    private String priority;

    public ToDoListItem(String title, String priority){
        this.title = title;
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getPriority() {
        return priority;
    }
}
