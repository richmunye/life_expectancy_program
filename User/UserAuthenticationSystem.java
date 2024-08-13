package User;

import java.util.Scanner;
import Views.ColorText;

public class UserAuthenticationSystem {
  private Scanner scanner;
  ColorText color = new ColorText();

  public UserAuthenticationSystem(Scanner scanner) {
    this.scanner = scanner;
  }

  public void start() {
    while (true) {
      System.out.print(color.blue("Enter email or UUID: "));
      String input = scanner.nextLine();

      if (input.equals(Admin.ADMIN_EMAIL)) {
        adminLogin(scanner);
      } else if (input.contains("@")) {
        try {
          String result = ScriptExecutor.executeScript( "script/user_management.sh", "get_by_email", input);
          System.out.println(result);
          if (result != null && !result.isEmpty() && !result.equals(color.red("not_found"))) {
            String[] parts = result.split(",");
            if (parts.length >= 2) {
              patientLoginByEmail(input, scanner);
            } else {
              System.out.println(color.blue("Login with your UUID"));
              patientLoginByUUID(input, scanner);
            }
          } else {
            System.out.println(color.red("Email not found in the system."));
          }
        } catch (Exception e) {
          System.out.println(color.red("Error during login: " + e.getMessage()));
          Logger.log("Error during login for email: " + input + " - " + e.getMessage());
        }
      } else {
        patientLoginByUUID(input, scanner);
      }
    }
  }

  private void adminLogin(Scanner scanner) {
    System.out.print(color.blue("Enter admin password: "));
    String password = scanner.nextLine();

    if (Admin.login(Admin.ADMIN_EMAIL, password)) {
      new Admin().menu(scanner);
    } else {
      System.out.println(color.red("Invalid admin password."));
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
          System.out.println(color.red("Unexpected result format from the script. login by Email"));
        }
      } else {
        System.out.println(color.red("Email not found in the system."));
      }
    } catch (Exception e) {
      System.out.println(color.red("Error during login: " + e.getMessage()));
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
          System.out.println(color.red("Unexpected result format from the script."));
        }
      } else {
        System.out.println(color.red("Invalid UUID."));
      }
    } catch (Exception e) {
      System.out.println(color.red("Error during login: " + e.getMessage()));
      Logger.log("Error during login for UUID: " + uuid + " - " + e.getMessage());
    }
  }
}
