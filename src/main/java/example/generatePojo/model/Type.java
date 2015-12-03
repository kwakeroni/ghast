package example.generatePojo.model;

import com.google.common.base.Function;

/**
 * @author Maarten Van Puymbroeck
 */
public interface Type {

    public String getSimpleName();

    public boolean isEqualType(java.lang.reflect.Type type);

    public Class<?> getRawClass();

    public Type getParameterType(int index);

    public Iterable<Class<?>> getTypeDeclarationDependencies();

//        public static Function<Type, Class<?>> $getRawClass = new Function<Type, Class<?>>() {
//            @Override
//            public Class<?> apply(Type input) {
//                return input.getRawClass();
//            }
//    };

            public static Function<Type, Iterable<Class<?>>> $getTypeDeclarationDependencies = new Function<Type, Iterable<Class<?>>>() {
            @Override
            public Iterable<Class<?>> apply(Type input) {
                return input.getTypeDeclarationDependencies();
            }
    };
}
