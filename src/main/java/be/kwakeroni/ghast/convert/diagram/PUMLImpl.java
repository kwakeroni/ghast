package be.kwakeroni.ghast.convert.diagram;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.BiConsumer;

class PUMLImpl {

    public static final String GRAPHVIZ_LOCATION = "C:/Programs/graphviz/dot.exe";
    private static final String DOT_ENVVAR = "GRAPHVIZ_DOT";

    static BiConsumer<byte[], OutputStream> imageWriter(FileFormat format) {
        return (bytes, destination) -> writeImage(bytes, new FileFormatOption(format), destination);
    }

    static void writeImage(byte[] bytes, FileFormatOption format, OutputStream destination) {
        writeImage(new String(bytes), format, destination);
    }

    static void writeImage(String source, FileFormatOption format, OutputStream destination) {
        try (Closeable env = pumlEnvironment()) {
            new SourceStringReader(source).outputImage(destination, format);
            destination.flush();
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    private static Closeable pumlEnvironment() {
        if (System.getProperty("GRAPHVIZ_DOT") != null) {
            return () -> {
            };
        }

        System.setProperty("GRAPHVIZ_DOT", GRAPHVIZ_LOCATION);
        return () -> System.clearProperty(DOT_ENVVAR);
    }


    static void convertSvgFileToEmf(Path svgFile, Path emfFile) {
        try {
            String[] command = new String[]{
                    "c:\\Program Files\\Inkscape\\inkscape.exe",
                    "-z",
                    "-f",
                    svgFile.toString(),
                    "--export-emf=" + emfFile.toString()
            };
            System.out.println("Running " + Arrays.toString(command));

            Process process = new ProcessBuilder().command(command).inheritIO().start();
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new IOException("Exit value " + process.exitValue());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
