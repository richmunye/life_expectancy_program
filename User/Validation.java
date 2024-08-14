package User;

import java.io.Console;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import Views.ColorText;
import java.util.HashSet;
import java.util.Set;

public class Validation {
    private static final Console console = System.console();

    // Set of valid country ISO codes
    private static final Set<String> VALID_COUNTRY_CODES = new HashSet<>();

    static {
        String[] countryCodes = {
                "AFG", "ALB", "DZA", "ASM", "AND", "AGO", "AIA", "ATG", "ARG", "ARM",
                "ABW", "AUS", "AUT", "AZE", "BHS", "BHR", "BGD", "BRB", "BLR", "BEL",
                "BLZ", "BEN", "BMU", "BTN", "BOL", "BES", "BIH", "BWA", "BRA", "VGB",
                "BRN", "BGR", "BFA", "BDI", "KHM", "CMR", "CAN", "CPV", "CYM", "CAF",
                "TCD", "CHL", "CHN", "COL", "COM", "COG", "COK", "CRI", "CIV", "HRV",
                "CUB", "CUW", "CYP", "CZE", "COD", "DNK", "DJI", "DMA", "DOM", "TLS",
                "ECU", "EGY", "SLV", "GNQ", "ERI", "EST", "SWZ", "ETH", "FLK", "FRO",
                "FJI", "FIN", "FRA", "GUF", "PYF", "GAB", "GMB", "GEO", "DEU", "GHA",
                "GIB", "GRC", "GRL", "GRD", "GLP", "GUM", "GTM", "GGY", "GIN", "GNB",
                "GUY", "HTI", "HND", "HKG", "HUN", "ISL", "IND", "IDN", "IRN", "IRQ",
                "IRL", "IMN", "ISR", "ITA", "JAM", "JPN", "JEY", "JOR", "KAZ", "KEN",
                "KIR", "KWT", "KGZ", "LAO", "LVA", "LBN", "LSO", "LBR", "LBY", "LIE",
                "LTU", "LUX", "MAC", "MDG", "MWI", "MYS", "MDV", "MLI", "MLT", "MHL",
                "MTQ", "MRT", "MUS", "MYT", "MEX", "FSM", "MDA", "MCO", "MNG", "MNE",
                "MSR", "MAR", "MOZ", "MMR", "NAM", "NRU", "NPL", "NLD", "NCL", "NZL",
                "NIC", "NER", "NGA", "NIU", "PRK", "MKD", "MNP", "NOR", "OMN", "PAK",
                "PLW", "PSE", "PAN", "PNG", "PRY", "PER", "PHL", "POL", "PRT", "PRI",
                "QAT", "REU", "ROU", "RUS", "RWA", "BLM", "SHN", "KNA", "LCA", "MAF",
                "SPM", "VCT", "WSM", "SMR", "STP", "SAU", "SEN", "SRB", "SYC", "SLE",
                "SGP", "SXM", "SVK", "SVN", "SLB", "SOM", "ZAF", "KOR", "SSD", "ESP",
                "LKA", "SDN", "SUR", "SWE", "CHE", "SYR", "TWN", "TJK", "TZA", "THA",
                "TGO", "TKL", "TON", "TTO", "TUN", "TUR", "TKM", "TCA", "TUV", "UGA",
                "UKR", "ARE", "GBR", "USA", "VIR", "URY", "UZB", "VUT", "VEN", "VNM",
                "WLF", "ESH", "YEM", "ZMB", "ZWE"
        };
        for (String code : countryCodes) {
            VALID_COUNTRY_CODES.add(code);
        }
    }

    static String getValidatePassword() {
        ColorText color = new ColorText();
        String password = null;
        boolean passwordsMatch = false;

        while (!passwordsMatch) {
            char[] passwordArray = console.readPassword(color.blue("Password: "));
            password = new String(passwordArray);
            char[] confirmPasswordArray = console.readPassword(color.blue("Confirm Password: "));
            String confirmPassword = new String(confirmPasswordArray);

            if (password.equals(confirmPassword)) {
                passwordsMatch = true;
            } else {
                System.out.println(color.red("Passwords do not match. Please try again."));
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
                System.out.println(color.red("Invalid date format. Please enter the date in YYYY-MM-DD format."));
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
                System.out.println(color.red("Invalid input. Please enter 'true' or 'false'."));
            }
        }

        return false;
    }

// Country ISO Code Validation
public static String getValidCountryISOCode(Scanner scanner, String prompt) {
    ColorText color = new ColorText();
    String isoCode = "";
    boolean validCode = false;

    while (!validCode) {
        System.out.print(prompt);
        isoCode = scanner.nextLine().trim().toUpperCase();

        if (VALID_COUNTRY_CODES.contains(isoCode)) {
            validCode = true;
        } else {
            System.out.println(color.red("Invalid ISO code. Please enter a valid ISO code."));
        }
    }

    return isoCode;
}
}
