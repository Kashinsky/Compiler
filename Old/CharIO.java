import java.io.*;

/*
 * Lazy character I/O based on a LineNumberReader object
 */

public class CharIO {

    public static int EOF = -1;
    public LineNumberReader rdr;
    public int pos = -1; // the position in the 
    public static int BUFSZ = 10;
    public int bufsz = BUFSZ;
    public boolean eof = false;
    public char[] charbuf = new char[BUFSZ];
    
    public CharIO(String fileName) {
	rdr = null;
	try {
	    rdr = new LineNumberReader(new FileReader(fileName));
	} catch (Exception e) {
	    throw new RuntimeException(e.toString());
        }
    }

    public CharIO() {
	rdr = new LineNumberReader(new InputStreamReader(System.in));
    }

    public int ch() {
	if (pos >= 0)
	    return charbuf[pos];
	else if (eof)
	    return EOF;
	else {
	    int c = rawch();
	    if (c < 0) {
		eof = true;
		return EOF;
	    } else {
		charbuf[++pos] = (char)c;
		return c;
	    }
	}
    }

    public int rawch() {
	int c = -1;
	try {
	    c = rdr.read();
	} catch (IOException e) {
	    throw new RuntimeException(e.toString());
	}
	return c;
    }

    public void advch() {
	if (pos >= 0)
	    pos--;
	else if (!eof) {
	    int c = rawch();
	    if (c < 0)
		eof = true;
	}
    }

    public void putch(int c) {
	pos++;
	if (pos >= bufsz) {
	    int newbufsz = 2*bufsz;
	    char [] newbuf = new char[newbufsz];
	    for (int i=0 ; i<bufsz ; i++)
		newbuf[i] = charbuf[i];
	    bufsz = newbufsz;
	    charbuf = newbuf;
	}
	charbuf[pos] = (char)c;
    }

    public boolean atEOF() {
	return eof;
    }

    public int lineNumber() {
	return rdr.getLineNumber();
    }

    public static void main(String [] args) {
	/*
	if (args.length != 1)
	    throw new RuntimeException("usage: java CharIO filename");
	CharIO cio = new CharIO(args[0]);
	*/
	CharIO cio = new CharIO();
	int c;
	int currLineNumber = -1;
	while (true) {
	    int lineNumber = cio.lineNumber();
	    if (lineNumber > currLineNumber) {
		System.out.printf("%6d: ", lineNumber+1);
		currLineNumber = lineNumber;
            }
	    c = cio.ch();
	    if (c == EOF)
		break;
	    System.out.print((char)c);
	    cio.advch();
	}
    }
}
