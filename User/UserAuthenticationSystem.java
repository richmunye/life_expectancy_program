package User;

import java.util.Scanner;
import java.io.Console;

public class UserAuthenticationSystem {
  private Scanner scanner;
  private final Console console;

  public UserAuthenticationSystem(Scanner scanner) {
    this.scanner = scanner;
    this.console = System.console();
  }

  public void start() {
    while (true) {
      System.out.print("Enter email or UUID: ");
      String input = scanner.nextLine();

      if (input.equals(Admin.ADMIN_EMAIL)) {
        adminLogin(scanner);
      } else if (input.contains("@")) {
        try {
          String result = ScriptExecutor.executeScript( "script/user_management.sh", "get_by_email", input);
          System.out.println(result);
          if (result != null && !result.isEmpty() && !result.equals("not_found")) {
            String[] parts = result.split(",");
            if (parts.length >= 2) {
              patientLoginByEmail(input, scanner);
            } else {
              System.out.println("Login with your UUID");
              patientLoginByUUID(input, scanner);
            }
          } else {
            System.out.println("Email not found in the system.");
          }
        } catch (Exception e) {
          System.out.println("Error during login: " + e.getMessage());
          Logger.log("Error during login for email: " + input + " - " + e.getMessage());
        }
      } else {
        patientLoginByUUID(input, scanner);
      }
    }
  }

  private void adminLogin(Scanner scanner) {
    if (console == null) {
      System.out.println("No console available. Cannot securely enter a password.");
      return;
    }

    char[] passwordArray = console.readPassword("Enter admin password: ");
    String password = new String(passwordArray);

    if (Admin.login(Admin.ADMIN_EMAIL, password)) {
      new Admin().menu(new Scanner(System.in));
    } else {
      System.out.println("Invalid admin password.");
      Logger.log("Failed admin login attempt");
    }
  }

  private void patientLoginByEmail(String email, Scanner scanner) {
    try {
      String result = ScriptExecutor.executeScript("script/user_management.sh", "get_by_email", email);
      if (result != null && !result.isEmpty()) {
        String[] parts = result.split(",");
        if (parts.length >= 2) {
          String uuid = parts[0];
          String firstName = parts[1];
          Patient patient = new Patient(uuid, email);
          patient.firstName = firstName;
          patient.menu(scanner);
        } else {
          System.out.println("Unexpected result format from the script. login by Email");
        }
      } else {
        System.out.println("Email not found in the system.");
      }
    } catch (Exception e) {
      System.out.println("Error during login: " + e.getMessage());
      Logger.log("Error during login for email: " + email + " - " + e.getMessage());
    }
  }

  private void patientLoginByUUID(String uuid, Scanner scanner) {
    try {
      String result = ScriptExecutor.executeScript("script/user_management.sh", "verify_uuid", uuid);
      if (result != null && !result.isEmpty()) {
        String[] parts = result.split(",");
        if (parts != null) {
          String email = parts[1];
          Patient patient = new Patient(uuid, email);
          patient.email = email;
          patient.menu(scanner);
        } else {
          System.out.println("Unexpected result format from the script.");
        }
      } else {
        System.out.println("Invalid UUID.");
      }
    } catch (Exception e) {
      System.out.println("Error during login: " + e.getMessage());
      Logger.log("Error during login for UUID: " + uuid + " - " + e.getMessage());
    }
  }
}
