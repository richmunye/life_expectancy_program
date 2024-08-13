package User;

import java.io.Console;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Validation {
    private static final Console console = System.console();

    //    Password validation
    static String getValidatePassword() {
        String password = null;
        boolean passwordsMatch = false;

        while (!passwordsMatch) {
            char[] passwordArray = console.readPassword("Password: ");
            password = new String(passwordArray);
            char[] confirmPasswordArray = console.readPassword("Confirm Password: ");
            String confirmPassword = new String(confirmPasswordArray);

            if (password.equals(confirmPassword)) {
                passwordsMatch = true;
            } else {
                System.out.println("Passwords do not match. Please try again.");
            }
        }

        return password;
    }

    //    Validate Date
    public static String getValidDate(Scanner scanner, String prompt) {
        String dateStr = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean validDate = false;

        while (!validDate) {
            System.out.print(prompt);
            dateStr = scanner.nextLine();

            try {
                // Attempt to parse the date
                LocalDate date = LocalDate.parse(dateStr, formatter);
                validDate = true; // Date is valid
            } catch (DateTimeParseException e) {
                // If parsing fails, the date is not valid
                System.out.println("Invalid date format. Please enter the date in YYYY-MM-DD format.");
            }
        }

        return dateStr;
    }

    // Validate ("true" or "false")
    public static boolean getValidBoolean(Scanner scanner, String prompt) {
        String input = "";
        boolean validInput = false;

        while (!validInput) {
            System.out.print(prompt);
            input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("true")) {
                return true;
            } else if (input.equals("false")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'true' or 'false'.");
            }
        }

        return false;
    }
}
