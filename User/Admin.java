package User;

import java.util.Scanner;

public class Admin extends User {
    public static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "adminpass";

    public Admin() {
        super("admin_uuid", ADMIN_EMAIL);
    }

    public static boolean login(String email, String password) {
        return email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD);
    }

    @Override
    public void menu(Scanner scanner) {
        while (true) {
            System.out.println("\nAdmin Menu");
            System.out.println("1. Register a patient");
            System.out.println("2. Export patients data");
            System.out.println("3. Export Patients Stats ");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerPatient(scanner);
                    break;
                case 2:
                    exportPatients();
                    break;
                case 3:
                    analytics();
                case 4:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void exportPatients() {
        try {
            String result = ScriptExecutor.executeScript( "script/user_management.sh", "export_patient_data");
            System.out.println(result);
            Logger.log("Exported patient data");
        } catch (Exception e) {
            Logger.log("Error exporting patients: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void registerPatient(Scanner scanner) {
        System.out.print("Enter patient email: ");
        String email = scanner.nextLine();

        try {
            String result = ScriptExecutor.executeScript("script/user_management.sh", "register", email);
            if (result.equals("exists")) {
                System.out.println("Patient already exists in the system.");
            } else {
                System.out.println("Patient registered successfully. UUID: " + result);
            }
            Logger.log("Registered patient with email: " + email);
        } catch (Exception e) {
            System.out.println("Error registering patient: " + e.getMessage());
            Logger.log("Error registering patient: " + e.getMessage());
        }
    }

    private void analytics (){
        try{
            String result = ScriptExecutor.executeScript("script/analyze_life_expectancy.sh", "analyze_life_expectancy", "12");
            System.out.println(result);
            Logger.log("Exported the system analytics");
        }catch (Exception e){
            Logger.log("Error exporting analytics " + e.getMessage());
        }
    }
}
