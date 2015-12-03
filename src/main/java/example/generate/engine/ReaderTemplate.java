package example.generate.engine;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Maarten Van Puymbroeck
 */
public class ReaderTemplate implements Template {
    private BufferedReader template;
    private String next;
    private int line = 0;

    public ReaderTemplate(BufferedReader template){
        this.template = template;
        next = readLine();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }


    public Line next(){
        String current = next;
        next = readLine();
        return new Line(current, line++);
    }

    private String readLine(){
        try {
            return template.readLine();
        } catch (IOException exc){
            throw new RuntimeException(exc);
        }
    }


    public boolean hasNext(){
        return next != null;
    }

}
