package User;

import java.util.Scanner;

import Views.ColorText;

public class Admin extends User {
    public static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String ADMIN_PASSWORD = "adminpass";
    ColorText color = new ColorText();

    public Admin() {
        super("admin_uuid", ADMIN_EMAIL);
    }

    public static boolean login(String email, String password) {
        return email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD);
    }

    @Override
    public void menu(Scanner scanner) {
        while (true) {
            System.out.println("  ==================================");
            System.out.println(" |" + color.blue("           ADMIN MENU    ") + "         |");
            System.out.println(" | 1. | Register a patient          |");
            System.out.println(" | 2. | Export patients data        |");
            System.out.println(" | 3. | Export Patients Stats       |");
            System.out.println(" | 4. | Logout                      |");
            System.out.println("  ==================================");
            System.out.println();
            System.out.print(color.blue("Choose an option: "));
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerPatient(scanner);
                    break;
                case 2:
                    exportPatients();
                    break;
                case 4:
                    return;
                default:
                    System.out.println(color.red("Invalid option. Please try again."));
                    System.out.println();
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
        System.out.print(color.blue("Enter patient email: "));
        String email = scanner.nextLine();

        try {
            String result = ScriptExecutor.executeScript("script/user_management.sh", "register", email);
            if (result.equals("exists")) {
                System.out.println(color.red("Patient already exists in the system."));
            } else {
                System.out.println(color.green("Patient registered successfully. UUID: " + result));
            }
            Logger.log("Registered patient with email: " + email);
        } catch (Exception e) {
            System.out.println(color.red("Error registering patient: " + e.getMessage()));
            Logger.log("Error registering patient: " + e.getMessage());
        }
    }
}
