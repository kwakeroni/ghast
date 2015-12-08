package example.generatePojo.dependency;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

/**
 * @author Maarten Van Puymbroeck
 */
class MavenProject implements ClassSource.ConfiguredURLSupplier {

    private final Path modulePath;

    public MavenProject(Path modulePath) {
        this.modulePath = modulePath;
    }

    @Override
    public URL get(ClassSource.Configuration config) {
        try {
            return modulePath.resolve("target/classes").toUri().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
