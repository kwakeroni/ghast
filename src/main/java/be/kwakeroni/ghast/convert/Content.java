package be.kwakeroni.ghast.convert;

import java.util.function.Function;
import java.util.function.Supplier;

public interface Content<Type, Form> {

    Type type();
    default Type _type() {
        throw new UnsupportedOperationException();
    }
    Form get();

    default <NewForm> Content<Type, NewForm> to(Function<? super Form, ? extends NewForm> mapper) {
        return Content.of(type(), () -> mapper.apply(this.get()));
    }

    default <NewContentType extends Content<Type, ?>> NewContentType to(FormMapper<Type, Content<Type, Form>, NewContentType> mapper) {
        return mapper.map(this);
    }

    default <NewType> Content<NewType, Form> mapTo(TypeMapper<Type, NewType, Form> mapping) {
        throw new UnsupportedOperationException();
    }

    default <NewContentType extends Content<?, ?>> NewContentType mapTo(Function<? super Content<Type, Form>, NewContentType> mapper){
        return mapper.apply(this);
    }

    default <NewContentType extends Content<Type, Form>> NewContentType as(Function<? super Content<Type, Form>, ? extends NewContentType> wrapper) {
        return wrapper.apply(this);
    }

    static <Type, Form> Content<Type, Form> of(Type type, Supplier<? extends Form> dataSupplier) {
        return new Content<Type, Form>(){
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
