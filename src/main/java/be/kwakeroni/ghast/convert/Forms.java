package be.kwakeroni.ghast.convert;

import be.kwakeroni.ghast.convert.form.ByteArrayContent;
import be.kwakeroni.ghast.convert.type.MimeAwareType;
import be.kwakeroni.ghast.convert.type.NativeAwareType;
import be.kwakeroni.ghast.convert.type.TextualType;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Forms {

    private Forms() {
        throw new UnsupportedOperationException("Instantiation not allowed");
    }

    public static <Type extends MimeAwareType & NativeAwareType>
    Function<Content<Type, byte[]>, ByteArrayContent<Type>> clipboard() {
        return Clipboard.asDeclaredType();
    }

    public static class Clipboard {

        public static <Type extends TextualType> Function<Content<Type, byte[]>, ByteArrayContent<Type>> asText() {
            return ContentMappers.mappingForm((type, bytes) -> {
                String string = type.toString(bytes);
                System.out.println("Writing to clipboard: " + string);
                copyToClipboard(new StringSelection(string));
                return bytes;
            }, ByteArrayContent::of);

        }

        public static <Type extends MimeAwareType & NativeAwareType>
        Function<Content<Type, byte[]>, ByteArrayContent<Type>> as(Type type) {
            return ContentMappers.mappingForm(bytes -> {
                System.out.println("Writing to clipboard: " + bytes.length + " bytes as " + type.getMimeType());
                copyToClipboard(getBinaryTransferable(type, type, bytes));
                return bytes;
            }, ByteArrayContent::of);

        }

        public static <Type extends MimeAwareType & NativeAwareType>
        Function<Content<Type, byte[]>, ByteArrayContent<Type>> asDeclaredType() {
            return ContentMappers.mappingForm((type, bytes) -> {
                System.out.println("Writing to clipboard: " + bytes.length + " bytes as " + type.getMimeType());
                copyToClipboard(getBinaryTransferable(type, type, bytes));
                return bytes;
            }, ByteArrayContent::of);

        }
    }
//
//
//    private static class ClipboardOutputStream extends ByteArrayOutputStream {
//        private final MimeAwareType mimeType;
//        private final NativeAwareType nativeType;
//
//        private ClipboardOutputStream(MimeAwareType mimeType, NativeAwareType nativeType) {
//            this.mimeType = mimeType;
//            this.nativeType = nativeType;
//        }
//
//        @Override
//        public void close() throws IOException {
//            copyToClipboard(super::close, getBinaryTransferable(mimeType, nativeType, this.toByteArray()));
//        }
//    }

    private static void copyToClipboard(Transferable contents) {
        try {
            copyToClipboard(() -> {
            }, contents);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void copyToClipboard(Closeable closeable, Transferable contents) throws IOException {
        AtomicBoolean running = new AtomicBoolean(true);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, (cp, ct) -> running.set(false));
        closeable.close();

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            System.out.println("Written to clipboard.");
        } else {
            System.out.println("Written to clipboard. Waiting...");
            while (running.get()) {

            }
        }
    }

    private static void registerFormatToNative(DataFlavor flavor, NativeAwareType type) {
        SystemFlavorMap flavorMap = ((SystemFlavorMap) SystemFlavorMap.getDefaultFlavorMap());
        String nativeType = type.getNativeTypeId();
//        flavorMap.addUnencodedNativeForFlavor(format.getDataFlavor(), nativeType);
        if (flavorMap.getNativesForFlavor(flavor).stream().noneMatch(nat -> nat.startsWith(nativeType))) {
            flavorMap.addUnencodedNativeForFlavor(flavor, nativeType);
        }
    }

    private static Transferable getBinaryTransferable(MimeAwareType mimeType, NativeAwareType nativeType, byte[] bytes) {
        DataFlavor dataFlavor = getDataFlavor(mimeType, nativeType);
        return new BinaryTransferable(dataFlavor, bytes);
    }

    private static DataFlavor getDataFlavor(MimeAwareType mimeType, NativeAwareType nativeType) {
        DataFlavor flavor = new DataFlavor(mimeType.getMimeType(), mimeType.getHumanPresentableName());
        if (nativeType != null) {
            registerFormatToNative(flavor, nativeType);
        }
        return flavor;
    }

    private static class BinaryTransferable implements Transferable {
        private final DataFlavor flavor;
        private final Supplier<InputStream> supplier;

        public BinaryTransferable(DataFlavor flavor, byte[] data) {
            this(flavor, () -> new ByteArrayInputStream(data));
        }

        public BinaryTransferable(DataFlavor flavor, Supplier<InputStream> supplier) {
            this.flavor = flavor;
            this.supplier = supplier;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{flavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            System.out.println("Is Supported? " + flavor);
            return this.flavor.equals(flavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (this.flavor.equals(flavor)) {
                return supplier.get();
            }
            throw new UnsupportedFlavorException(flavor);
        }
// DISABLED for now, MacOS X does not pick up PDF and or PNG
//      addType(result, "PDF",         "application/pdf",
//                      "Portable Document Format",
//                      "org.freehep.graphicsio.pdf.PDFExportFileType");
//      addType(result, "PNG",         "application/pdf",
//                      "Portable Network Graphics",
//                      "org.freehep.graphicsio.png.PNGExportFileType");

    }
}
