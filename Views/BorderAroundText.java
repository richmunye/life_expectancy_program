package Views;

public class BorderAroundText {
    ColorText color = new ColorText();

    public void printBorderedText(String [] texts){
        int maximumLength = getTextMaximumLength(texts);
        int borderWidth = maximumLength + 4;

        printHorizontalLines(borderWidth);
        printEmptyLine(borderWidth);

        for(String text : texts){
            printTextLine(text, maximumLength);
        }

        printEmptyLine(borderWidth);
        printHorizontalLines(borderWidth);
    }

    private int getTextMaximumLength(String [] texts){
        int maximumLength = 0;
        for(String text : texts){
            if(text.length() > maximumLength){
                maximumLength = text.length();
            }
        }
        return maximumLength;
    }
    
    private void printHorizontalLines(int width){
        for(int i = 0; i < width; i++){
            System.out.print(color.green("-"));            
        }
        System.out.println();
    }

    private void printEmptyLine(int width){
        System.out.println("|");
        for(int i = 0; i < width - 2; i++){
            System.out.print(" ");
        }
        System.out.println(color.green("|"));
    }

    private void printTextLine(String text, int maximumLength){
        System.out.print(color.green("| " ) + text);
        int padding = maximumLength - text.length();
        for(int i = 0; i < padding; i++){
            System.out.print(" ");
        }
        System.out.println(color.green(" |"));
    }
}