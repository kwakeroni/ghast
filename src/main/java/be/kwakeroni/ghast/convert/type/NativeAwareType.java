package be.kwakeroni.ghast.convert.type;

/**
 * Represents a content type that is aware of its OS-native representation.
 */
public interface NativeAwareType {

    /**
     * @return The identifier of this content type applicable to the current OS.
     */
    public String getNativeTypeId();
}
