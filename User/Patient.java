package User;

import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import Views.ColorText;

public class Patient extends User {
    private final Console console = System.console();

    public Patient(String uuid, String email) {
        super(uuid, email);
    }

    ColorText color = new ColorText();

    @Override
    public void menu(Scanner scanner) {
        if (firstName == null || firstName.isEmpty()) {
            System.out.println(" ====================================================");
            System.out.println("|       " + color.purple("Welcome! Please complete your profile.") + "       |");
            System.out.println(" ====================================================");
            completeRegistration(scanner);
        } else {
            System.out.println(color.yellow("\nProfile is complete. Enter your password to continue."));
            authenticateAndProceed(scanner);
        }
    }

    private void completeRegistration(Scanner scanner) {
        System.out.println(" =================================================================");
        System.out.print("|" + color.blue(" First Name: "));
        String firstName = scanner.nextLine();
        System.out.print("|" + color.blue(" Last Name: "));
        String lastName = scanner.nextLine();

        // Validate Password
        String password = Validation.getValidatePassword();

        // Get and validate the date of birth
        String dateOfBirth = Validation.getValidDate(scanner, "|" + color.blue(" Date of Birth (YYYY-MM-DD): "));
        // HIV status
        boolean hivStatus = Validation.getValidBoolean(scanner, "|" + color.blue(" HIV Status (true/false): "));

        String diagnosisDate = "";
        if (hivStatus) {
            diagnosisDate = Validation.getValidDate(scanner, "|" + color.blue(" Diagnosis (YYYY-MM-DD): "));
        }
        // ART Status
        boolean artStatus = Validation.getValidBoolean(scanner, "|" + color.blue(" ART Status (true/false): "));
        String artStartDate = "";
        if (artStatus) {
            artStartDate = Validation.getValidDate(scanner, color.blue("|" + color.blue(" Art Start (YYYY-MM-DD): ")));
        }
        String countryISOCode = Validation.getValidCountryISOCode(scanner, color.blue("|" + "Country ISO Code: "));
//        System.out.print("|" + color.blue(" Country ISO Code: "));
//        String countryISOCode = scanner.nextLine();

        System.out.println(" ================================================================");    

        try {
            String result = ScriptExecutor.executeScript("script/user_management.sh", "update_profile",
                    this.uuid, this.email.trim(), firstName, lastName, password, dateOfBirth,
                    String.valueOf(hivStatus), diagnosisDate, String.valueOf(artStatus),
                    artStartDate, countryISOCode);
            System.out.println(result);
            if (result != null && !result.isEmpty()) {
                int patientRemainingLife = LifeSpanCalculator.calculatePatientLifeExpectancy(this.email.trim());
                try {
                    ScriptExecutor.executeScript("script/analyze_life_expectancy.sh", "update_user_life_span",
                            this.email.trim(), Integer.toString(patientRemainingLife));
                } catch (Exception e) {
                    Logger.log("Error while Updating life span " + e.getMessage());
                }
            }
            Logger.log("Updated profile for patient: " + this.email);
        } catch (Exception e) {
            System.out.println(color.red("Error updating profile: " + e.getMessage()));
            Logger.log("Error updating profile for user: " + this.email + " - " + e.getMessage());
        }
    }

    private void authenticateAndProceed(Scanner scanner) {
        char[] passwordArray = console.readPassword(color.blue("Enter your password: "));
        String password = new String(passwordArray);

        try {
            String result = ScriptExecutor.executeScript("script/user_management.sh", "validate_password",
                    this.email,
                    password);
            System.out.println(color.yellow(result + " this is the result"));
            if ("valid".equals(result)) {
                System.out.println(color.yellow("Password validated. Proceeding..."));
                patientFunctionality(scanner, this.email);
            } else {
                System.out.println(color.red("Invalid password."));
            }
            Logger.log("User authentication attempted for: " + this.email);
        } catch (Exception e) {
            System.out.println(color.red("Error during authentication: " + e.getMessage()));
            Logger.log(color.red("Error during authentication for user: " + this.email + " - " + e.getMessage()));
        }
    }

    private void patientFunctionality(Scanner scanner, String email) {
        while (true) {
            System.out.println("  ==================================");
            System.out.println(" | " + color.blue("          PATIENT MENU     ") + "      |");
            System.out.println(" | 1. | View Profile                |");
            System.out.println(" | 2. | Update Profile              |");
            System.out.println(" | 3. | Logout                      |");
            System.out.println("  ==================================");

            System.out.println();
            System.out.print(color.blue("Choose an option: "));
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    patientProfile(email);
                    break;
                case 2:
                    // System.out.println("Press enter if you do not want to update the field!!");
                    updatePatientProfile(scanner, email);
                    break;
                case 3:
                    return;
                default:
                    System.out.println(color.red("Invalid option. Please try again."));
            }
        }
    }

    private static Map<String, String> readUser(String email) {
        Map<String, String> userData = new HashMap<>();
        ColorText colorText = new ColorText();
        try {
            String result = ScriptExecutor.executeScript("script/update_user.sh", "read", email);
            System.out.println(colorText.yellow(result + "these are the data we are building"));
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
            System.out.println(colorText.red("Error executing read script: " + e.getMessage()));
        }
        System.out.println(userData);
        return userData;
    }

    private static void showUserDetails(Map<String, String> userData) {
        ColorText colorText = new ColorText();

        System.out.println("  ================================================");
        System.out.println(" |" + colorText.blue("          USER DETAILS TO UPDATE:") + "        ");
        System.out.println(" | First Name: " + userData.get("first_name"));
        System.out.println(" | Last Name: " + userData.get("last_name"));
        System.out.println(" | DOB: " + userData.get("dob"));
        System.out.println(" | HIV Status: " + userData.get("hiv_status"));
        System.out.println(" | Diagnosis Date: " + userData.get("diagnosis_date"));
        System.out.println(" | ART Status: " + userData.get("art_status"));
        System.out.println(" | ART Status Date: " + userData.get("art_start_date"));
        System.out.println(" | Country ISO: " + userData.get("country_iso"));
        System.out.println("  ================================================");
    }

    private static void updatePatientProfile(Scanner scanner, String email) {
        Map<String, String> userData = readUser(email);
        ColorText colorText = new ColorText();
        if (userData != null && !userData.isEmpty()) {
            showUserDetails(userData);

            System.out.print(colorText.blue(
                    "\nEnter the field you want to update: between (first_name, last_name, dob, hiv_status, diagnosis_date, art_status, art_start_date, country_iso) "));
            String fieldToUpdate = scanner.nextLine();
            System.out.println(fieldToUpdate + userData);
            if (userData.containsKey(fieldToUpdate)) {
                System.out.print(colorText.blue("Enter new value for " + fieldToUpdate + ": "));
                String newValue = scanner.nextLine();

                try {
                    if (updateUser(email, fieldToUpdate, newValue)) {
                        System.out.println(colorText.yellow("User data updated successfully."));
                    } else {
                        System.out.println(colorText.red("Failed to update user data."));
                    }
                } catch (Exception e) {
                    System.out.println(colorText.red("Error executing update script: " + e.getMessage()));
                }
            } else {
                System.out.println(colorText.red("Invalid field name."));
            }
        } else {
            System.out.println(colorText.red("User not found."));
        }

        // scanner.close();
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
            // System.out.println("\nPatient Profile");
            String[] parts = result.split(",");

            System.out.println("  ================================================");
            System.out.println(" |" + color.blue("          PATIENT PROFILE:") + "        ");
            System.out.println(" | First Name: " + parts[2]);
            System.out.println(" | last Name: " + parts[3]);
            System.out.println(" | Email: " + parts[0]);
            System.out.println(" | Date of Birth: " + parts[5]);
            System.out.println(" | Diagnosis Status: " + parts[6]);
            System.out.println(" | Diagnosis Date: " + parts[7]);
            System.out.println(" | ART Status: " + parts[8]);
            System.out.println(" | ART Date: " + parts[9]);
            System.out.println(" | Country ISO: " + parts[10]);
            System.out.println("  ================================================");

            Logger.log("Viewed profile for user: " + email);
        } catch (Exception e) {
            System.out.println(color.red("Error during getting patient Profile: "));
            Logger.log("Error viewing profile for user: " + email + " - " + e.getMessage());
        }
    }
}
