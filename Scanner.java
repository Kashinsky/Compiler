import cis463.util.*;
import java.util.*;

public class Scanner {
    // idCRT, a tree map of String keys and Stack<Integer> values 
    public static Map<String,Stack<Integer>> idCRT = new TreeMap<String,Stack<Integer>> ();
    public static void main(String [] args) {
        LineIO lio = new LineIO(); // get input from stdin
        TokenReader tio = new TokenReader(lio);
        Lazy<Token> lzytok = new Lazy<Token>(tio);
        while(true) {
            Token tok = lzytok.cur();
            System.out.printf("%6d: %9s \"%s\"\n", tok.lno, tok.val, tok.str);
            // adds tok.val to the correct stack if one exists,
            // else it will create a new Map entry and a new stack
            // to place tok.lno into
            if (tok.val == Token.Val.ID) {
                Stack<Integer> temp = idCRT.get(tok.str.toString());
                // tok.val is already in the Map
                // push tok.lno into existing stack
                if(temp != null) {
                    int num = tok.lno;
                    if(tok.lno != temp.peek())
                        temp.push(tok.lno);
                // tok.val is not already in the Map
                // create a new stack and push tok.lno onto it
                // put tok string representation and the new stack into the Map
                } else {
                    temp = new Stack<Integer>();
                    temp.push(tok.lno);
                    idCRT.put(tok.str.toString(), temp);
                }

            }
            if (tok.val == Token.Val.EOF) {
                // if id table is not empty, call printIDTable method
                if(!idCRT.isEmpty()) {
                    printIDTable();
                }
                break;
            }
            lzytok.adv();
        }
    }

    // prints the Cross Reference Table from the entries in Map idCRT
    public static void printIDTable () {
        System.out.println(); 
        System.out.println("Identifier Cross Reference Table");
        System.out.println("--------------------------------");  
        for(String key : idCRT.keySet()) {
            // if id string length is > 30,
            // replace excess characters with a '+' character
            if(key.length() > 30) {
                String temp = key.substring(0, 30);
                temp += "+";
                System.out.printf("%-30s",temp); 
            } else {
                System.out.printf("%-30s",key);
            }
            // prints the line numbers, creating a new line
            // if number of line numbers exceeds 7
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
