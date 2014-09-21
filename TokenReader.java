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
    private Map<String,Token.Val> rwtab;
    public TokenReader(LineIO lio) {
	    this.lio = lio;
	    lzin = new Lazy<Character>(lio);
        buildMap();
   }

    private void buildMap() {
        this.rwtab = new HashMap<String, Token.Val>();
        for(Token.Val val : EnumSet.range(Token.Val.AND, Token.Val.WITH)) {
            rwtab.put(val.toString(),val);
        }
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

    public void PUT(Character ch) {
        lzin.put(ch);
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
                case '\'': adv(); return s_STRING;
                case '{': tok.lno = lno(); adv(); return s_COMMENT;
            }
            if (Character.isLetter(ch)) {
                AA(ch);
                return  s_ID;
            }
            if (Character.isDigit(ch)) {
                AA(ch);
                return s_INT;
            }
            if (Character.isWhitespace(ch)) {
                adv();
                return this;
            } else {
                AAV(ch,Token.Val.ERROR);
                return null;
            }
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
    
    private FSMState s_ID = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            if(Character.isLetter(ch)||Character.isDigit(ch)||ch=='_') {
                AA(ch);
                return this;
            }
            String id = (tok.str.toString()).toUpperCase();
            Token.Val rwval = rwtab.get(id);
            if(rwval == null) {
                VAL(Token.Val.ID);
                return null;
            }
            VAL(rwval);
            tok.str = new StringBuffer(id);
            return null;
        }
    };

    private FSMState s_STRING = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            if(ch == '\'') {
                adv();
                return s_QQ;
            }
            if(isPrint(ch)) {
                AA(ch);
                return this;
            }
            AAV(ch, Token.Val.ESTRING);
            return null;
        }

        private boolean isPrint(Character ch) {
            return (ch != null && ' ' <= ch && ch <='~');
        }
    };

    private FSMState s_QQ = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            if(ch == '\'') {
                AA(ch);
                return s_STRING;
            }
            VAL(Token.Val.STRING);
            return null;
        }

    };

    private FSMState s_INT = new FSMState () {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            if(Character.isDigit(ch)) {
                AA(ch);
                return this;
            }
            if(ch == '.') {
                adv();
                return s_posReal;
            }
            VAL(Token.Val.INT);
            return null;
        }
    };

    private FSMState s_posReal = new FSMState () {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            if(Character.isDigit(ch)) {
                APPEND('.');
                AA(ch);
                return s_REAL;
            }
            VAL(Token.Val.INT);
            PUT('.');
            return null;
        }
    };

    private FSMState s_REAL = new FSMState () {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            if(Character.isDigit(ch)) {
                AA(ch);
                return this;
            }
            VAL(Token.Val.REAL);
            return null;
        }
    };

    private FSMState s_COMMENT = new FSMState () {
        public FSMState next() {
            Character ch = cur();
            switch(ch) {
                case NL: adv(); return s_NACOMMENT;
                case '}': adv(); VAL(Token.Val.COMMENT); return null;
            }
            if(ch == null) {
                VAL(Token.Val.ECOMMENT);
                return null;
            }
            AA(ch);
            return this;
        }
    };

    private FSMState s_NACOMMENT = new FSMState () {
        public FSMState next() {
            Character ch = cur();
            if(ch == null) {
                VAL(Token.Val.ECOMMENT);
                return null;
            }
            if(ch == '}') {
                adv();
                VAL(Token.Val.COMMENT);
                return null;
            }
            adv();
            return this;
        }
    };
    

//     private FSMState

    public Token read() {
        tok = new Token(); // get a new Token object
        FSM.run(s_INIT); // initialize and run the FSM
        return tok;
    }
}
