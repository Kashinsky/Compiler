import cis463.util.*;
import cis463.fsm.*;
import java.util.*;

/**
 * a skeleton TokenReader for Pascal tokens --
 * the only tokens returned are ERROR (for alphabetic characters)
 * and EOF
 */
public class TokenReader implements StreamReader<Token> {

    private LineIO lio;              // a StreamReader<Character>
    private Lazy<Character> lzin;   // a Lazy<Character> based on lio
    private static final char NL = '\n';    // the newline character

    public TokenReader(LineIO lio) {
	this.lio = lio;
	lzin = new Lazy<Character>(lio);
    }

    public Character cur() {
        return lzin.cur();
    }

    public void adv() {
        lzin.adv();
    }

    public int lno() {
        return lio.getLineNumber();
    }

    // this variable is referenced in each FSMState method
    private Token tok;

    // Appends the character ch to the current token buffer
    // and sets the token type to Val
    public void AV(Character ch, Token.Val val) {
        tok.str.append(ch);
        tok.val = val;
    }

    // Appends the character ch to the current token buffer
    // and advances to the next character
    public void AA(Character ch) {
        tok.str.append(ch);
        adv();
    }

    // Advance, Append, and set Value
    public void AAV(Character ch, Token.Val val) {
        adv();
        tok.str.append(ch);
        tok.val = val;
    }

    // APPENDs the character ch to the current token buffer
    public void APPEND(Character ch) {
        tok.str.append(ch);
    }

    // sets the VALue of this token to v
    public void VAL(Token.Val v) {
        tok.val = v;
    }

    // the FSM states
    private FSMState s_INIT = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            if (ch == null) {
                // end of file
                tok.str.append("*EOF*");
                VAL(Token.Val.EOF);
                return null;
            }
            switch(ch) {
                case '+': AAV(ch, Token.Val.PLUS); return null;
                case '-': AAV(ch, Token.Val.MINUS); return null;
                case '*': AAV(ch, Token.Val.TIMES); return null;
                case '/': AAV(ch, Token.Val.DIVIDE); return null;
                case '=': AAV(ch, Token.Val.EQU); return null;
                case '(': AAV(ch, Token.Val.LPAREN); return null;
                case ')': AAV(ch, Token.Val.RPAREN); return null;
                case '[': AAV(ch, Token.Val.LBRACK); return null;
                case ']': AAV(ch, Token.Val.RBRACK); return null;
                case ',': AAV(ch, Token.Val.COMMA); return null;
                case ';': AAV(ch, Token.Val.SEMI); return null;
                case '^': AAV(ch, Token.Val.UPARROW); return null;
                case '<': AA(ch); return s_LT;
                case '>': AA(ch); return s_GT;
                case '.': AA(ch); return s_DOT;
                case ':': AA(ch); return s_COLON;
            }
            if (Character.isLetter(ch)) {
                AAV(ch, Token.Val.ERROR);
                return  null;
            }
            // ignore anything else
            adv();
            return this; // ... could loop instead ...
        }
    };

    private FSMState s_LT = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno(); 
            switch(ch) {
                case '=': AAV(ch, Token.Val.LE); return null;
                case '>': AAV(ch, Token.Val.NE); return null;
                default: VAL(Token.Val.LT);return null;
            }
        }

    };

    private FSMState s_GT = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            switch(ch) {
                case '=': AAV(ch,Token.Val.GE); return null;
                default: VAL(Token.Val.GT); return null;
            }
        }

    };

    private FSMState s_DOT = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            switch(ch) {
                case '.': AAV(ch, Token.Val.DOTDOT); return null;
                default: VAL(Token.Val.DOT); return null;

            }
        }

    };

    private FSMState s_COLON = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            switch(ch) {
                case '=': AAV(ch, Token.Val.ASSIGN); return null;
                default: VAL(Token.Val.COLON); return null;
            }
        }

    };

//     private FSMState

    public Token read() {
        tok = new Token(); // get a new Token object
        FSM.run(s_INIT); // initialize and run the FSM
        return tok;
    }
}
