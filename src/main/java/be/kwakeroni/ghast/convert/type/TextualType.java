package be.kwakeroni.ghast.convert.type;

/**
 * Represents a content type that is readable and can be converted to a String.
 */
public interface TextualType {

    /**
     * Converts the given content bytes to a String.
     * @param bytes The content in the form of a byte array.
     * @return The content in the form of a String.
     */
    public String toString(byte[] bytes);

}
