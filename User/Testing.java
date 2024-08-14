package User;

import java.util.Scanner;

import Views.ColorText;;

public class Testing {
    public static void main (String [] args){
        Scanner scanner = new Scanner(System.in);
        ColorText color = new ColorText();
        
        System.out.println(" =================================================");
        System.out.print("|" + color.blue(" Enter patient email: "));
        System.out.println();
        System.out.println(" =================================================");


        System.out.println(" ====================================================");
        System.out.println("|       " + color.purple("Welcome! Please complete your profile.") + "       |");
        System.out.println(" ====================================================");
        System.out.println();
        System.out.println();


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
            artStartDate = Validation.getValidDate(scanner, "|" + color.blue(" Art Start (YYYY-MM-DD): "));
        }
        System.out.print("|" + color.blue(" Country ISO Code: "));
        String countryISOCode = scanner.nextLine();

        System.out.println(" ================================================================");
    }
}
