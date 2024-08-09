package User;

import java.util.Scanner;

public abstract class User {
    protected String uuid;
    protected String email;
    protected String firstName;

    public User(String uuid, String email) {
        this.uuid = uuid;
        this.email = email;
    }

    public abstract void menu(Scanner scanner);
}
