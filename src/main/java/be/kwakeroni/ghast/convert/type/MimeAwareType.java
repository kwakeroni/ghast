package be.kwakeroni.ghast.convert.type;

/**
 * Represents a content type that can provide information about the Mime Type it represents.
 */
public interface MimeAwareType {

    /**
     * @return The Mime Type represented by this content type.
     */
    public String getMimeType();

    /**
     * @return The name of this content type in human-readable form.
     */
    public String getHumanPresentableName();

}
