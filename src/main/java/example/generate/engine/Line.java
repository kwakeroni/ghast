package example.generate.engine;

/**
 * @author Maarten Van Puymbroeck
 */
public class Line {

    private final String string;
    private final String number;

    public Line(String string, int number) {
        this(string, String.valueOf(number));
    }
    public Line(String string, String number) {
        this.string = string;
        this.number = number;
    }

    public String getString() {
        return string;
    }

    public String getNumber() {
        return number;
    }
}
