package be.kwakeroni.ghast.convert.image;

import be.kwakeroni.ghast.convert.type.MimeAwareType;
import be.kwakeroni.ghast.convert.type.NativeAwareType;

public final class EMF implements MimeAwareType, NativeAwareType {

    public static final EMF type = new EMF();

    private EMF() {

    }

    @Override
    public String getMimeType() {
        return "image/emf";
    }

    @Override
    public String getHumanPresentableName() {
        return "Enhanced Meta File";
    }

    @Override
    public String getNativeTypeId() {
        return "ENHMETAFILE";
    }

}
