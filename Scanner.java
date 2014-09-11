import cis463.util.*;

public class Scanner {

    public static void main(String [] args) {
        LineIO lio = new LineIO(); // get input from stdin
        TokenReader tio = new TokenReader(lio);
        Lazy<Token> lzytok = new Lazy<Token>(tio);
        while(true) {
            Token tok = lzytok.cur();
            System.out.printf("%6d: %9s \"%s\"\n", tok.lno, tok.val, tok.str);
            if (tok.val == Token.Val.EOF)
                break;
            lzytok.adv();
        }
    }
}
