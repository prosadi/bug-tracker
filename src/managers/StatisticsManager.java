package managers;

import models.Bug;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsManager {

    // Show counts of bugs by status
    public void showBugsCountByStatus(List<Bug> bugs) {
        Map<String, Integer> statusCounts = new HashMap<>();
        for (Bug bug : bugs) {
            // Convert enum to string using name(), then toUpperCase()
            String status = bug.getStatus().name().toUpperCase();
            statusCounts.put(status, statusCounts.getOrDefault(status, 0) + 1);
        }

        System.out.println("\n-- Bugs Count by Status --");
        if (statusCounts.isEmpty()) {
            System.out.println("No bugs found.");
        } else {
            for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // Show counts of bugs by priority
    public void showBugsCountByPriority(List<Bug> bugs) {
        Map<String, Integer> priorityCounts = new HashMap<>();
        for (Bug bug : bugs) {
            // Convert enum to string using name(), then toUpperCase()
            String priority = bug.getPriority().name().toUpperCase();
            priorityCounts.put(priority, priorityCounts.getOrDefault(priority, 0) + 1);
        }

        System.out.println("\n-- Bugs Count by Priority --");
        if (priorityCounts.isEmpty()) {
            System.out.println("No bugs found.");
        } else {
            for (Map.Entry<String, Integer> entry : priorityCounts.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // Show counts of bugs by assignee
    public void showBugsCountByAssignee(List<Bug> bugs) {
        Map<String, Integer> assigneeCounts = new HashMap<>();
        for (Bug bug : bugs) {
            String assignee = bug.getAssignedTo().toLowerCase();
            assigneeCounts.put(assignee, assigneeCounts.getOrDefault(assignee, 0) + 1);
        }

        System.out.println("\n-- Bugs Count by Assignee --");
        if (assigneeCounts.isEmpty()) {
            System.out.println("No bugs found.");
        } else {
            for (Map.Entry<String, Integer> entry : assigneeCounts.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // Show a summary report of all bugs
    public void showSummaryReport(List<Bug> bugs) {
        System.out.println("\n-- Summary Report --");
        int totalBugs = bugs.size();
        System.out.println("Total bugs: " + totalBugs);

        // Count how many are "open" (not RESOLVED or CLOSED)
        int openCount = 0;
        for (Bug bug : bugs) {
            String status = bug.getStatus().name().toUpperCase();
            if (!status.equals("RESOLVED") && !status.equals("CLOSED")) {
                openCount++;
            }
        }

        System.out.println("Open (not resolved or closed): " + openCount);

        if (totalBugs == 0) {
            System.out.println("No bugs in the system.");
        } else {
            System.out.println("Bugs are present, consider reviewing statuses and priorities for a healthier project.");
        }
    }
}
