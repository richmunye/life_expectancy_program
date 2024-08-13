package User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.Console;

public class Patient extends User {
    private final Console console = System.console();

    public Patient(String uuid, String email) {
        super(uuid, email);
    }

    @Override
    public void menu(Scanner scanner) {
        if (firstName == null || firstName.isEmpty()) {
            System.out.println("\nWelcome! Please complete your profile.");
            completeRegistration(scanner);
        } else {
            System.out.println("\nProfile is complete. Enter your password to continue.");
            authenticateAndProceed(scanner);
        }
    }


    private void completeRegistration(Scanner scanner) {
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();

        // Validate Password
        String password = Validation.getValidatePassword();

        // Get and validate the date of birth
        String dateOfBirth = Validation.getValidDate(scanner, "Date of Birth (YYYY-MM-DD): ");

        // HIV status
        boolean hivStatus = Validation.getValidBoolean(scanner, "HIV Status (true/false): ");
        String diagnosisDate = "";
        if (hivStatus) {
            diagnosisDate = Validation.getValidDate(scanner, "Diagnosis (YYYY-MM-DD): ");
        }
        // ART Status
        boolean artStatus = Validation.getValidBoolean(scanner, "ART Status (true/false): ");
        String artStartDate = "";
        if (artStatus) {
            artStartDate = Validation.getValidDate(scanner, "Art Start (YYYY-MM-DD): ");
        }
        System.out.print("Country ISO Code: ");
        String countryISOCode = scanner.nextLine();

        try {
            String result = ScriptExecutor.executeScript("script/user_management.sh", "update_profile",
                    this.uuid, this.email.trim(), firstName, lastName, password, dateOfBirth,
                    String.valueOf(hivStatus), diagnosisDate, String.valueOf(artStatus),
                    artStartDate, countryISOCode);
            System.out.println(result);
            if (result != null && !result.isEmpty()) {
                int patientRemainingLife = LifeSpanCalculator.calculatePatientLifeExpectancy("peace@gmail.com");
                System.out.println("Patient's Remaining Life: " + patientRemainingLife + " Years");
            }
            Logger.log("Updated profile for patient: " + this.email);
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
            Logger.log("Error updating profile for user: " + this.email + " - " + e.getMessage());
        }
    }

    private void authenticateAndProceed(Scanner scanner) {
        char[] passwordArray = console.readPassword("Enter your password: ");
        String password = new String(passwordArray);


        try {
            String result = ScriptExecutor.executeScript("script/user_management.sh", "validate_password",
                    this.email,
                    password);
            System.out.println(result + " this is the result");
            if ("valid".equals(result)) {
                System.out.println("Password validated. Proceeding...");
                patientFunctionality(scanner, this.email);
            } else {
                System.out.println("Invalid password.");
            }
            Logger.log("User authentication attempted for: " + this.email);
        } catch (Exception e) {
            System.out.println("Error during authentication: " + e.getMessage());
            Logger.log("Error during authentication for user: " + this.email + " - " + e.getMessage());
        }
    }

    private void patientFunctionality(Scanner scanner, String email) {
        while (true) {
            System.out.println("\nPatient Menu");
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    patientProfile(email);
                    break;
                case 2:
                    //System.out.println("Press enter if you do not want to update the field!!");
                    updatePatientProfile(scanner, email);
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static Map<String, String> readUser(String email) {
        Map<String, String> userData = new HashMap<>();
        try {
            String result = ScriptExecutor.executeScript("script/update_user.sh", "read", email);
            System.out.println(result + "these are the data we are building");
            if (result != null && !result.equals("User not found")) {
                String[] fields = result.split(",");
                for (String field : fields) {
                    String[] keyValue = field.split(":");
                    if (keyValue.length == 2) {
                        userData.put(keyValue[0], keyValue[1]);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error executing read script: " + e.getMessage());
        }
        System.out.println(userData);
        return userData;
    }

    private static void showUserDetails(Map<String, String> userData) {
        System.out.println("\nUser Details to Update:");
        System.out.println("first_name: " + userData.get("first_name"));
        System.out.println("last_name: " + userData.get("last_name"));
        System.out.println("dob: " + userData.get("dob"));
        System.out.println("hiv_status: " + userData.get("hiv_status"));
        System.out.println("diagnosis_date: " + userData.get("diagnosis_date"));
        System.out.println("art_status: " + userData.get("art_status"));
        System.out.println("art_start_date: " + userData.get("art_start_date"));
        System.out.println("country_iso: " + userData.get("country_iso"));
    }

    private static void updatePatientProfile(Scanner scanner, String email) {
        Map<String, String> userData = readUser(email);
        if (userData != null && !userData.isEmpty()) {
            showUserDetails(userData);

            System.out.print("\nEnter the field you want to update: between (first_name, last_name, dob, hiv_status, diagnosis_date, art_status, art_start_date, country_iso) ");
            String fieldToUpdate = scanner.nextLine();
            System.out.println(fieldToUpdate + userData);
            if (userData.containsKey(fieldToUpdate)) {
                System.out.print("Enter new value for " + fieldToUpdate + ": ");
                String newValue = scanner.nextLine();

                try {
                    if (updateUser(email, fieldToUpdate, newValue)) {
                        System.out.println("User data updated successfully.");
                    } else {
                        System.out.println("Failed to update user data.");
                    }
                } catch (Exception e) {
                    System.out.println("Error executing update script: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid field name.");
            }
        } else {
            System.out.println("User not found.");
        }

        //scanner.close();
    }

    private static boolean updateUser(String email, String field, String newValue) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("script/update_user.sh", "update", email, field, newValue);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    private void patientProfile(String email) {
        try {
            String result = ScriptExecutor.executeScript("script/user_management.sh", "view_profile", email);
            System.out.println("\nPatient Profile");
            String[] parts = result.split(",");
            System.out.println("\nFirstName: " + parts[2]);
            System.out.println("\nlastName: " + parts[3]);
            System.out.println("\nemail: " + parts[0]);
            System.out.println("\ndate of birth: " + parts[5]);
            System.out.println("\ndiagnosis Status: " + parts[6]);
            System.out.println("\ndiagnosis Date: " + parts[7]);
            System.out.println("\nART Status: " + parts[8]);
            System.out.println("\nART Date: " + parts[9]);
            System.out.println("\nCountry ISO: " + parts[10]);
            Logger.log("Viewed profile for user: " + email);
        } catch (Exception e) {
            System.out.println("Error during getting patient Profile: " + e.getMessage());
            Logger.log("Error viewing profile for user: " + email + " - " + e.getMessage());
        }
    }
}
