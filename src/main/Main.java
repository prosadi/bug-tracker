package main;

import java.util.List;
import java.util.Scanner;
import managers.BugManager;
import managers.StatisticsManager;
import managers.UserManager;
import models.Bug;
import models.Role;
import models.Status;


public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final BugManager manager = new BugManager();
    private static final StatisticsManager statsManager = new StatisticsManager();
    private static final UserManager userManager = new UserManager(); // UserManager instance

    public static void main(String[] args) {
        // Load users from file
        userManager.loadUsersFromFile("users.dat");
    
        // Save users to file on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            userManager.saveUsersToFile("users.dat");
        }));
    
        // Login process
        boolean loggedIn = false;
        while (!loggedIn) {
            loggedIn = loginMenu();
        }
    
        Role currentRole = userManager.getCurrentUserRole(); // Get the logged-in user's role
    
        // Main program menu
        boolean running = true;
        while (running) {
            printMainMenu(currentRole); // Show menu based on role
            int choice;
    
            if (currentRole == Role.PROJECT_MANAGER) {
                choice = getUserChoice(1, 2); // Limited options for Project Managers
            } else if (currentRole == Role.ADMIN) {
                choice = getUserChoice(1, 5); // Admins have all options
            } else {
                choice = getUserChoice(1, 4); // Full menu for other roles
            }
    
            switch (choice) {
                case 1:
                    if (currentRole == Role.PROJECT_MANAGER) {
                        System.out.println("Access Denied. Project Managers cannot access Bug Operations.");
                    } else {
                        bugOperationMenu(); // Admins and other roles access Bug Operations
                    }
                    break;
    
                case 2:
                    if (currentRole == Role.PROJECT_MANAGER) {
                        System.out.println("Access Denied. Project Managers cannot access Search Operations.");
                    } else {
                        searchOperationMenu(); // Admins and other roles access Search Operations
                    }
                    break;
    
                case 3:
                    statisticsMenu(); // All roles, including Project Managers, can access Statistics
                    break;
    
                case 4:
                    if (currentRole == Role.ADMIN) {
                        // userManagementMenu(); // Admin-only User Management
                    } else {
                        running = false; // Exit for non-admin roles
                        System.out.println("Exiting the application. Goodbye!");
                    }
                    break;
    
                case 5:
                    if (currentRole == Role.ADMIN) {
                        running = false; // Exit for Admin
                        System.out.println("Exiting the application. Goodbye!");
                    } else {
                        System.out.println("Invalid option.");
                    }
                    break;
    
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
    
    

    // ========================= LOGIN MENU =========================

    private static boolean loginMenu() {
        System.out.println("\n==================================================");
        System.out.println("                  LOGIN MENU                      ");
        System.out.println("==================================================");
        System.out.println("[1] Sign Up");
        System.out.println("[2] Login");
        System.out.println("[3] Exit");
        System.out.println("--------------------------------------------------");
        System.out.print("Please select an option (1-3): ");
        int choice = getUserChoice(1, 3);

        switch (choice) {
            case 1:
                return signUp();
            case 2:
                return login();
            case 3:
                System.out.println("Exiting application. Goodbye!");
                System.exit(0);
        }
        return false;
    }
    private static Role selectRole() {
        System.out.println("\nSelect Role:");
        System.out.println("[1] Developer");
        System.out.println("[2] Tester");
        System.out.println("[3] Project Manager");
        System.out.println("[4] Admin");
    
        int choice;
        while (true) {
            System.out.print("Enter your choice (1-4): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                switch (choice) {
                    case 1:
                        return Role.DEVELOPER;
                    case 2:
                        return Role.TESTER;
                    case 3:
                        return Role.PROJECT_MANAGER;
                    case 4:
                        return Role.ADMIN;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    private static void changeBugStatus() {
        System.out.println("\n[Main Menu > Bug Operations > Change Bug Status]");
        System.out.println("Enter Bug ID to change status, or 0 to cancel.");
        int id = promptForBugId("Bug ID: ");
        if (id == 0) {
            canceled();
            return;
        }
    
        Bug foundBug = manager.findBugById(id);
        if (foundBug == null) {
            System.out.println("No bug found with ID " + id + ".");
            return;
        }
    
        System.out.println("Current Status: " + foundBug.getStatus());
        String newStatusStr = promptUser("Enter the new status (NEW, IN_PROGRESS, RESOLVED, CLOSED): ").toUpperCase();
        if (newStatusStr.isEmpty()) {
            canceled();
            return;
        }
    
        Status newStatus;
        try {
            newStatus = Status.valueOf(newStatusStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status. Operation canceled.");
            return;
        }
    
        boolean success = manager.updateBugStatus(id, newStatus);
        if (success) {
            System.out.println("Bug status updated successfully!");
        } else {
            System.out.println("Failed to update bug status.");
        }
    }
    

    private static boolean signUp() {
        System.out.println("\n[Sign Up]");
        String username = promptUser("Enter a username: ");
        String password = promptUser("Enter a password: ");
        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password cannot be empty. Try again.");
            return false;
        }

        if (userManager.signUp(username, password, selectRole())) {
            System.out.println("Sign-up successful! Please log in.");
            return false;
        } else {
            System.out.println("Username already exists. Try again.");
            return false;
        }
    }

    private static boolean login() {
        System.out.println("\n[Login]");
        String username = promptUser("Enter your username: ");
        String password = promptUser("Enter your password: ");

        if (userManager.login(username, password)) {
            System.out.println("Login successful! Welcome, " + username + ".");
            return true;
        } else {
            System.out.println("Invalid username or password. Try again.");
            return false;
        }
    }

    // ========================= MAIN MENU =========================

    private static void printMainMenu(Role role) {
        System.out.println("\n==================================================");
        System.out.println("                    MAIN MENU                     ");
        System.out.println("==================================================");
    
        if (role == Role.PROJECT_MANAGER) {
            // Limited options for Project Managers
            System.out.println("[1] Statistics & Reporting");
            System.out.println("[2] Exit");
        } else if (role == Role.ADMIN) {
            // Full options for Admins, including User Management
            System.out.println("[1] Bug Operations");
            System.out.println("[2] Search Operations");
            System.out.println("[3] Statistics & Reporting");
            System.out.println("[4] User Management");
            System.out.println("[5] Exit");
        } else {
            // Full menu for other roles
            System.out.println("[1] Bug Operations");
            System.out.println("[2] Search Operations");
            System.out.println("[3] Statistics & Reporting");
            System.out.println("[4] Exit");
        }
    
        System.out.println("--------------------------------------------------");
        if (role == Role.PROJECT_MANAGER) {
            System.out.print("Please select an option (1-2): ");
        } else if (role == Role.ADMIN) {
            System.out.print("Please select an option (1-5): ");
        } else {
            System.out.print("Please select an option (1-4): ");
        }
    }
    
    
    

    // ========================= BUG OPERATION MENU =========================

    private static void bugOperationMenu() {
        Role currentRole = userManager.getCurrentUserRole();
    
        if (currentRole == Role.PROJECT_MANAGER) {
            System.out.println("Access Denied. Project Managers cannot access Bug Operations.");
            return;
        }
    
        boolean running = true;
        while (running) {
            printBugOperationMenu(currentRole);
            int choice = getUserChoice(1, 7); // Admins and other roles can access all options
    
            switch (choice) {
                case 1:
                    listAllBugs();
                    pause();
                    break;
    
                case 2:
                    if (currentRole == Role.TESTER || currentRole == Role.ADMIN) {
                        addNewBug();
                        pause();
                    } else {
                        System.out.println("Access Denied.");
                    }
                    break;
    
                case 3:
                    if (currentRole == Role.DEVELOPER || currentRole == Role.ADMIN) {
                        changeBugStatus();
                        pause();
                    } else {
                        System.out.println("Access Denied.");
                    }
                    break;
    
                case 4:
                    if (currentRole != Role.TESTER) {
                        updateBugDetailsById();
                        pause();
                    } else {
                        System.out.println("Access Denied.");
                    }
                    break;
    
                case 5:
                    if (currentRole != Role.TESTER) {
                        deleteBugById();
                        pause();
                    } else {
                        System.out.println("Access Denied.");
                    }
                    break;
    
                case 6:
                    if (currentRole != Role.TESTER) {
                        addCommentToBug();
                        pause();
                    } else {
                        System.out.println("Access Denied.");
                    }
                    break;
    
                case 7:
                    running = false; // Go back to Main Menu
                    break;
    
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
    

    private static void printBugOperationMenu(Role role) {
        System.out.println("\n[Main Menu > Bug Operations]");
        System.out.println("=============== BUG OPERATIONS ================");
    
        System.out.println("[1] View All Bugs");
    
        if (role == Role.TESTER) {
            System.out.println("[2] Add Bug");
        } else if (role == Role.DEVELOPER) {
            System.out.println("[2] Search Bugs");
            System.out.println("[3] Change Bug Status");
        } else {
            // Non-Testers and Non-Developers (e.g., Admins, Project Managers)
            System.out.println("[2] Add Bug");
            System.out.println("[3] Search Bugs");
            System.out.println("[4] Edit Bug");
            System.out.println("[5] Delete Bug");
            System.out.println("[6] Add Comment to Bug");
        }
    
        System.out.println("[7] Go Back to Main Menu");
        System.out.println("-----------------------------------------------");
    
        if (role == Role.TESTER) {
            System.out.print("Please select an option (1-2): ");
        } else if (role == Role.DEVELOPER) {
            System.out.print("Please select an option (1-3): ");
        } else {
            System.out.print("Please select an option (1-7): ");
        }
    }
    
    // CRUD and comment methods

    private static void addNewBug() {
        System.out.println("\n[Main Menu > Bug Operations > Add Bug]");
        System.out.println("----- Add a New Bug -----");
        System.out.println("Leave any field empty and press ENTER to cancel.");

        List<Bug> existingBugs = manager.loadBugs();
        int maxId = existingBugs.stream().mapToInt(Bug::getId).max().orElse(0);
        int newId = maxId + 1;

        System.out.println("Assigned Bug ID: " + newId);

        String title = promptUser("Enter bug title: ");
        if (title.isEmpty()) { canceled(); return; }
        String description = promptUser("Enter bug description: ");
        if (description.isEmpty()) { canceled(); return; }
        String status = promptUser("Enter bug status (e.g., NEW, IN_PROGRESS, RESOLVED, CLOSED): ");
        if (status.isEmpty()) { canceled(); return; }
        String priority = promptUser("Enter bug priority (e.g., LOW, MEDIUM, HIGH, CRITICAL): ");
        if (priority.isEmpty()) { canceled(); return; }
        String assignedTo = promptUser("Enter the name of the person assigned to this bug: ");
        if (assignedTo.isEmpty()) { canceled(); return; }

        // Validation and enum conversion happens in BugManager
        Bug newBug = new Bug(newId, title, description, manager.stringToStatus(status), manager.stringToPriority(priority), assignedTo);
        manager.addBug(newBug);

        System.out.println("New bug added successfully!");
    }

    private static void viewBugDetailsById() {
        System.out.println("\n[Main Menu > Bug Operations > View Bug]");
        System.out.println("Enter Bug ID to view details, or 0 to cancel.");
        int id = promptForBugId("Bug ID: ");
        if (id == 0) { canceled(); return; }
        if (id == -1) return;

        Bug foundBug = manager.findBugById(id);
        if (foundBug == null) {
            System.out.println("No bug found with ID " + id + ".");
            return;
        }

        displayBugDetails(foundBug);
    }

    private static void updateBugDetailsById() {
        System.out.println("\n[Main Menu > Bug Operations > Edit Bug]");
        System.out.println("Enter Bug ID to edit, or 0 to cancel.");
        int id = promptForBugId("Bug ID: ");
        if (id == 0) { canceled(); return; }
        if (id == -1) return;

        Bug foundBug = manager.findBugById(id);
        if (foundBug == null) {
            System.out.println("No bug found with ID " + id + ".");
            return;
        }

        System.out.println("\nCurrent Bug Details:");
        System.out.println("1. Title: " + foundBug.getTitle());
        System.out.println("2. Description: " + foundBug.getDescription());
        System.out.println("3. Status: " + foundBug.getStatus().name());
        System.out.println("4. Priority: " + foundBug.getPriority().name());
        System.out.println("5. Assigned To: " + foundBug.getAssignedTo());
        System.out.println("Enter 0 to cancel.");

        int fieldChoice = promptForInt("Enter the number of the field you want to update (1-5): ", 0, 5);
        if (fieldChoice == 0) { canceled(); return; }
        if (fieldChoice == -1) {
            System.out.println("Invalid choice. Operation canceled.");
            return;
        }

        String newValue = promptUser("Enter the new value (or empty to cancel): ");
        if (newValue.isEmpty()) { canceled(); return; }

        boolean success = manager.updateBugField(id, fieldChoice, newValue);
        if (success) {
            System.out.println("Bug details updated successfully!");
        } else {
            System.out.println("Failed to update the bug.");
        }
    }

    private static void deleteBugById() {
        System.out.println("\n[Main Menu > Bug Operations > Delete Bug]");
        System.out.println("Enter Bug ID to delete, or 0 to cancel.");
        int id = promptForBugId("Bug ID: ");
        if (id == 0) { canceled(); return; }
        if (id == -1) return;

        boolean success = manager.deleteBug(id);
        if (success) {
            System.out.println("Bug ID " + id + " has been deleted successfully!");
        } else {
            System.out.println("No bug found with ID " + id + ".");
        }
    }

    private static void listAllBugs() {
        System.out.println("\n[Main Menu > Bug Operations > List All Bugs]");
        System.out.println("=============== BUG LIST ===============");
    
        List<Bug> bugs = manager.loadBugs();
    
        if (bugs.isEmpty()) {
            System.out.println("No bugs available to display.");
            return;
        }
    
        System.out.printf("%-10s %-30s %-15s%n", "BUG ID", "TITLE", "PRIORITY");
        System.out.println("---------------------------------------------");
    
        for (Bug bug : bugs) {
            System.out.printf("%-10d %-30s %-15s%n", bug.getId(), bug.getTitle(), bug.getPriority().name());
        }
    
        System.out.println("---------------------------------------------");
    }
    

    private static void addCommentToBug() {
        System.out.println("\n[Main Menu > Bug Operations > Add Comment]");
        System.out.println("Enter Bug ID to add a comment, or 0 to cancel.");
        int id = promptForBugId("Bug ID: ");
        if (id == 0) { canceled(); return; }
        if (id == -1) return;

        Bug foundBug = manager.findBugById(id);
        if (foundBug == null) {
            System.out.println("No bug found with ID " + id + ".");
            return;
        }

        String comment = promptUser("Enter your comment (or empty to cancel): ");
        if (comment.isEmpty()) { canceled(); return; }

        boolean success = manager.addCommentToBug(id, comment);
        if (success) {
            System.out.println("Comment added successfully to Bug ID " + id + "!");
        } else {
            System.out.println("Failed to add comment. Bug not found?");
        }
    }

    // ========================= SEARCH OPERATION MENU =========================

    private static void searchOperationMenu() {
        Role currentRole = userManager.getCurrentUserRole();
    
        if (currentRole == Role.PROJECT_MANAGER) {
            System.out.println("Access Denied. Project Managers cannot access Search Operations.");
            return;
        }
    
        boolean running = true;
        while (running) {
            printSearchOperationMenu();
            int choice = getUserChoice(1, 6);
    
            switch (choice) {
                case 1:
                    searchBugsByKeyword();
                    pause();
                    break;
    
                case 2:
                    searchBugsByAssigneeName();
                    pause();
                    break;
    
                case 3:
                    searchBugsByStatus();
                    pause();
                    break;
    
                case 4:
                    searchBugsByPriority();
                    pause();
                    break;
    
                case 5:
                    filteredSearchWithSorting();
                    pause();
                    break;
    
                case 6:
                    running = false; // Go Back
                    break;
    
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
    

    private static void printSearchOperationMenu() {
        System.out.println("\n[Main Menu > Search Operations]");
        System.out.println("============= SEARCH OPERATIONS =============");
        System.out.println("[1] Search By Keyword");
        System.out.println("[2] Search by Assignee Name");
        System.out.println("[3] Search Bugs by Status");
        System.out.println("[4] Search Bugs by Priority");
        System.out.println("[5] Filtered Search (Keyword & Status) with Sorting");
        System.out.println("[6] Go Back to Main Menu");
        System.out.println("---------------------------------------------");
        System.out.print("Please select an option (1-6): ");
    }

    private static void searchBugsByKeyword() {
        System.out.println("\n[Main Menu > Search Operations > Search by Keyword]");
        System.out.println("Enter a keyword (or empty to cancel).");
        String keyword = promptUser("Keyword: ").toLowerCase();
        if (keyword.isEmpty()) { canceled(); return; }

        List<Bug> bugs = manager.loadBugs();
        boolean found = false;
        for (Bug bug : bugs) {
            if (bug.getTitle().toLowerCase().contains(keyword) || bug.getDescription().toLowerCase().contains(keyword)) {
                displayBugSummary(bug);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No bugs found containing \"" + keyword + "\".");
        }
    }

    private static void searchBugsByAssigneeName() {
        System.out.println("\n[Main Menu > Search Operations > Search by Assignee Name]");
        System.out.println("Enter the assignee's name (or empty to cancel).");
        String assignee = promptUser("Assignee: ").toLowerCase();
        if (assignee.isEmpty()) { canceled(); return; }

        List<Bug> bugs = manager.loadBugs();
        boolean found = false;
        for (Bug bug : bugs) {
            if (bug.getAssignedTo().toLowerCase().contains(assignee)) {
                displayBugSummary(bug);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No bugs found assigned to \"" + assignee + "\".");
        }
    }

    private static void searchBugsByStatus() {
        System.out.println("\n[Main Menu > Search Operations > Search by Status]");
        System.out.println("Enter a status (e.g., NEW, IN_PROGRESS), or empty to cancel.");
        String statusStr = promptUser("Status: ").trim().toUpperCase();
        if (statusStr.isEmpty()) { canceled(); return; }

        Status statusFilter = null;
        try {
            statusFilter = Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status. Operation canceled.");
            return;
        }

        List<Bug> bugs = manager.loadBugs();
        boolean found = false;
        for (Bug bug : bugs) {
            if (bug.getStatus() == statusFilter) {
                displayBugSummary(bug);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No bugs found with status \"" + statusStr + "\".");
        }
    }

    private static void searchBugsByPriority() {
        System.out.println("\n[Main Menu > Search Operations > Search by Priority]");
        System.out.println("Enter a priority (LOW/MEDIUM/HIGH/CRITICAL), or empty to cancel.");
        String priorityStr = promptUser("Priority: ").toUpperCase();
        if (priorityStr.isEmpty()) { canceled(); return; }

        List<Bug> bugs = manager.loadBugs();
        boolean found = false;
        for (Bug bug : bugs) {
            if (bug.getPriority().name().equalsIgnoreCase(priorityStr)) {
                displayBugSummary(bug);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No bugs found with priority \"" + priorityStr + "\".");
        }
    }

    private static void filteredSearchWithSorting() {
        System.out.println("\n[Main Menu > Search Operations > Filtered Search]");
        String keyword = promptUser("Enter a keyword (or empty to skip): ").toLowerCase();
        String statusStr = promptUser("Enter a status (NEW, IN_PROGRESS, RESOLVED, CLOSED) or empty to skip: ").toUpperCase().trim();
        Status statusFilter = null;
        if (!statusStr.isEmpty()) {
            try {
                statusFilter = Status.valueOf(statusStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid status. Skipping status filter.");
            }
        }

        String sortChoice = promptUser("Sort by 'priority', 'status', or leave empty for no sorting: ").toLowerCase().trim();
        if (!sortChoice.equals("priority") && !sortChoice.equals("status")) {
            sortChoice = null;
        }

        List<Bug> results = manager.searchBugs(keyword, statusFilter, sortChoice);

        if (results.isEmpty()) {
            System.out.println("No bugs found with the given filters.");
        } else {
            System.out.println("Filtered and Sorted Results:");
            for (Bug bug : results) {
                displayBugSummary(bug);
            }
        }
    }

    // ========================= STATISTICS & REPORTING MENU =========================

    private static void statisticsMenu() {
        boolean running = true;
        while (running) {
            printStatisticsMenu();
            int choice = getUserChoice(1, 5);
            switch (choice) {
                case 1:
                    showBugsCountByStatus();
                    pause();
                    break;
                case 2:
                    showBugsCountByPriority();
                    pause();
                    break;
                case 3:
                    showBugsCountByAssignee();
                    pause();
                    break;
                case 4:
                    showSummaryReport();
                    pause();
                    break;
                case 5:
                    running = false; // Go Back to main menu
                    break;
            }
        }
    }

    private static void printStatisticsMenu() {
        System.out.println("\n[Main Menu > Statistics & Reporting]");
        System.out.println("=========== STATISTICS & REPORTING ===========");
        System.out.println("[1] Show Counts of Bugs by Status");
        System.out.println("[2] Show Counts of Bugs by Priority");
        System.out.println("[3] Show Counts of Bugs by Assignee");
        System.out.println("[4] Show Summary Report");
        System.out.println("[5] Go Back to Main Menu");
        System.out.println("---------------------------------------------");
        System.out.print("Please select an option (1-5): ");
    }

    private static void showBugsCountByStatus() {
        List<Bug> bugs = manager.loadBugs();
        statsManager.showBugsCountByStatus(bugs);
    }

    private static void showBugsCountByPriority() {
        List<Bug> bugs = manager.loadBugs();
        statsManager.showBugsCountByPriority(bugs);
    }

    private static void showBugsCountByAssignee() {
        List<Bug> bugs = manager.loadBugs();
        statsManager.showBugsCountByAssignee(bugs);
    }

    private static void showSummaryReport() {
        List<Bug> bugs = manager.loadBugs();
        statsManager.showSummaryReport(bugs);
    }

    // ========================= HELPER METHODS =========================

    private static int getUserChoice(int min, int max) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Please select a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number (" + min + "-" + max + "): ");
            }
        }
    }

    private static void pause() {
        System.out.println("\nPress ENTER to continue...");
        scanner.nextLine();
    }

    private static String promptUser(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static int promptForBugId(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return 0; // cancel option
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID. Please enter a numeric value or 0 to cancel.");
            return -1;
        }
    }

    private static int promptForInt(String message, int min, int max) {
        System.out.print(message);
        String input = scanner.nextLine().trim();
        if (input.equals("0")) return 0; // allow cancellation
        int value;
        try {
            value = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (value < min || value > max) return -1;
        return value;
    }

    private static void canceled() {
        System.out.println("Operation canceled.");
    }

    private static void displayBugDetails(Bug bug) {
        System.out.println("--------------------------------------------------");
        System.out.println("                   BUG DETAILS                    ");
        System.out.println("--------------------------------------------------");
        System.out.printf("ID:          %d%n", bug.getId());
        System.out.printf("Title:       %s%n", bug.getTitle());
        System.out.printf("Description: %s%n", bug.getDescription());
        System.out.printf("Status:      %s%n", bug.getStatus().name());
        System.out.printf("Priority:    %s%n", bug.getPriority().name());
        System.out.printf("Assigned To: %s%n", bug.getAssignedTo());
        System.out.println("--------------------------------------------------");
        System.out.println("Comments:");

        String comments = bug.getCommentsAsString();
        if (comments.equals("No comments.")) {
            System.out.println("  No comments yet for this bug.");
        } else {
            for (String line : comments.split("\n")) {
                System.out.println("  " + line);
            }
        }
        System.out.println("--------------------------------------------------");
    }

    private static void displayBugSummary(Bug bug) {
        System.out.println("--------------------------------------------------");
        System.out.printf("ID:          %d%n", bug.getId());
        System.out.printf("Title:       %s%n", bug.getTitle());
        System.out.printf("Description: %s%n", bug.getDescription());
        System.out.printf("Priority:    %s%n", bug.getPriority().name());
        System.out.printf("Assigned To: %s%n", bug.getAssignedTo());
        System.out.println("Status:      " + bug.getStatus().name());
        System.out.println("--------------------------------------------------");
    }
}
