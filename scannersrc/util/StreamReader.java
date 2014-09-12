package cis463.util;

/**
 * The StreamReader interface defines a single method, read(), that
 * returns the next item in the input stream.
 */
public interface StreamReader<E> {

    /**
     * Returns the next item of type E in the input stream, or null if the
     * end of the stream has been reached.
     *
     * @return    the next item in the input stream, or null if end of input
     */ 
    public E read();   // returns the next item of type E in the stream

}
