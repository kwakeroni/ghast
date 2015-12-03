package example.generatePojo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Maarten Van Puymbroeck
 */
public class MavenClassSource {

        private URLClassLoader loader;

        private MavenClassSource(Builder builder) {
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
                throw new IllegalArgumentException(e);
            }
        }


    private static class Artefact {

        final String groupId;
        final String artifactId;
        final String version;

        public Artefact(String groupId, String artifactId, String version) {
            this.artifactId = artifactId;
            this.groupId = groupId;
            this.version = version;
        }

        public URL getJarURL(Path root){
            try {
                return root.resolve(getJarPath()).toUri().toURL();
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }

        public Path getJarPath(){
            return Paths.get(groupId.replaceAll("\\.", "/"))
                        .resolve(artifactId)
                        .resolve(version)
                        .resolve(getJarFileName());
        }

        public String getJarFileName(){
            return MessageFormat.format("{1}-{2}.jar", groupId, artifactId, version);
        }
    }

    public static Builder newBuilder(ClassLoader parent){
        return new Builder(parent);
    }

    public static final class Builder {
        private Path repo;
        private ClassLoader parent;
        private List<URL> urls = new ArrayList<>();

        private Builder(ClassLoader parent) {
            this.parent = parent;
        }

        public Builder atRepository(String path){
            this.repo = Paths.get(path);
            return this;
        }

        public Builder addArtefact(String groupId, String artifactId, String version){
            urls.add(new Artefact(groupId, artifactId, version).getJarURL(this.repo));
            return this;
        }

        public Builder addCompilationTarget(Path path){
            try {
                urls.add(path.resolve("target/classes").toUri().toURL());
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
            return this;
        }

        public MavenClassSource build() {
            return new MavenClassSource(this);
        }
    }
}
