package example.generatePojo.dependency;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author Maarten Van Puymbroeck
 */
public class ClassSource {

        private URLClassLoader loader;

        private ClassSource(Builder builder) {
            this.loader = new URLClassLoader(builder.urls.toArray(new URL[builder.urls.size()]), builder.parent);

            for (URL url : builder.urls){
                System.out.print(url + " - ");
                try {
                    System.out.println(url.openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        public Class<?> forName(String name){
            try {
                return this.loader.loadClass(name);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("Class not found in " + Arrays.toString(this.loader.getURLs()), e);
            }
        }


    public static Builder newBuilder(ClassLoader parent){
        return new Builder(parent);
    }

    public static MavenConfiguration maven(){
        return new MavenConfiguration();
    }

    public static ConfiguredURLSupplier artefact(String groupId, String artifactId, String version){
        return new MavenArtefact(groupId, artifactId, version);
    }

    public static ConfiguredURLSupplier mavenModule(String path){
        return mavenModule(Paths.get(path));
    }

    public static ConfiguredURLSupplier mavenModule(Path path){
        return new MavenProject(path);
    }

    public static final class Builder implements Configuration {

        private ClassLoader parent;
        private List<URL> urls = new ArrayList<>();
        private Map<Class<?>, Object> config = new HashMap<>(1);

        private Builder(ClassLoader parent) {
            this.parent = parent;
        }

        public Builder configure(Object configuration){
            this.config.put(configuration.getClass(), configuration);
            return this;
        }

        @Override
        public <T> T getConfiguration(Class<T> type) {
            return type.cast(config.containsKey(type) ? config.get(type) : doThrow(noSuchElement(), type));
        }

        @Override
        public boolean hasConfiguration(Class<?> type) {
            return config.containsKey(type);
        }

        public Builder add(URL url){
            urls.add(url);
            return this;
        }

        public Builder add(Supplier<? extends URL> urlSupplier){
            return add(urlSupplier.get());
        }

        public Builder add(ConfiguredURLSupplier urlSupplier){
            return add(urlSupplier.get(this));
        }


        public Builder addCompilationTarget(Path path){
            return this;
        }

        public ClassSource build() {
            return new ClassSource(this);
        }
    }

    public interface ConfiguredURLSupplier {
        URL get(Configuration config);
    }

    interface Configuration {
        boolean hasConfiguration(Class<?> type);
        <T> T getConfiguration(Class<T> type);
    }

    private static <T, I, X extends Throwable> T doThrow(Function<? super I, ? extends X> throwable, I input) throws X {
        throw throwable.apply(input);
    }

    private static Function<Object, NoSuchElementException> noSuchElement(){
        return new Function<Object, NoSuchElementException>() {
            @Override
            public NoSuchElementException apply(Object input) {
                return new NoSuchElementException(String.valueOf(input));
            }
        };
    }
}
