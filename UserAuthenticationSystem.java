import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

abstract class User {
  protected String uuid;
  protected String email;
  protected String firstName;

  public User(String uuid, String email) {
    this.uuid = uuid;
    this.email = email;
  }

  public abstract void menu(Scanner scanner);
}

class Admin extends User {
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
      System.out.println("2. Logout");
      System.out.print("Choose an option: ");
      int choice = scanner.nextInt();
      scanner.nextLine(); // Consume newline

      switch (choice) {
        case 1:
          registerPatient(scanner);
          break;
        case 2:
          return;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    }
  }

  private void registerPatient(Scanner scanner) {
    System.out.print("Enter patient email: ");
    String email = scanner.nextLine();

    try {
      String result = UserAuthenticationSystem.executeScript("./user_management.sh", "register", email);
      if (result.equals("exists")) {
        System.out.println("Patient already exists in the system.");
      } else {
        System.out.println("Patient registered successfully. UUID: " + result);
      }
    } catch (Exception e) {
      System.out.println("Error registering patient: " + e.getMessage());
    }
  }
}

class Patient extends User {
  public Patient(String uuid, String email) {
    super(uuid, email);
  }

  @Override
  public void menu(Scanner scanner) {
    if (firstName == null || firstName.isEmpty()) {
      System.out.println("\nWelcome! Please complete your profile.");
      updateProfile(scanner);
    } else {
      System.out.println("\nProfile is complete. Enter your password to continue.");
      authenticateAndProceed(scanner);
    }
  }

  private void updateProfile(Scanner scanner) {
    System.out.print("First Name: ");
    String firstName = scanner.nextLine();
    System.out.print("Last Name: ");
    String lastName = scanner.nextLine();
    System.out.print("Password: ");
    String password = scanner.nextLine();
    System.out.print("Date of Birth (YYYY-MM-DD): ");
    String dateOfBirth = scanner.nextLine();
    System.out.print("HIV Status (true/false): ");
    boolean hivStatus = scanner.nextBoolean();
    scanner.nextLine(); // Consume newline
    String diagnosisDate = "";
    if (hivStatus) {
      System.out.print("Diagnosis Date (YYYY-MM-DD): ");
      diagnosisDate = scanner.nextLine();
    }
    System.out.print("ART Status (true/false): ");
    boolean artStatus = scanner.nextBoolean();
    scanner.nextLine(); // Consume newline
    String artStartDate = "";
    if (artStatus) {
      System.out.print("ART Start Date (YYYY-MM-DD): ");
      artStartDate = scanner.nextLine();
    }
    System.out.print("Country ISO Code: ");
    String countryISOCode = scanner.nextLine();

    try {
      String result = UserAuthenticationSystem.executeScript("./user_management.sh", "update_profile",
          this.uuid, this.email.trim(), firstName, lastName, password, dateOfBirth,
          String.valueOf(hivStatus), diagnosisDate, String.valueOf(artStatus),
          artStartDate, countryISOCode);
      System.out.println(result);
    } catch (Exception e) {
      System.out.println("Error updating profile: " + e.getMessage());
    }
  }

  private void authenticateAndProceed(Scanner scanner) {
    System.out.print("Enter your password: ");
    String password = scanner.nextLine();

    try {
      String result = UserAuthenticationSystem.executeScript("./user_management.sh", "validate_password", this.email,
          password);
      System.out.println(result + " this is the result");
      if ("valid".equals(result)) {
        System.out.println("Password validated. Proceeding...");
        // Add code to proceed with the user's menu or functionality here
        patientFunctionality(scanner, this.email);
      } else {
        System.out.println("Invalid password.");
      }
    } catch (Exception e) {
      System.out.println("Error during authentication: " + e.getMessage());
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
          updateProfile(scanner);
        case 3:
          return;
        default:
          System.out.println("Invalid option. Please try again.");
      }
    }
  }

  private void patientProfile(String email) {
    try {
      String result = UserAuthenticationSystem.executeScript("./user_management.sh", "view_profile", email);
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
    } catch (Exception e) {
      System.out.println("Error during getting patient Profile: " + e.getMessage());
    }

  }
}

public class UserAuthenticationSystem {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print("Enter email or UUID: ");
      String input = scanner.nextLine();

      if (input.equals(Admin.ADMIN_EMAIL)) {
        adminLogin(scanner);
      } else if (input.contains("@")) {
        try {
          String result = executeScript("./user_management.sh", "get_by_email", input);
          System.out.println("this is the error>>>> " + result);
          if (result != null && !result.isEmpty() && !result.equals("not_found")) {
            String[] parts = result.split(",");
            if (parts.length >= 2) {
              System.out.println("no error here");
              patientLoginByEmail(input, scanner);
            } else {
              System.out.println("login with your UUID");
              patientLoginByUUID(input, scanner);
            }
          } else {
            System.out.println("Email not found in the system.");
          }
        } catch (Exception e) {
          System.out.println("Error during login: " + e.getMessage());
        }
      } else {
        patientLoginByUUID(input, scanner);
        // System.out.println("We are not available for Now");
      }
    }
  }

  private static void adminLogin(Scanner scanner) {
    System.out.print("Enter admin password: ");
    String password = scanner.nextLine();

    if (Admin.login(Admin.ADMIN_EMAIL, password)) {
      new Admin().menu(scanner);
    } else {
      System.out.println("Invalid admin password.");
    }
  }

  private static void patientLoginByEmail(String email, Scanner scanner) {
    try {
      String result = executeScript("./user_management.sh", "get_by_email", email);
      if (result != null && !result.isEmpty()) {
        String[] parts = result.split(",");
        if (parts.length >= 2) {
          String uuid = parts[0];
          String firstName = parts[1];
          System.out.println(parts[1].toString() + "these are the parts");
          // String firstName = parts[1];
          Patient patient = new Patient(uuid, email);
          patient.firstName = firstName; // Set firstName for checking profile
          // completion
          patient.menu(scanner);
        } else {
          System.out.println("Unexpected result format from the script. login by Email");
        }
      } else {
        System.out.println("Email not found in the system.");
      }
    } catch (Exception e) {
      System.out.println("Error during login: " + e.getMessage());
    }
  }

  private static void patientLoginByUUID(String uuid, Scanner scanner) {
    try {
      String result = executeScript("./user_management.sh", "verify_uuid", uuid);
      System.out.println(result + "this is the result " + uuid);
      if (result != null && !result.isEmpty()) {
        String[] parts = result.split(",");
        if (parts != null) {
          // String uuid = parts[0];
          String email = parts[1];
          Patient patient = new Patient(uuid, email);
          patient.email = email; // Set firstName for checking profile completion
          patient.menu(scanner);
        } else {
          System.out.println("Unexpected result format from the script.");
        }
      } else {
        System.out.println("Invalid UUID.");
      }
    } catch (Exception e) {
      System.out.println("Error during login 101: " + e.getMessage());
    }
  }

  public static String executeScript(String... command) throws Exception {
    ProcessBuilder pb = new ProcessBuilder(command);
    Process p = pb.start();

    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
    StringBuilder output = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      output.append(line);
    }

    int exitCode = p.waitFor();
    if (exitCode != 0) {
      throw new RuntimeException("Script execution failed with exit code: " + exitCode);
    }

    return output.toString();
  }
}
