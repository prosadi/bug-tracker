package models;

import java.util.ArrayList;
import java.util.List;

public class Bug {
    private int id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private String assignedTo;
    private List<String> comments;

    public Bug(int id, String title, String description, Status status, Priority priority, String assignedTo) {
        this(id, title, description, status, priority, assignedTo, new ArrayList<>());
    }

    public Bug(int id, String title, String description, Status status, Priority priority, String assignedTo, List<String> comments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.assignedTo = assignedTo;
        this.comments = comments != null ? comments : new ArrayList<>();
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Status getStatus() { return status; }
    public Priority getPriority() { return priority; }
    public String getAssignedTo() { return assignedTo; }
    public List<String> getComments() { return comments; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(Status status) { this.status = status; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }
    public void addComment(String comment) { this.comments.add(comment); }

    public String getCommentsAsString() {
        if (comments.isEmpty()) {
            return "No comments.";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < comments.size(); i++) {
            sb.append(i + 1).append(". ").append(comments.get(i)).append("\n");
        }
        return sb.toString().trim();
    }
}
