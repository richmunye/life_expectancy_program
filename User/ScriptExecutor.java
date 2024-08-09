package User;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ScriptExecutor {

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
