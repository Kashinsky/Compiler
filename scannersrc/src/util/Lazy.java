package cis463.util;

import java.util.*;

/**
 * Lazy input on a StreamReader
 */

public class Lazy<E> {

    private boolean eof;
    private StreamReader<E> rdr;           // the StreamReader to make lazy
    private Stack<E> stk = new Stack<E>(); // pushback stack

    public Lazy(StreamReader<E> rdr) {
	this.rdr = rdr;
	this.eof = false;
    }

    /**
     * Returns the current item of type E from the StreamReader, or null if
     * the StreamReader is at end of input.
     *
     * @return     an item of type E
     */
    public E cur() {
	if (stk.size() > 0)
	    return stk.peek();
	else if (eof)
	    return null;
	else {
	    E e = rdr.read();
	    if (e == null) {
		eof = true;
	    } else {
		stk.push(e);
	    }
	    return e;
	}
    }

    /**
     * Advance to the next item in the StreamReader, if there is one
     */
    public void adv() {
	if (stk.size() > 0)
	    stk.pop();
	else if (!eof) {
	    E e = rdr.read();
	    if (e == null)
		eof = true;
	}
    }

    /**
     * Put an item of type E into the input stream, to be read by the
     * next invocation of chr().  Sets eof on the lazy input to false.
     *
     * @param  e   an item of type E to put back into the input stream
     */
    public void put(E e) {
	stk.push(e);
	eof = false;
    }

}
