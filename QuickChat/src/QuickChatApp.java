import java.util.Scanner;

public class QuickChatApp {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // ========== REGISTRATION & LOGIN FEATURE ==========

        // Registration
        System.out.println("=== USER REGISTRATION ===");
        
        System.out.print("Enter your first name: ");
        String firstName = input.nextLine();
        
        System.out.print("Enter your last name: ");
        String lastName = input.nextLine();
        
        // Username validation: must contain number
        String username;
        boolean validUsername;
        do {
            System.out.print("Enter your username (must contain a number): ");
            username = input.nextLine();
            validUsername = isValidUsername(username);
            if (!validUsername) {
                System.out.println("Invalid username. Must contain at least one number. Please try again.");
            }
        } while (!validUsername);
        
        // Password validation: 8+ chars, one capital, one number, one special
        String password;
        boolean validPassword;
        do {
            System.out.print("Enter your password (8+ chars, one capital, one number, one special): ");
            password = input.nextLine();
            validPassword = isValidPassword(password);
            if (!validPassword) {
                System.out.println("Invalid password. Please try again.");
            }
        } while (!validPassword);
        
        // Phone number validation: format +27XXXXXXXXX
        String phoneNumber;
        boolean validPhone;
        do {
            System.out.print("Enter your SA cell phone number (format +27XXXXXXXXX): ");
            phoneNumber = input.nextLine();
            validPhone = phoneNumber.matches("\\+27\\d{9}");
            if (!validPhone) {
                System.out.println("Invalid phone number. Please try again.");
            }
        } while (!validPhone);
        
        System.out.println("\nUser registered successfully!");
        System.out.println("Registration successful. You can now log in.");
        
        // Login
        System.out.println("\n=== LOGIN ===");
        boolean loggedIn = false;
        int attempts = 0;
        
        while (!loggedIn && attempts < 3) {
            System.out.print("Enter your username: ");
            String loginUser = input.nextLine();
            
            System.out.print("Enter your password: ");
            String loginPass = input.nextLine();
            
            if (loginUser.equals(username) && loginPass.equals(password)) {
                loggedIn = true;
                System.out.println("\nWelcome " + firstName + " " + lastName + ", it is great to see you.");
            } else {
                attempts++;
                System.out.println("Invalid credentials. Attempts remaining: " + (3 - attempts));
            }
        }
        
        if (!loggedIn) {
            System.out.println("\nToo many failed attempts. Account locked. Goodbye!");
            input.close();
            return;
        }

        // ========== QUICKCHAT MESSAGING FEATURE ==========

        System.out.println("===Welcome to QuickChat ==");

        // Get message limit
        System.out.print("How many messages do you wish to enter? ");
        int messageLimit = input.nextInt();
        input.nextLine();

        // Arrays to store messages
        String[] contents = new String[messageLimit];
        String[] recipients = new String[messageLimit];
        String[] senders = new String[messageLimit];

        int choice;
        int messageCount = 0;

        // Menu loop
        do {

            System.out.println("QUICKCHAT MAIN MENU");

            System.out.println("1. Send Messages");
            System.out.println("2. Show recently sent messages");
            System.out.println("3. Quit");
            System.out.print("Enter your choice (1-3): ");

            choice = input.nextInt();
            input.nextLine();

            if (choice == 1) {
                if (messageCount < messageLimit) {
                    System.out.println("\n--- Message " + (messageCount + 1) + " ---");
                    System.out.print("Enter message content: ");
                    contents[messageCount] = input.nextLine();
                    System.out.print("Enter recipient: ");
                    recipients[messageCount] = input.nextLine();
                    System.out.print("Enter sender: ");
                    senders[messageCount] = input.nextLine();

                    System.out.println("\nMessage sent successfully.");
                    messageCount++;
                    System.out.println("Messages sent: " + messageCount + "/" + messageLimit);

                    if (messageCount == messageLimit) {
                        System.out.println("You have reached your message limit.");
                    }
                } else {
                    System.out.println("\nMessage limit reached. You have already sent " + messageLimit + " message(s).");
                }
            } else if (choice == 2) {
                if (messageCount == 0) {
                    System.out.println("\nNo messages have been sent yet.");
                } else {
                    System.out.println("\n========== RECENTLY SENT MESSAGES ==========");
                    for (int i = 0; i < messageCount; i++) {
                        System.out.println("\nMessage #" + (i + 1));
                        System.out.println("  From:    " + senders[i]);
                        System.out.println("  To:      " + recipients[i]);
                        System.out.println("  Content: " + contents[i]);
                        System.out.println("--------------------------------------------");
                    }
                }
            } else if (choice == 3) {
                System.out.println("\nThank you for using QuickChat. Goodbye!");
            } else {
                System.out.println("\nInvalid choice. Please enter 1, 2, or 3.");
            }

        } while (choice != 3);

        input.close();
    }
    
    // Helper method to validate username
    static boolean isValidUsername(String username) {
        for (char c : username.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
    
    // Helper method to validate password
    static boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        
        boolean hasUpper = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (!Character.isLetterOrDigit(c)) hasSpecial = true;
        }
        
        return hasUpper && hasDigit && hasSpecial;
    }
}