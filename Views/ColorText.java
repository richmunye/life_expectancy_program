package Views;

public class ColorText {
    private static final String RESET = "\u001B[0m";
    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";;
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    
    public String black(String text){
        return BLACK + text + RESET;
    }

    public String red(String text){
        return  RED + text + RESET;
    }

    public String green(String text){
        return GREEN + text + RESET;
    }

    public String yellow(String text){
        return YELLOW + text + RESET;
    }

    public String blue(String text){
        return BLUE + text + RESET;
    }

    public String purple(String text){
        return PURPLE + text + RESET;
    }

    public String cyan(String text){
        return CYAN + text + RESET;
    }

    public String white(String text){
        return WHITE + text + RESET;
    }
}
