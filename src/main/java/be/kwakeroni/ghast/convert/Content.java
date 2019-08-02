package be.kwakeroni.ghast.convert;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents some kind of content or data of a certain {@code Type} that is available in a certain {@code Form}.
 *
 * @param <Type> The type of the data (for example: a Java source file, a Java class file, a PNG image, ...)
 * @param <Form> The way the data is available to the application (for example: as a file on the file system, or as a byte array)
 */
public interface Content<Type, Form> {

    /**
     * @return The type of the data
     */
    Type type();

    /**
     * @return The data
     */
    Form get();

    /**
     * Maps this content to another type of content.
     * <p>
     * Mapping is a <em>lazy</em> operation.
     * The actual mapping only happens when the data of the final mapped Content is retrieved
     * using the {@link #get()} method.
     * </p>
     * <p>
     * Mappers that transform only the data act as <em>type converters</em>. For example:
     * <ul>
     * <li>An image converter could transform a PNG file to a JPG file</li>
     * <li>A compiler could transform a Java source file to a Java class file</li>
     * </ul>
     * </p>
     * <p>
     * Other mappers will transform only the form of the data. For example:
     * A File-to-byte-array mapper might read the contents of a file into the memory
     * </p>
     * <p>
     * Some mappers will transform both the type and the content of the data.
     * This happens if the backing implementation of the mapper always produces a certain Form of output,
     * which may differ from the original Form.
     * It is preferable to expose the produced Form by default instead of performing
     * another conversion to the original Form, which may be unnecessary for the client.
     * </p>
     * <p>
     * Mappers may produce a <em>specialized</em> kind of Content,
     * typically providing specific functionality related to the new Form of the Content.
     * </p>
     *
     * @param mapper           The mapper function
     * @param <NewContentType> The new type of Content provided by the mapper
     * @return The mapped Content
     */
    default <NewContentType extends Content<?, ?>> NewContentType to(Function<? super Content<Type, Form>, NewContentType> mapper) {
        return mapper.apply(this);
    }

    /**
     * Creates a new Content instance with the given {@code type} and with data retrieved from the given {@code dataSupplier}.
     *
     * @param type         The type of the data
     * @param dataSupplier Provides the data in a specific {@code Form}
     * @param <Type>       The type of the data
     * @param <Form>       The way the data is available to the application
     * @return a new Content instance
     */
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
