package example.generatePojo.model;

/**
 * @author Maarten Van Puymbroeck
 */
public interface Pojo extends ClassDeclaration {

    Iterable<Property> getProperties();


}
