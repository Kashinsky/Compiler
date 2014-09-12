import cis463.util.LineIO;
import java.io.*;

public class LineIOTest {

    public static void main(String [] args) {
	if (args.length != 1)
	    throw new RuntimeException("usage: java LineIO filename");
	LineIO lio = null;
	try {
	    lio = new LineIO(new FileReader(args[0]));
	} catch (Exception e) {
	    throw new RuntimeException(e.toString());
	}
	Character ch;
	int currLineNumber = 0;
	while (true) {
	    ch = lio.read();
	    if (ch == null)
		break; // end of file
	    int lineNumber = lio.getLineNumber();
	    if (lineNumber > currLineNumber) {
		System.out.printf("%6d: ", lineNumber);
		currLineNumber = lineNumber;
            }
	    System.out.print(ch);
	}
    }
}
