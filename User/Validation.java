package User;

import java.io.Console;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Views.ColorText;

public class Validation {
    private static final Console console = System.console();

    static String getValidatePassword() {
        ColorText color = new ColorText();
        String password = null;
        boolean passwordsMatch = false;

        while (!passwordsMatch) {
            char[] passwordArray = console.readPassword("|" + color.blue(" Password: "));
            password = new String(passwordArray);
            char[] confirmPasswordArray = console.readPassword("|" + color.blue(" Confirm Password: "));
            String confirmPassword = new String(confirmPasswordArray);

            if (password.equals(confirmPassword)) {
                passwordsMatch = true;
            } else {
                System.out.println("|" + color.red(" Passwords do not match. Please try again."));
            }
        }

        return password;
    }

    public static String getValidDate(Scanner scanner, String prompt) {
        ColorText color = new ColorText();
        String dateStr = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        boolean validDate = false;

        while (!validDate) {
            System.out.print(prompt);
            dateStr = scanner.nextLine();

            try {
                LocalDate date = LocalDate.parse(dateStr, formatter);
                validDate = true;
            } catch (DateTimeParseException e) {
                System.out.println("|" + color.red("Invalid date format. Please enter the date in YYYY-MM-DD format."));
            }
        }

        return dateStr;
    }

    public static boolean getValidBoolean(Scanner scanner, String prompt) {
        ColorText color = new ColorText();
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
                System.out.println("|" + color.red("Invalid input. Please enter 'true' or 'false'."));
            }
        }

        return false;
    }
}
