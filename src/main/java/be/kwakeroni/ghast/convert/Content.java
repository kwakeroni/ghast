package be.kwakeroni.ghast.convert;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Content<Type, Form> {

    Type type();

    Form get();

    default <NewContentType extends Content<?, ?>> NewContentType to(Function<? super Content<Type, Form>, NewContentType> mapper) {
        return mapper.apply(this);
    }

    static <Type, Form> Content<Type, Form> of(Type type, Supplier<? extends Form> dataSupplier) {
        return new Content<Type, Form>() {
            @Override
            public Type type() {
                return type;
            }

            @Override
            public Form get() {
                return dataSupplier.get();
            }
        };
    }
}
