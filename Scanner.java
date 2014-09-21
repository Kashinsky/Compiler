import cis463.util.*;
import java.util.*;

public class Scanner {
    public static Map<String,Stack<Integer>> idCRT = new TreeMap<String,Stack<Integer>> ();
    public static void main(String [] args) {
        LineIO lio = new LineIO(); // get input from stdin
        TokenReader tio = new TokenReader(lio);
        Lazy<Token> lzytok = new Lazy<Token>(tio);
        while(true) {
            Token tok = lzytok.cur();
            System.out.printf("%6d: %9s \"%s\"\n", tok.lno, tok.val, tok.str);
            if (tok.val == Token.Val.ID) {
                Stack<Integer> temp = idCRT.get(tok.str.toString());
                if(temp != null) {
                    int num = tok.lno;
                    if(tok.lno != temp.peek())
                        temp.push(tok.lno);
                } else {
                    temp = new Stack<Integer>();
                    temp.push(tok.lno);
                    idCRT.put(tok.str.toString(), temp);
                }

            }
            if (tok.val == Token.Val.EOF) {
                if(!idCRT.isEmpty()) {
                    printIDTable();
                }
                break;
            }
            lzytok.adv();
        }
    }

    public static void printIDTable () {
        System.out.println(); 
        System.out.println("Identifier Cross Reference Table");
        System.out.println("--------------------------------");  
        for(String key : idCRT.keySet()) {
            if(key.length() > 30) {
                String temp = key.substring(0, 30);
                temp += "+";
                System.out.printf("%-30s",temp); 
            } else {
                System.out.printf("%-30s",key);
            }
            int count = 0;
            for(Integer lno : idCRT.get(key)) {
                if(count == 7) {
                    System.out.printf("\n%30s", " ");
                    count = 0;
                }
                System.out.printf("%6d",lno);
                count++;
            }
            System.out.println();
        }
    }
}
