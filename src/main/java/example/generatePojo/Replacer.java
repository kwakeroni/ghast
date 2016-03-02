package example.generatePojo;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Maarten Van Puymbroeck
 */
public class Replacer {


    public static void main(String[] args) throws Exception {

        Collection<? extends UnaryOperator<String>> regex = copyBuilderInterfaceFixer();
        String base = "/home/talend/voo-services/external/jira/jira-core/src/main/java/be/voo/esb/external/jira/core/domain";

        replace(regex, allIn(base));
    }


    private static Collection<? extends UnaryOperator<String>> copyBuilderInterfaceFixer(){
        return Arrays.asList(
            new Prepender("(\\r?\\n)public class ([A-Za-z0-9_$]+) implements ([A-Za-z0-9_$]+) \\{",
                "$1@JsonDeserialize(builder = $2.Builder.class)"),
            new Prepender("(\\r?\\n +)public static final class Builder",
                "$1@JsonPOJOBuilder(withPrefix = \"\")$1@JsonIgnoreProperties(ignoreUnknown = true)"),
            new RegexReplacement("(public static Builder newBuilder\\s*\\(\\s*)Jackson",
                m -> MessageFormat.format("{0}", m.group(1))),
            new RegexReplacement(
                "builder\\.([a-zA-Z0-9_$]+) \\= copy\\.([a-zA-Z0-9_$]+)\\;",
                m -> MessageFormat.format("builder.{0} = copy.{1}();",
                    m.group(1), fieldToGetter(m.group(2)))
            )
        );
    }

    private static Collection<? extends UnaryOperator<String>> methodToSignature(){
        return Collections.singleton(new RegexReplacement(
            "^([ \\t]+)public Builder ([^\\{]+) \\{\\s*(\\r?\\n)([^\\}]+)\\}",
            m -> MessageFormat.format("{0}Builder {1};{2}",
                m.group(1), m.group(2), m.group(3))
        ));
    }

    private static Collection<? extends UnaryOperator<String>> fieldToGetter(){
        return Collections.singleton(new RegexReplacement(
            "(\\s+)public static ([a-zA-Z0-9_$\\[\\]]+) ([a-zA-Z0-9_$]+)\\;",
            m -> MessageFormat.format("{0}{1} {2}();",
                m.group(1), m.group(2), fieldToGetter(m.group(3)))));
    }

    private static Collection<Path> paths(String basePath, Collection<String> paths){
        Path base = Paths.get(basePath);
        return paths.stream()
                    .map(name -> base.resolve(name))
                    .collect(Collectors.toList());
    }

    private static Collection<Path> allIn(String basePath) throws Exception {
        Path base = Paths.get(basePath);
        return StreamSupport.stream(Files.newDirectoryStream(base).spliterator(), false).collect(Collectors.toList());
    }

    private static void dump(Collection<? extends UnaryOperator<String>> regexes, Collection<Path> paths) throws Exception {
        process(regexes, paths, System.out::println);
    }

    private static void replace(Collection<? extends UnaryOperator<String>> regexes, Collection<Path> paths) throws Exception {
        for (Path path : paths){
                 replace(regexes, path);
        }
    }
    private static void replace(Collection<? extends UnaryOperator<String>> regexes, Path path) throws Exception {

        process(regexes, path, content -> {
            Path newFile = path.getParent().resolve(path.getFileName().toString() + ".new~");
            Path oldFile = path.getParent().resolve(path.getFileName().toString() + "~");

            Files.write(newFile, Collections.singleton(content));

            Files.move(path, oldFile, StandardCopyOption.REPLACE_EXISTING);
            Files.move(newFile, path);

            if (!confirm("Keep the changes to " + path.getFileName() + " ?")) {
                Files.move(oldFile, path, StandardCopyOption.REPLACE_EXISTING);
            }
        });

    }

    private static void process(Collection<? extends UnaryOperator<String>> regexes, Collection<Path> paths, ThrowingConsumer<String> action) throws Exception {
        for (Path path : paths){
                 process(regexes, path, action);
        }
    }

    private static void process(UnaryOperator<String> regex, Path path, ThrowingConsumer<String> action) throws Exception {
        process(Collections.singleton(regex), path, action);
    }

    private static void process(Collection<? extends UnaryOperator<String>> regexes, Path path, ThrowingConsumer<String> action) throws Exception {
        String result = readString(path);
        for(UnaryOperator<String> regex : regexes){
            result = regex.apply(result);
        }
        action.accept(result);
    }

    private static String fieldToGetter(String field){
        return "get" + StringUtils.capitalize(field);
    }

    private static String readString(Path file){
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean confirm(String message){
        return JOptionPane.showConfirmDialog(null, message) == JOptionPane.YES_OPTION;
    }

    private static class RegexReplacement implements UnaryOperator<String> {

        private final Pattern pattern;
        private final Function<Matcher, String> replacement;

        public RegexReplacement(String regex, Function<Matcher, String> replacement){
            this.pattern = Pattern.compile(regex,  Pattern.DOTALL | Pattern.MULTILINE);
            this.replacement = replacement;
        }

        public String apply(String line){
            Matcher matcher = this.pattern.matcher(line);

            StringBuilder result = new StringBuilder();
            int start = 0;

            while (matcher.find()){
                System.out.println("match: \"" + matcher.group(0) + "\"");
                if (matcher.start() > start){
                    result.append(line.substring(start, matcher.start()));
                }
                result.append(replacement.apply(matcher));
                start = matcher.end();
            }
            System.out.println("Last match at " + start);

            if (start < line.length()){
                result.append(line.substring(start));
            }

            return result.toString();
        }

    }

    private static class Prepender extends RegexReplacement {
        public Prepender(String regex, String prefixPattern) {
            super(regex, m -> MessageFormat.format(toMessageFormatPattern(prefixPattern), groups(m)) + m.group(0));
        }
    }

    private static String toMessageFormatPattern(String replacePattern){
        return replacePattern.replaceAll("\\$(\\d)", "{$1}");
    }

    private static String[] groups(Matcher matcher){
        String[] array = new String[matcher.groupCount()+1];
        for (int i=0; i<=matcher.groupCount(); i++){
            array[i] = matcher.group(i);
        }
        return array;
    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T t) throws Exception;
    }

}
