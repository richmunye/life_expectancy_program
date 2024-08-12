package User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Views.BorderAroundText;
import Views.ColorText;

public class Patient extends User {
    public Patient(String uuid, String email) {
        super(uuid, email);
    }

    ColorText color = new ColorText();
    BorderAroundText border = new BorderAroundText();

    @Override
    public void menu(Scanner scanner) {
        if (firstName == null || firstName.isEmpty()) {
            System.out.println(color.purple("\nWelcome! Please complete your profile."));
            completeRegistration(scanner);
        } else {
            System.out.println(color.yellow("\nProfile is complete. Enter your password to continue."));
            authenticateAndProceed(scanner);
        }
    }

    private void completeRegistration(Scanner scanner) {
        System.out.print(color.blue("First Name: "));
        String firstName = scanner.nextLine();
        System.out.print(color.blue("Last Name: "));
        String lastName = scanner.nextLine();
        System.out.print(color.blue("Password: "));
        String password = scanner.nextLine();
        System.out.print(color.blue("Date of Birth (YYYY-MM-DD): "));
        String dateOfBirth = scanner.nextLine();
        System.out.print(color.blue("HIV Status (true/false): "));
        boolean hivStatus = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline
        String diagnosisDate = "";
        if (hivStatus) {
            System.out.print(color.blue("Diagnosis Date (YYYY-MM-DD): "));
            diagnosisDate = scanner.nextLine();
        }
        System.out.print(color.blue("ART Status (true/false): "));
        boolean artStatus = scanner.nextBoolean();
        scanner.nextLine(); // Consume newline
        String artStartDate = "";
        if (artStatus) {
            System.out.print(color.blue("ART Start Date (YYYY-MM-DD): "));
            artStartDate = scanner.nextLine();
        }
        System.out.print(color.blue("Country ISO Code: "));
        String countryISOCode = scanner.nextLine();

        try {
            String result = ScriptExecutor.executeScript("C:\\Program Files\\Git\\bin\\bash.exe", "script/user_management.sh", "update_profile",
                    this.uuid, this.email.trim(), firstName, lastName, password, dateOfBirth,
                    String.valueOf(hivStatus), diagnosisDate, String.valueOf(artStatus),
                    artStartDate, countryISOCode);
            System.out.println(result);
            if(result != null && !result.isEmpty()) {
                int patientRemainingLife = LifeSpanCalculator.calculatePatientLifeExpectancy("peace@gmail.com");
                System.out.println(color.green("Patient's Remaining Life: " + patientRemainingLife + " Years"));
            }
            Logger.log("Updated profile for patient: " + this.email);
        } catch (Exception e) {
            System.out.println(color.red("Error updating profile: " + e.getMessage()));
            Logger.log("Error updating profile for user: " + this.email + " - " + e.getMessage());
        }
    }

    private void authenticateAndProceed(Scanner scanner) {
        System.out.print(color.blue("Enter your password: "));
        String password = scanner.nextLine();

        try {
            String result = ScriptExecutor.executeScript("C:\\Program Files\\Git\\bin\\bash.exe", "script/user_management.sh", "validate_password",
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
            // System.out.println("\nPatient Menu");
            // System.out.println("1. View Profile");
            // System.out.println("2. Update Profile");
            // System.out.println("3. Logout");

            String [] choices = {
                "\nPatient Menu",
                "1. View Profile",
                "2. Update Profile",
                "3. Logout",
            };

            border.printBorderedText(choices);
            System.out.println();
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
                    System.out.println(color.red("Invalid option. Please try again."));
            }
        }
    }

    private static Map<String, String> readUser(String email) {
        Map<String, String> userData = new HashMap<>();
        ColorText colorText = new ColorText();
        try {
            String result = ScriptExecutor.executeScript( "C:\\Program Files\\Git\\bin\\bash.exe", "script/update_user.sh", "read", email);
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
        BorderAroundText borderText = new BorderAroundText();
        ColorText colorText = new ColorText();
        // System.out.println("\nUser Details to Update:");
        // System.out.println("first_name: " + userData.get("first_name"));
        // System.out.println("last_name: " + userData.get("last_name"));
        // System.out.println("dob: " + userData.get("dob"));
        // System.out.println("hiv_status: " + userData.get("hiv_status"));
        // System.out.println("diagnosis_date: " + userData.get("diagnosis_date"));
        // System.out.println("art_status: " + userData.get("art_status"));
        // System.out.println("art_start_date: " + userData.get("art_start_date"));
        // System.out.println("country_iso: " + userData.get("country_iso"));

        String [] userDetails = {
            colorText.red("\nUSER DETAILS TO UPDATE:"),
            colorText.black("First Name: " + userData.get("first_name")),
            colorText.black("Last Name: " + userData.get("last_name")),
            colorText.black("DOB: " + userData.get("dob")),
            colorText.black("HIV Status: " + userData.get("hiv_status")),
            colorText.black("Diagnosis Date: " + userData.get("diagnosis_date")),
            colorText.black("ART Status: " + userData.get("art_status")),
            colorText.black("ART Start Date: " + userData.get("art_start_date")),
            colorText.black("Country ISO: " + userData.get("country_iso"))
        };

        borderText.printBorderedText(userDetails);
    }

    private static void updatePatientProfile(Scanner scanner, String email){
        Map<String, String> userData = readUser(email);
        ColorText colorText = new ColorText();
        if (userData != null && !userData.isEmpty()) {
            showUserDetails(userData);

            System.out.print(colorText.blue("\nEnter the field you want to update: between (first_name, last_name, dob, hiv_status, diagnosis_date, art_status, art_start_date, country_iso) "));
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

        //scanner.close();
    }
    private static boolean updateUser(String email, String field, String newValue) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder( "C:\\Program Files\\Git\\bin\\bash.exe", "script/update_user.sh", "update", email, field, newValue);
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
            String result = ScriptExecutor.executeScript("C:\\Program Files\\Git\\bin\\bash.exe", "script/user_management.sh", "view_profile", email);
            //System.out.println("\nPatient Profile");
            String[] parts = result.split(",");
            // System.out.println("\nFirstName: " + parts[2]);
            // System.out.println("\nlastName: " + parts[3]);
            // System.out.println("\nemail: " + parts[0]);
            // System.out.println("\ndate of birth: " + parts[5]);
            // System.out.println("\ndiagnosis Status: " + parts[6]);
            // System.out.println("\ndiagnosis Date: " + parts[7]);
            // System.out.println("\nART Status: " + parts[8]);
            // System.out.println("\nART Date: " + parts[9]);
            // System.out.println("\nCountry ISO: " + parts[10]);

            String [] userDetails = {
                color.red("\nPATIENT PROFILE"),
                color.black("\nFirst Name: " + parts[2]),
                color.black("\nLast Name: " + parts[3]),
                color.black("\nEmail " + parts[0]),
                color.black("Date of Birth: " + parts[5]),
                color.black("Diagnosis Status: " + parts[6]),
                color.black("Diagnosis Date: " + parts[7]),
                color.black("ART Status: " + parts[8]),
                color.black("ART Date: " + parts[9]),
                color.black("Country ISO: " + parts[10])
            };
    
            border.printBorderedText(userDetails);

            Logger.log("Viewed profile for user: " + email);
        } catch (Exception e) {
            System.out.println(color.red("Error during getting patient Profile: " + e.getMessage()));
            Logger.log("Error viewing profile for user: " + email + " - " + e.getMessage());
        }
    }
}
