package cis463.util;

import java.io.*;

/**
 * simple line-buffered character I/O wrapper for readers
 * that implements the StreamReader<Character> interface
 */

public class LineIO implements StreamReader<Character> {

    public static final int EOF = -1;
    public static final int NL = '\n';
    public BufferedReader rdr;
    public LineIOWriter out;
    public String line;
    public int lno;
    public int len;
    public int pos;

    /**
     * Constructs a LineIO object based on the given BufferedReader.
     *
     * @param rdr   a BufferedReader
     */
    public LineIO(BufferedReader rdr, LineIOWriter out) {
	this.rdr = rdr;
	this.out = out;
	line = null;
	lno = 0;
	len = -1; // force the first getch to read a new line
	pos = 0;
    }

    public LineIO(BufferedReader rdr) {
	this(rdr, null);
    }

    /**
     * Constructs a LineIO object based on the given Reader, by
     * wrap it in a BufferedReader.
     *
     * @param rdr   a Reader object
     */
    public LineIO(Reader rdr, LineIOWriter out) {
	this(new BufferedReader(rdr), out);
    }

    public LineIO(Reader rdr) {
	this(rdr, null);
    }

    /**
     * Constructs a LineIO object with input from System.in
     */
    public LineIO(LineIOWriter out) {
	this(new InputStreamReader(System.in), out);
    }

    public LineIO() {
	this(new InputStreamReader(System.in), null);
    }

    /**
     * Returns an int representing the next character in the input stream.
     * A return value of -1 means end-of-file.
     *
     * @return   the next input character, or -1 if at end of input
     */
    public int getch() {
	if (pos > len) {
	    // need to get a new line
	    try {
		line = rdr.readLine();
	    } catch (IOException e) {
		throw new RuntimeException(e.toString());
	    }
	    if (line == null)
		return EOF;
	    // we have a real line now
	    pos = 0;
	    lno++;
	    len = line.length();
	    if (out != null)
		out.writeLine(lno, line);
	}
	// assert: pos <= len
	if (pos < len)
	    return line.charAt(pos++);
	// readline doesn't save newlines, but we want getch to return a
	// newline as if the line string were terminated with '\n'
	// assert: pos == len
	pos++;
	return NL;
    }

    /**
     * Returns the next item in the input stream as a Character, or null
     * upon end-of-input.
     *
     * @return    the next Character in the input
     */
    public Character read() {
	int c = getch();
	if (c == EOF)
	    return null;
	return new Character((char)c);
    }

    /**
     * Returns the current line in the input as a String, not
     * including the trailing newline.  Before reading any characters,
     * or upon reaching end-of-input, null will be returned.  Upon a call
     * to getch(), a new line is obtained from the input if this
     * is the first call to getch(), or if the previous call to getch()
     * returned a NL character, otherwise the returned value will be
     * unchanged.
     *
     * @return   The current line in the Reader, without a trailing newline
     */
    public String getLine() {
	return line;
    }

    /**
     * Returns the current line number in the input, starting at line number 1.
     * Before reading any characters, a line number of zero will be reported.
     * Once end-of-file is reached, the return value will be the line number of
     * the last line of input.
     *
     * @return   The current line number in the file
     */
    public int getLineNumber() {
	return lno;
    }

    /**
     * Sets the current line number to the given parameter, returning the
     * previous line number.
     *
     * @param  lno   the line number to set
     * @return       the previous line number
     */
    public int setLineNumber(int lno) {
	int save = lno;
	this.lno = lno;
	return save;
    }
}
