public class LZLineIO {

    // test the Lazy and LineIO interactions
    public static void main(String [] args) {
	LineIO lio = new LineIO(); // get input from standard input
	Lazy<Character> lzlio = new Lazy<Character>(lio);
	int lno = 0;
	while (true) {
	    Character ch = lzlio.cur();
	    int nlno = lio.lineNumber();
	    if (ch == null)
		break;
	    if (nlno > lno) {
		lno = nlno;
		System.out.printf("%d: ", lno);
	    } 
	    System.out.print(ch);
	    lzlio.adv();
	}
    }
}
