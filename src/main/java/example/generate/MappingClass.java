package example.generate;

/**
 * @author Maarten Van Puymbroeck
 */
public class MappingClass {
    private final String pkg;
    private final String name;

    public MappingClass(String pkg, String name) {
        this.pkg = pkg;
        this.name = name;
    }

    public String getPackage() {
        return pkg;
    }

    public String getName() {
        return name;
    }

    public String getLowerCase(){
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public String getFullyQualifiedName() {
        String pkg = getPackage();
        return (pkg!=null && pkg.length() > 0)? getPackage() + '.' + getName() : getName();
    }
}
