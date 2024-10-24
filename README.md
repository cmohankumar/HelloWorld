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

CI-Specific Configuration
For CI builds specifically:
Set the environment variable SPOTLESS_DISABLE_LINE_ENDINGS=true in your CI configuration.
Or use a CI-specific Spotless profile in your build tool configuration that disables line ending checks.
