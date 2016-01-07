package example.generatePojo.extract;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import example.generatePojo.PojoExtractor;
import example.generatePojo.Util;
import example.generatePojo.model.AbstractPojo;
import example.generatePojo.model.AbstractType;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Maarten Van Puymbroeck
 */
abstract class GetterExtractor implements PojoExtractor<Class<?>> {

    protected static Predicate<Method> distinct() {
        return new Predicate<Method>() {
            private final Set<Method> encountered = new HashSet<>();
            @Override
            public boolean apply(Method input) {
                if (encountered.contains(input)){
                    return false;
                }
                encountered.add(input);
                return true;
            }
        };
    }

    protected static Function<Method, GetterProperty> toGetterProperty() {
        return new Function<Method, GetterProperty>() {
            @Override
            public GetterProperty apply(Method input) {
                return new GetterProperty(input);
            }
        };
    }

    private static final String[] prefixes = {"get", "is", "has"};

    protected static Predicate<Method> isGetter(){

        return new Predicate<Method>() {
            @Override
            public boolean apply(Method method) {
                boolean result =
                    method.getParameterTypes().length == 0
                    && (! method.getReturnType().equals(Void.TYPE))
                    && Util.hasPrefix(method.getName(), prefixes)
                    && (! "getClass".equals(method.getName()))
                    && (! "hashCode".equals(method.getName()));

//                if (! result){
//                    System.out.println("No getter: " + method);
//                }
                return result;
            }
        };
    }

    protected static class GetterProperty implements Property {


        private final Method method;

        public GetterProperty(Method method) {
            if (! isGetter().apply(method)) {
                throw new IllegalArgumentException("No getter method: " + method);
            }

            this.method = method;
        }

        @Override
        public String getName() {
            return StringUtils.uncapitalize(Util.removePrefix(method.getName(), prefixes));
        }

        @Override
        public Type getType() {
            return AbstractType.of(method.getGenericReturnType());
        }

        @Override
        public String toString() {
            return getType().getSimpleName() + " " + getName();
        }
    }

    protected static abstract class GetterPojo extends AbstractPojo {

        private final Class<?> baseClass;

        public GetterPojo(Class<?> baseClass) {
            this.baseClass = baseClass;
        }

        protected Class<?> getBaseClass(){
            return this.baseClass;
        }

        protected abstract Iterable<Class<?>> getClassHierarchy(Class<?> base);
        protected abstract Iterable<Method> getMethods(Class<?> type);

        @Override
        public Type getExtends() {
            return null;
        }

        @Override
        public Set<Type> getImplements() {
            return Collections.singleton(AbstractType.of(baseClass));
        }

        @Override
        public Iterable<Property> getProperties() {

            return Iterables.<Method, Property>transform(Iterables.filter(
                                                                             Iterables.concat(
                                                                                                 Iterables.transform(
                                                                                                                        getClassHierarchy(baseClass),
                                                                                                                        methods())
                                                                             ), distinct()), toGetterProperty()
            );

        }

        @Override
        public String getPackage() {
            return baseClass.getPackage().getName();
        }

        @Override
        public String getName() {
            return baseClass.getName();
        }

        @Override
        public String getSimpleName() {
            return baseClass.getSimpleName();
        }

        @Override
        public String toString() {
            return generateToString();
        }

        private Function<Class<?>, Iterable<Method>> methods() {
            return new Function<Class<?>, Iterable<Method>>() {
                @Override
                public Iterable<Method> apply(Class<?> input) {
                    return getMethods(input);
                }
            };
        }
    }
}
