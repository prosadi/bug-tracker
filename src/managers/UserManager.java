package managers;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import models.Role;
import models.User;

public class UserManager {
    private List<User> users;
    private String currentUser; // To track the currently logged-in user

    public UserManager() {
        this.users = new ArrayList<>();
        this.currentUser = null;
    }

    // Sign up a new user
    public boolean signUp(String username, String password, Role role) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return false; // Username already exists
            }
        }
        users.add(new User(username, password, role)); // Add user with role
        return true;
    }

    // Log in a user
    public boolean login(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username) && user.getPassword().equals(password)) {
                currentUser = username; // Store the logged-in username
                return true; // Login successful
            }
        }
        return false; // Invalid credentials
    }

    // Log out the current user
    public void logout() {
        currentUser = null;
    }

    // Get the role of the currently logged-in user
    public Role getCurrentUserRole() {
        if (currentUser == null) {
            return null; // No user is logged in
        }
        return getUserRole(currentUser); // Fetch role of the current user
    }

    // Get the role of a user by username
    public Role getUserRole(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user.getRole(); // Return the user's role
            }
        }
        return null; // User not found
    }

    // Save users to a file
    public void saveUsersToFile(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    // Load users from a file
    @SuppressWarnings("unchecked")
    public void loadUsersFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            users = (List<User>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No user data file found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    // Get all users (for debugging or admin purposes)
    public List<User> getUsers() {
        return users;
    }

    // Get the currently logged-in user
    public String getCurrentUser() {
        return currentUser;
    }

    public User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }
    
    public boolean removeUser(String username) {
        return users.removeIf(user -> user.getUsername().equalsIgnoreCase(username));
    }
    
}
