package example.generate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Maarten Van Puymbroeck
 */
public class MappingSpec {

    private PropertyChain source;
    private PropertyChain target;


    public MappingSpec(String sourceSpec, String sourcePkg, int sourceSkip, String targetSpec, String targetPkg, int targetSkip){
        this.source = new PropertyChain(sourceSpec, sourcePkg, sourceSkip);
        this.target = new PropertyChain(targetSpec, targetPkg, targetSkip);
    }

    public PropertyChain getSource() {
        return source;
    }

    public PropertyChain getTarget() {
        return target;
    }

    public static class PropertyChain {
        List<Property> properties;

        public PropertyChain(String spec, String pkg, int skip){
            String[] parts = spec.split("\\.");
            this.properties = new java.util.ArrayList<Property>(parts.length);
            int skipped = 0;
            for (String part : parts){
                if (++skipped > skip){
                    this.properties.add(new Property(part, pkg));
                }
            }
        }

        private PropertyChain(List<Property> properties){
            this.properties = new java.util.ArrayList<Property>(properties);
        }

        public String getGetterChain(){
            StringBuilder builder = new StringBuilder();
            for (Property property : properties){
                builder.append('.').append(property.getGetter()).append("()");
            }
            return builder.toString();
        }

        public String getPropertyChain(){
            StringBuilder builder = new StringBuilder();
            for (Property property : properties){
                builder.append('.').append(property.getLowerCase());
            }
            return builder.toString();

        }

        public Collection<Property> getPath(){
            if (this.properties.size() > 1){
                return this.properties.subList(0, properties.size() - 1);
            } else {
                return Collections.emptySet();
            }
        }

        public Property getProperty(){
            return properties.get(properties.size() - 1);
        }

        public Property getRoot(){
            return properties.get(0);
        }

        public PropertyChain getSubChain(int fromIndex){
            return new PropertyChain(this.properties.subList(fromIndex, this.properties.size()));
        }
    }

    public static class Property {
        String lcase;
        String ucase;

        MappingClass mappingClass;

        public Property(String name){
            this.lcase = name.substring(0, 1).toLowerCase() + name.substring(1).replaceAll("_", "");
            this.ucase = name.substring(0, 1).toUpperCase() + name.substring(1).replaceAll("_", "");
        }

        public Property(String name, String pkg){
            this(name);
            if (pkg != null){
                this.mappingClass = new MappingClass(pkg, this.ucase);
            }
        }

        public String getLowerCase(){
            return this.lcase;
        }

        public String getUpperCase(){
            return this.ucase;
        }

        public MappingClass getType(){
            return this.mappingClass;
        }

        private void setMappingClass(MappingClass type){
            this.mappingClass = type;
        }

        public String getGetter(){
            return "get" + this.ucase;
        }

        public String getSetter(){
            return "set" + this.ucase;
        }

        public String getTester(){
            return "test" + this.ucase;
        }

        public String toString(){
            return this.lcase;
        }
    }

}
