package example.generatePojo.spi.json;

import example.generatePojo.PojoExtractor;
import example.generatePojo.spi.CodeWriterSupport;
import example.generatePojo.model.Pojo;
import example.generatePojo.model.Property;
import example.generatePojo.model.Type;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Maarten Van Puymbroeck
 */
public class JSonExampleWriter extends CodeWriterSupport<Pojo> {

    private static final Collection<Class<?>> INTEGER_CLASSES = Arrays.<Class<?>> asList(BigInteger.class, Long.class, Long.TYPE, Integer.class, Integer.TYPE, Short.class, Short.TYPE, Byte.class, Byte.TYPE);
    private static final Collection<Class<?>> DECIMAL_CLASSES = Arrays.<Class<?>> asList(BigDecimal.class, Double.class, Double.TYPE, Float.class, Float.TYPE);


    private final PojoExtractor<Class<?>> extractor;

    public JSonExampleWriter(Path src, PojoExtractor<Class<?>> extractor) {
        super(src);
        this.extractor = extractor;
    }


    @Override
    protected Path getTarget(Pojo pojo) {
        return Paths.get(pojo.getSimpleName() + ".json");
    }

    @Override
    protected Iterable<String> toCode(Pojo pojo) {
        return Iterables.concat(
                                   line("{"),
                                   propertiesToCode(pojo, ""),
                                   line("}")
        );
    }

    private Iterable<String> propertiesToCode(Pojo pojo, String prefix){
        return removeLastComma(Iterables.concat(Iterables.transform(pojo.getProperties(), toCode(prefix))));
    }

    private Iterable<String> toCode(Property property, String prefix){
        return toExampleValue(MessageFormat.format("{0}\"{1}\": ", prefix, property.getName()), prefix + "    ", property.getType(), ",", Optional.of(property.getName()));
    }

    private Iterable<String> toExampleValue(String firstLinePrefix, String prefix, Type propertyType, String lastLinePostfix, Optional<String> propertyName){
       if (isSimpleValue(propertyType)){
           return line(firstLinePrefix + toSimpleExampleValue(propertyType, propertyName) + lastLinePostfix);
       } else if (propertyType.getRawClass().isArray() || Collection.class.isAssignableFrom(propertyType.getRawClass())){
           return toCollectionExampleValue(firstLinePrefix, prefix, propertyType, lastLinePostfix);
       } else {
           return toObjectExampleValue(firstLinePrefix, prefix, propertyType, lastLinePostfix);
       }
    }

    private boolean isSimpleValue(Type propertyType){
        Class<?> type = propertyType.getRawClass();
        return type.isEnum()
                || type.isPrimitive()
                || type.getName().startsWith("java.lang")
                || java.util.Date.class.isAssignableFrom(type)
                || java.lang.Number.class.isAssignableFrom(type)
                ;
    };

    private String toSimpleExampleValue(Type propertyType, Optional<String> propertyName){
        Class<?> type = propertyType.getRawClass();
        if (type.isEnum()){
            return quote(((Enum<?>) type.getEnumConstants()[0]).name());
        } else if (INTEGER_CLASSES.contains(type)){
            return "18";
        } else if (DECIMAL_CLASSES.contains(type)){
            return "18.18";
        } else if (Boolean.class == type || Boolean.TYPE == type){
            return "true";
        } else if (CharSequence.class.isAssignableFrom(type)){
            return quote(propertyName.or("abcd"));
        } else if (java.util.Date.class.isAssignableFrom(type)){
            return quote("1981-12-14");
        } else {
            throw new IllegalArgumentException(propertyType + " " + propertyName.or("<anonymous>"));
        }
    }

    private static final String quote(String contents){
        return '\"' + contents + '\"';
    }

    private Iterable<String> toObjectExampleValue(String firstLinePrefix, String prefix, Type propertyType, String lastLinePostfix){
        Pojo pojo = extractor.getPojo(propertyType.getRawClass());
        return Iterables.concat(
                                   line(firstLinePrefix + "{"),
                                   propertiesToCode(pojo, prefix),
                                   line (prefix + "}" + lastLinePostfix)
        );
    }

    private Iterable<String> toCollectionExampleValue(String firstLinePrefix, String prefix, Type propertyType, String lastLinePostfix){

        return Iterables.concat(
                                   toExampleValue(firstLinePrefix + "[", prefix + " ", propertyType.getParameterType(0), ",", Optional.<String> absent()),
                                   toExampleValue(prefix + " ", prefix + " ", propertyType.getParameterType(0), "]" + lastLinePostfix, Optional.<String> absent())
        );

    }


    private Function<Property, Iterable<String>> toCode(final String prefix){
        return new Function<Property, Iterable<String>>() {
            @Override
            public Iterable<String> apply(Property input) {
                return toCode(input, prefix);
            }
        };
    }

    private Function<String, String> commaSeparated(){
        return new Function<String, String>() {
            boolean first = true;
            @Override
            public String apply(String input) {
                if (first){
                    first = false;
                    return input;
                }
                return null;
            }
        };
    }

    private Iterable<String> removeLastComma(final Iterable<String> iterable){
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new RemoveLastCommaIterator(iterable.iterator());
            }
        };
    }

    private static final class RemoveLastCommaIterator implements Iterator<String> {
        private final Iterator<String> delegate;

        public RemoveLastCommaIterator(Iterator<String> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public String next() {
            String next = delegate.next();
            if (delegate.hasNext()){
                return next;
            } else {
                return next.substring(0, next.length()-1);
            }

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
