package be.kwakeroni.ghast.convert.image;

import be.kwakeroni.ghast.convert.type.MimeAwareType;
import be.kwakeroni.ghast.convert.type.NativeAwareType;

public final class PNG implements MimeAwareType, NativeAwareType {

    public static final PNG type = new PNG();

    private PNG() {

    }

    @Override
    public String getMimeType() {
        return "image/png";
    }

    @Override
    public String getHumanPresentableName() {
        return "PNG";
    }

    @Override
    public String getNativeTypeId() {
        return "BITMAP";
    }
}
