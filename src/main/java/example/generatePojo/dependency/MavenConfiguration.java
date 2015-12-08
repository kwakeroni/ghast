package example.generatePojo.dependency;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Maarten Van Puymbroeck
 */
public class MavenConfiguration {

    private Path repository;

    public Path getRepository() {
        return this.repository;
    }

    public MavenConfiguration at(String path) {
        this.repository = Paths.get(path);
        return this;
    }

}
