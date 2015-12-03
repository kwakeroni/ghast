package example.generatePojo.model;

/**
 * @author Maarten Van Puymbroeck
 */
public abstract class AbstractPojo implements Pojo {

    protected String generateToString() {
        StringBuilder builder = new StringBuilder(getName()).append("{").append(System.lineSeparator());
        for (Property property : getProperties()) {
            builder.append("    ").append(property).append(System.lineSeparator());
        }
        builder.append("}");
        return builder.toString();
    }

}
