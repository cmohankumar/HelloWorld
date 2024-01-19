# HelloWorld
My first hello world Repo...
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String text = "Hello world:then is not enough:welcome my:to my space";

        String result = text.replaceAll("(?<=:[^:]*+), (?=[^:]++*:[^:]*+$)", 
                System.lineSeparator());

        System.out.println(result);
    }
}
