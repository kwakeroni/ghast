package example.generatePojo.model;

/**
 * @author Maarten Van Puymbroeck
 */
public interface ClassDeclaration {

    public String getPackage();
    public String getName();
    public String getSimpleName();
    public Type getExtends();
    public Iterable<Type> getImplements();

}
