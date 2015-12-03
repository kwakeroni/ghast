package example.generatePojo.spi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

/**
 * @author Maarten Van Puymbroeck
 */
public abstract class PojoWriterSupport<Input> {
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private Path src;

    public PojoWriterSupport(Path src) {
        this.src = src;
    }

    public void write(Input input) throws IOException {
        Path target = src.resolve(getTarget(input));
        Files.createDirectories(target.getParent());
        Files.write(target, toCode(input), UTF8, StandardOpenOption.CREATE_NEW);
    }


    public void printOut(Input input){
        for (String string : toCode(input)){
            System.out.println(string);
        }
    }

    protected abstract Path getTarget(Input input);
    protected abstract Iterable<String>  toCode(Input input);

    protected static Iterable<String> line(String contents){
        return Collections.singleton(contents);
    }

    protected static Iterable<String> EMPTY_LINE = Collections.singleton("");
    public static Iterable<String> emptyLine(){
        return EMPTY_LINE;
    }

}
