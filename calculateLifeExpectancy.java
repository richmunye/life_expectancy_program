import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class calculateLifeExpectancy {
    public static void main(String[] args) {
        String email = "patient@gmail.com";
        System.out.println(getUserDetailsByEmail(email)); 
        // int patientRemainingLife = calculatePatientLifeExpectancy(email);
        // System.out.println("Patient's remaining life expectancy: " + patientRemainingLife + " years");
    }

    public static int calculatePatientLifeExpectancy(String email) {
        try {
            List<String> userDetails = getUserDetailsByEmail(email);
            System.out.println(userDetails + "details");
            if (userDetails.isEmpty()) {
                System.out.println("No user details found for email: " + email);
                return -1;
            }

            String dob = userDetails.get(0);
            boolean hivStatus = Boolean.parseBoolean(userDetails.get(1));
            String diagnosisDate = userDetails.get(2);
            boolean artStatus = Boolean.parseBoolean(userDetails.get(3));
            String artStartDate = userDetails.get(4);
            String countryIso = userDetails.get(5);

            double countryLifeExpectancy = getLifeExpectancyByCountryCode(countryIso);

            int roundedLifeExpectancy = (int) Math.ceil(countryLifeExpectancy);

            int birthYear = Integer.parseInt(dob.split("-")[0]);
            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            int patientAge = currentYear - birthYear;

            if (!hivStatus) {
                return roundedLifeExpectancy - patientAge;
            }

            int diagnosisYear = Integer.parseInt(diagnosisDate.split("-")[0]);
            int artStartYear = Integer.parseInt(artStartDate.split("-")[0]);
            int delayYears = artStartYear - diagnosisYear;

            double remainingLife = (roundedLifeExpectancy - patientAge) * 0.9;
            for (int i = 0; i < delayYears; i++) {
                remainingLife *= 0.9;
            }

            if (!artStatus) {
                remainingLife = Math.min(remainingLife, 5);
            }

            return (int) Math.ceil(remainingLife);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("error" + ex);
            return -1;
        }
    }

    private static List<String> getUserDetailsByEmail(String email) {


        try{
            List<String> userDetails = new ArrayList<>();
            String result = executeScript("bash", "./user_management.sh", "get_user_details_by_email", email);
            String[] parts = result.split(",");
            for(int i = 0; i < parts.length; i++){
                userDetails.add(parts[i]);
            }
            return userDetails;
            // ProcessBuilder builder = new ProcessBuilder("bash", "./user_management.sh", "get_user_details_by_email", email);
            // builder.redirectErrorStream(true);
            // Process process = builder.start();

            // try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            //     String line;
            //     while ((line = reader.readLine()) != null) {
            //         userDetails.add(line);
            //     }
            // }
            // process.waitFor();
            // return userDetails;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
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

    private static double getLifeExpectancyByCountryCode(String countryCode) {
        try{
            ProcessBuilder builder = new ProcessBuilder("bash", "./user_management.sh", "get_life_expectancy_by_code", countryCode);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            double lifeExpectancy;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line = reader.readLine();
                lifeExpectancy = Double.parseDouble(line);
            }
            process.waitFor();
            return lifeExpectancy;
        }
        catch(Exception ex){
            ex.printStackTrace();
            return 0.0;
        }
    }
}
