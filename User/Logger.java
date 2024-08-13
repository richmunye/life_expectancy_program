package User;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Views.ColorText;

public class Logger {

    private static final String LOG_FILE = "application.log";

    public static void log(String message) {
        ColorText color = new ColorText();
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.println(timestamp + " - " + message);
        } catch (IOException e) {
            System.out.println(color.red("Error writing to log file: " + e.getMessage()));
        }
    }
}
