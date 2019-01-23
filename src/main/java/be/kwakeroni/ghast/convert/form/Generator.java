package be.kwakeroni.ghast.convert.form;

import be.kwakeroni.ghast.convert.Content;
import be.kwakeroni.ghast.convert.TypeMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Generator<Type> extends Content<Type, Consumer<OutputStream>> {

    default <NewType> Generator<NewType> mapTo(TypeMapper<Type, NewType, Consumer<OutputStream>> mapping) {
        return mapping.map(this, Generator::wrap);
    }

    default ByteArrayContent<Type> toBytes() {
        return ByteArrayContent.of(type(), () -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            this.get().accept(baos);
            return baos.toByteArray();
        });
    }

    default FileContent<Type> toFile() {
        try {
            return toFile(Files.createTempFile("ghast", ".tmp"));
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    default FileContent<Type> toFile(String fileName) {
        return toFile(Paths.get(fileName));
    }

    default FileContent<Type> toFile(Path fileName) {
        return FileContent.of(type(), () -> {
            try (OutputStream stream = Files.newOutputStream(fileName)) {
                this.get().accept(stream);
                stream.flush();
            } catch (IOException exc) {
                throw new UncheckedIOException(exc);
            }
            return fileName;
        });
    }

    static <Type> Generator<Type> wrap(Content<Type, Consumer<OutputStream>> content) {
        if (content instanceof Generator) {
            return (Generator<Type>) content;
        }
        return Generator.of(content.type(), content::get);
    }

    static <Type> Generator<Type> of(Type type, Consumer<OutputStream> source) {
        return of(type, () -> source);
    }
    static <Type> Generator<Type> of(Type type, Supplier<? extends Consumer<OutputStream>> source) {
        return new Generator<Type>() {
            @Override
            public Type type() {
                return type;
            }

            @Override
            public Consumer<OutputStream> get() {
                return source.get();
            }
        };
    }

    static <OldType, OldForm, NewType> Function<Content<OldType, OldForm>, Generator<NewType>> transformer(NewType newType, BiConsumer<OldForm, OutputStream> transformer) {
        return content -> transforming(content, newType, transformer);
    }

    static <OldType, OldForm, NewType> Generator<NewType> transforming(Content<OldType, OldForm> content, NewType newType, BiConsumer<OldForm, OutputStream> transformer){
        return Generator.of(newType, () -> outputStream -> {
            transformer.accept(content.get(), outputStream);
        });
    }
}
