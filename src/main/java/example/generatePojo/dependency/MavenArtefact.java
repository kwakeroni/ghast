package example.generatePojo.dependency;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

/**
 * @author Maarten Van Puymbroeck
 */
class MavenArtefact implements ClassSource.ConfiguredURLSupplier {

    final String groupId;
    final String artifactId;
    final String version;

    public MavenArtefact(String groupId, String artifactId, String version) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
    }

    public URL get(ClassSource.Configuration configuration) {
        try {
            Path repository = configuration.getConfiguration(MavenConfiguration.class).getRepository();
            return repository.resolve(getJarPath()).toUri().toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Path getJarPath() {
        return Paths.get(groupId.replaceAll("\\.", "/"))
                   .resolve(artifactId)
                   .resolve(version)
                   .resolve(getJarFileName());
    }

    public String getJarFileName() {
        return MessageFormat.format("{1}-{2}.jar", groupId, artifactId, version);
    }
}
