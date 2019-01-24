package be.kwakeroni.ghast.convert.form;

import be.kwakeroni.ghast.convert.Content;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FileContent<Type> extends Content<Type, Path> {

    default FileContent<Type> toFile(String destination) {
        return toFile(Paths.get(destination));
    }

    default FileContent<Type> toFile(Path destination) {
        return FileContent.of(type(), () -> {
            try {
                return Files.copy(this.get(), destination);
            } catch (IOException exc) {
                throw new UncheckedIOException(exc);
            }
        });
    }


    static byte[] toBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    default ByteArrayContent<Type> toBytes() {
        return ByteArrayContent.of(type(), () -> toBytes(this.get()));
    }

    static <Type> FileContent<Type> wrap(Content<Type, Path> content) {
        if (content instanceof FileContent) {
            return (FileContent<Type>) content;
        }
        return FileContent.of(content.type(), content::get);
    }

    static <Type> FileContent<Type> of(Type type, String sourceFile) {
        return of(type, () -> Paths.get(sourceFile));
    }

    static <Type> FileContent<Type> of(Type type, Path source) {
        return of(type, () -> source);
    }

    static <Type> FileContent<Type> of(Type type, Supplier<? extends Path> supplier) {
        return new FileContent<Type>() {
            @Override
            public Type type() {
                return type;
            }

            @Override
            public Path get() {
                return supplier.get();
            }
        };
    }

    static <OldType, OldForm, NewType> Function<Content<OldType, OldForm>, FileContent<NewType>> transformer(NewType newType, BiConsumer<OldForm, Path> transformer) {
        return content -> transforming(content, newType, transformer);
    }

    static <OldType, OldForm, NewType> FileContent<NewType> transforming(Content<OldType, OldForm> content, NewType newType, BiConsumer<OldForm, Path> transformer) {
        return new FileContent<NewType>() {
            private Path target = null;

            @Override
            public NewType type() {
                return newType;
            }

            @Override
            public Path get() {
                ensureTargetDefined();
                transformer.accept(content.get(), this.target);
                return this.target;
            }

            @Override
            public FileContent<NewType> toFile(Path destination) {
                if (this.target == null) {
                    this.target = destination;
                    return this;
                } else {
                    return FileContent.super.toFile(destination);
                }
            }

            private void ensureTargetDefined() {
                if (target == null) {
                    try {
                        this.target = Files.createTempFile("ghast", ".tmp");
                    } catch (IOException exc) {
                        throw new UncheckedIOException(exc);
                    }
                }
            }
        };
    }


}
