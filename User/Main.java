package User;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserAuthenticationSystem authSystem = new UserAuthenticationSystem(scanner);
        authSystem.start();
    }
}
