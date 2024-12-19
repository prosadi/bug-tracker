package managers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import models.Bug;
import models.Priority;
import models.Status;

public class BugManager {
    private static final String FILE_PATH = "data/bugs.txt";

    /**
     * Load bugs from the file.
     * 
     * @return list of bugs
     */
    public List<Bug> loadBugs() {
        List<Bug> bugs = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return bugs;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                // Expected fields: id,title,description,status,priority,assignedTo,comments
                if (parts.length == 7) {
                    int id = Integer.parseInt(parts[0]);
                    String title = parts[1];
                    String description = parts[2];
                    Status status = stringToStatus(parts[3]);
                    Priority priority = stringToPriority(parts[4]);
                    String assignedTo = parts[5];

                    List<String> comments = new ArrayList<>();
                    String commentsField = parts[6].trim();
                    if (!commentsField.isEmpty()) {
                        String[] commentParts = commentsField.split("\\|");
                        for (String c : commentParts) {
                            comments.add(c);
                        }
                    }

                    // Ensure status and priority defaults if null
                    if (status == null) status = Status.NEW;
                    if (priority == null) priority = Priority.LOW;

                    Bug bug = new Bug(id, title, description, status, priority, assignedTo, comments);
                    bugs.add(bug);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading bugs: " + e.getMessage());
        }
        return bugs;
    }

    /**
     * Save bugs to the file.
     * 
     * @param bugs list of bugs to save
     */
    public void saveBugs(List<Bug> bugs) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Bug bug : bugs) {
                String commentsStr = String.join("|", bug.getComments());
                bw.write(String.format("%d,%s,%s,%s,%s,%s,%s\n",
                        bug.getId(),
                        bug.getTitle(),
                        bug.getDescription(),
                        bug.getStatus().name(),
                        bug.getPriority().name(),
                        bug.getAssignedTo(),
                        commentsStr));
            }
        } catch (IOException e) {
            System.err.println("Error saving bugs: " + e.getMessage());
        }
    }

    /**
     * Add a new bug to the system.
     * 
     * @param bug the bug to add
     */
    public void addBug(Bug bug) {
        List<Bug> bugs = loadBugs();
        bugs.add(bug);
        saveBugs(bugs);
    }

    /**
     * Update the entire list of bugs (overwrite).
     * 
     * @param bugs list of updated bugs
     */
    public void updateBugs(List<Bug> bugs) {
        saveBugs(bugs);
    }

    /**
     * Find a bug by its ID.
     * 
     * @param id the ID of the bug
     * @return the found bug, or null if not found
     */
    public Bug findBugById(int id) {
        List<Bug> bugs = loadBugs();
        for (Bug b : bugs) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    /**
     * Delete a bug by its ID.
     * 
     * @param id the ID of the bug to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteBug(int id) {
        List<Bug> bugs = loadBugs();
        Bug found = null;
        for (Bug b : bugs) {
            if (b.getId() == id) {
                found = b;
                break;
            }
        }
        if (found == null) return false;
        bugs.remove(found);
        saveBugs(bugs);
        return true;
    }

    /**
     * Update a specific field of a bug by its ID.
     * 
     * @param id the ID of the bug to update
     * @param fieldChoice the field to update (1-5)
     * @param newValue the new value for the field
     * @return true if updated, false if not found
     */
    public boolean updateBugField(int id, int fieldChoice, String newValue) {
        List<Bug> bugs = loadBugs();
        for (int i = 0; i < bugs.size(); i++) {
            Bug b = bugs.get(i);
            if (b.getId() == id) {
                switch (fieldChoice) {
                    case 1: b.setTitle(newValue); break;
                    case 2: b.setDescription(newValue); break;
                    case 3:
                        Status s = stringToStatus(newValue);
                        if (s == null) s = Status.NEW;
                        b.setStatus(s);
                        break;
                    case 4:
                        Priority p = stringToPriority(newValue);
                        if (p == null) p = Priority.LOW;
                        b.setPriority(p);
                        break;
                    case 5: b.setAssignedTo(newValue); break;
                    default: return false;
                }
                saveBugs(bugs);
                return true;
            }
        }
        return false;
    }

    /**
     * Add a comment to a bug by its ID.
     * 
     * @param id the ID of the bug
     * @param comment the comment to add
     * @return true if successful, false if not found
     */
    public boolean addCommentToBug(int id, String comment) {
        List<Bug> bugs = loadBugs();
        for (int i = 0; i < bugs.size(); i++) {
            Bug b = bugs.get(i);
            if (b.getId() == id) {
                b.addComment(comment);
                saveBugs(bugs);
                return true;
            }
        }
        return false;
    }

    /**
     * Convert a string to a Status enum.
     * 
     * @param s the string to convert
     * @return the corresponding Status, or null if invalid
     */
    public Status stringToStatus(String s) {
        try {
            return Status.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert a string to a Priority enum.
     * 
     * @param p the string to convert
     * @return the corresponding Priority, or null if invalid
     */
    public Priority stringToPriority(String p) {
        try {
            return Priority.valueOf(p.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Search bugs with optional keyword and status filters, and then sort by either priority or status.
     * 
     * @param keyword Filter bugs containing keyword in title or description (null or empty = no keyword filter)
     * @param statusFilter Filter by specific status (null = no status filter)
     * @param sortBy "priority" or "status" for sorting. null or anything else = no particular sorting.
     * @return list of filtered and sorted bugs
     */
    public List<Bug> searchBugs(String keyword, Status statusFilter, String sortBy) {
        List<Bug> bugs = loadBugs();
        List<Bug> results = new ArrayList<>();

        boolean filterByKeyword = (keyword != null && !keyword.trim().isEmpty());
        boolean filterByStatus = (statusFilter != null);

        // Filter
        for (Bug bug : bugs) {
            boolean matches = true;
            if (filterByKeyword) {
                String kw = keyword.toLowerCase();
                if (!bug.getTitle().toLowerCase().contains(kw) &&
                    !bug.getDescription().toLowerCase().contains(kw)) {
                    matches = false;
                }
            }
            if (matches && filterByStatus) {
                if (bug.getStatus() != statusFilter) {
                    matches = false;
                }
            }
            if (matches) {
                results.add(bug);
            }
        }

        // Sort
        if ("priority".equalsIgnoreCase(sortBy)) {
            results.sort((b1, b2) -> b1.getPriority().compareTo(b2.getPriority()));
        } else if ("status".equalsIgnoreCase(sortBy)) {
            results.sort((b1, b2) -> b1.getStatus().compareTo(b2.getStatus()));
        }

        return results;
    }
}
