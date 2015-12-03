package example.generate;

import java.util.Map;

/**
 * @author Maarten Van Puymbroeck
 */
public class HierarchicalMappingSpec {

    public static void main(String[] args){
        String sourceString = "debtorAddress.streetNumber";
        String targetString = "ZISU_SEPA_SENDER_INFO.SND_HOUSENUM";

        MappingSpec.PropertyChain sourceChain = new MappingSpec.PropertyChain(sourceString, "", 0);
        MappingSpec.PropertyChain targetChain = new MappingSpec.PropertyChain(targetString, "", 0);



        NestedMapping  mapping = new NestedMapping();

//        for (int i=0; i<targetPath.length; i++){
//            NestedMapping nested = mapping.getNestedMapping(targetPath[i]);
//            if (nested == null){
//
//            }
//        }


    }



    public static interface Mapping {

    }

    public static class NestedMapping implements Mapping {
        Map<String, Mapping> mapping = new java.util.HashMap<String, Mapping>();

        public NestedMapping getNestedMapping(String property){
            return (NestedMapping) mapping.get(property);
        }
    }

    public static class DirectMapping implements Mapping {
        MappingSpec.PropertyChain source;
        MappingSpec.PropertyChain target;

        public DirectMapping(String sourceProperty, String targetProperty){
            this.source = new MappingSpec.PropertyChain(sourceProperty, "", 0);
            this.target = new MappingSpec.PropertyChain(targetProperty, "", 0);
        }

        public String toString() {
            return this.target.getPropertyChain() + "=" + this.source.getPropertyChain();
        }
    }


}
