import java.io.*;

/*
 * Builds a StreamReader<CharLN> from a character-oriented input stream
 * or from a String
 */

public class CharIn implements StreamReader<CharLN> {

    public LineNumberReader rdr;

    public CharIn(File f) {
	rdr = null;
	try {
	    rdr = new LineNumberReader(new FileReader(f));
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public CharIn(String s) {
	rdr = null;
	try {
	    rdr = new LineNumberReader(new StringReader(s));
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    public CharIn() {
	rdr = new LineNumberReader(new InputStreamReader(System.in));
    }

    public CharLN read() {
	int c = -1;
	int lno;
	try {
	    lno = rdr.getLineNumber()+1;
	    c = rdr.read();
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	if (c < 0)
	    return null;
	return new CharLN((char)c, lno);
    }

    public static void displayStream(CharIn cin) {
	int lno = 0;
	while (true) {
	    CharLN cln = cin.read();
	    if (cln == null)
		break;
	    if (cln.lno > lno) {
		lno = cln.lno;
		System.out.printf("%6d: ", lno);
	    }
	    System.out.print(cln.ch);
	}
	System.out.println();
    }	

    public static void main(String [] args) {
	if (args.length != 2)
	    throw new RuntimeException("usage: java CharIn <string1> <string2>");
	CharIn cin;

	System.out.println("string:");
	displayStream(new CharIn(args[0]));

	System.out.println("contents of file " + args[1] + ":");
	displayStream(new CharIn(new File(args[1])));

	System.out.println("enter input from keyboard [ctrl-D to finish]:");
	displayStream(new CharIn());
    }

}
