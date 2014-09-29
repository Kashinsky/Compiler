/* Name: Dillon Yeh
 * Date: 9/23/2014
 * Assignment: Scanner
*/

import cis463.util.*;
import cis463.fsm.*;
import java.util.*;

/*Program: TokenReader.java
 *Overview: This program is used to simulate a finite state machine and 
 *          to process a character stream into tokens
 */
public class TokenReader implements StreamReader<Token> {

    private LineIO lio;              // a StreamReader<Character>
    private Lazy<Character> lzin;   // a Lazy<Character> based on lio
    private static final char NL = '\n';    // the newline character
    private Map<String,Token.Val> rwtab; // Map containing reserved word keys and their
                                         // Token values
    public TokenReader(LineIO lio) {
	    this.lio = lio;
	    lzin = new Lazy<Character>(lio);
        buildMap();
   }

    // builds the reserved word map rwtab
    // by iterating through the vals from AND to WITH
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
    
    // puts passed character onto the stack
    public void PUT(Character ch) {
        lzin.put(ch);
    }
    // the FSM states

    // Initial state
    private FSMState s_INIT = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            //if we have reached the end of file
            if (ch == null) {
                tok.str.append("*EOF*");
                VAL(Token.Val.EOF);
                return null;
            }

            // if ch is a single nonletter character
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
            // either ch is whitespace or ch represents an
            // unrecognized character
            if (Character.isWhitespace(ch)) {
                adv();
                return this;
            } else {
                AAV(ch,Token.Val.ERROR);
                return null;
            }
        }
    };

    // state that checks for '=' and '>'
    // if ch is '=' then characters in str represent the LE Token
    // if ch is '>' then characters in str represent the NE Token
    // else the previous '<' represents the less than Token
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

    // state that checks if '=' is the current character
    // if it is, the characters in str represent the GE Token
    // else the previous '>' represents the GT Token
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
    
    // state that checks if '.' is the current character
    // if it is, the characters in str represent the DOTDOT Token
    // else the previous '.' represents the DOT Token
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

    // state that checks if '=' is the current character
    // if it is, the characters in str represent the ASSIGN Token
    // else the previous ':' represents the COLON Token
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
    
    // state that compares the characters in str to the
    // reserved words map rwtab.
    // if when all the letters, digits and underscores are collected and
    //                          the rwtab.get method returns null, the 
    //                          string is an ID Token.
    // else it is the Token returned from rwtab.get.
    private FSMState s_ID = new FSMState() {
        public FSMState next() {
            Character ch = cur();
            tok.lno = lno();
            if(Character.isLetter(ch)||Character.isDigit(ch)||ch=='_') {
                AA(ch);
                return this;
            }
            // get the String that str represents
            // convert to uppercase, then attempt to get
            // a Token from rwtab
            String id = (tok.str.toString()).toUpperCase();
            Token.Val rwval = rwtab.get(id);
            if(rwval == null) {
                // String is not in rwtab
                VAL(Token.Val.ID);
                return null;
            }
            // String was found in rwtab and the Token.Val was returned
            VAL(rwval);
            tok.str = new StringBuffer(id);
            return null;
        }
    };

    // state that adds characters to str until either
    // if '\'' is the current character, return the QQ state.
    // if ch is a printable character, it is appended to str
    // else str represents an ESTRING Token
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
        //Parameters: Character ch, the character to be compared against printable characters
        //Returns: true, if ch is printable
        //         false, if ch is not a printable character
        private boolean isPrint(Character ch) {
            return (ch != null && ' ' <= ch && ch <='~');
        }
    };

    // state that checks if ch another '\''
    // if it is, the string has not been completed and 
    //           s_STRING is returned to continue processing
    // else, str represents the STRING Token
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

    // state that checks if ch is a digit
    // if ch is a digit, it is appended and the next character is checked.
    // if ch is '.', then str is potentially a real and s_posReal is returned.
    // else str is an INT Token.
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

    // state that checks if ch is a digit.
    // if it is, str is a REAL Token and a '.' is appended to str before ch.
    //           then s_REAL is returned.
    // else str is an INT Token and '.' is put back onto the StreamReader.
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

    // state that checks if ch is a digit.
    // if it is, ch is appended to str then this is returned
    // else, nothing is appended or advanced and str is a REAL Token
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

    // state appends ch to str unless,
    // ch is a newline, then return s_NACOMMENT
    // or ch is '}', then str is a COMMENT Token
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

    // state advances past any character ch unless,
    // ch == null, then str is an ECOMMENT Token
    // or ch is '}', then str is a COMMENT Token
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
