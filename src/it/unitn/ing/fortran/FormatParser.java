package it.unitn.ing.fortran;

import it.unitn.ing.rista.util.Misc;

/* Generated By:JavaCC: Do not edit this line. FormatParser.java */

class FormatParser implements FormatParserConstants {

// A string literal inside a format. We haven't implemented
// embedded quotes yet.
  static final public int Integer() throws ParseException {
    Token t;
    t = jj_consume_token(INTEGER);
    {
      if (true) return (Integer.valueOf(t.image)).intValue();
    }
    throw new Error("Missing return statement in function");
  }

  static final public FormatElement FormatIOElement() throws ParseException {
    FormatElement fe;
    int w, d;
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case 4:
        jj_consume_token(4);
        w = Integer();
        fe = new FormatA(w);
        break;
      case 5:
        jj_consume_token(5);
        w = Integer();
        fe = new FormatI(w);
        break;
      case 6:
        jj_consume_token(6);
        w = Integer();
        jj_consume_token(7);
        d = Integer();
        fe = new FormatF(w, d);
        break;
      case 8:
        jj_consume_token(8);
        w = Integer();
        jj_consume_token(7);
        d = Integer();
        fe = new FormatE(w, d);
        break;
      default:
        jj_la1[0] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
    }
    {
      if (true) return fe;
    }
    throw new Error("Missing return statement in function");
  }

// This represents a format element that transfers one
// data item.
  static final public FormatElement FormatNonIOElement() throws ParseException {
    jj_consume_token(9);
    {
      if (true) return new FormatX();
    }
    throw new Error("Missing return statement in function");
  }

// This represents a format element that doesn't transfer
// any data items.
  static final public FormatElement FormatElement() throws ParseException {
    FormatElement fe;
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case 4:
      case 5:
      case 6:
      case 8:
        fe = FormatIOElement();
        break;
      case 9:
        fe = FormatNonIOElement();
        break;
      default:
        jj_la1[1] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
    }
    {
      if (true) return fe;
    }
    throw new Error("Missing return statement in function");
  }

  static final public FormatSlash FormatSlash() throws ParseException {
    jj_consume_token(10);
    {
      if (true) return new FormatSlash();
    }
    throw new Error("Missing return statement in function");
  }

// These are a special case. Unlike other format elements,
// Fortran permits several slashes to be concatenated without
// commas to separate them, and you can't use a repetition
// factor on them.
  static final public FormatString FormatString() throws ParseException {
    Token t;
    String s;
    t = jj_consume_token(STRING);
    s = t.image;
    s = s.substring(1, s.length() - 1); // Remove the quotes.
    {
      if (true) return new FormatString(s);
    }
    throw new Error("Missing return statement in function");
  }

// Another special case that can't be repeated, and can be
// concatenated to other elements without commas.
  static final public void OptionalFormatSlashesOrStrings(Format f) throws ParseException {
    FormatUniv fs;
    label_1:
    while (true) {
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case STRING:
        case 10:
          ;
          break;
        default:
          jj_la1[2] = jj_gen;
          break label_1;
      }
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case 10:
          fs = FormatSlash();
          break;
        case STRING:
          fs = FormatString();
          break;
        default:
          jj_la1[3] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
      }
      f.addElement(fs);
    }
  }

  static final public FormatRepeatedItem FormatRepeatedItem() throws ParseException {
    int r = 1;
    FormatUniv fu;
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case INTEGER:
        r = Integer();
        break;
      default:
        jj_la1[4] = jj_gen;
        ;
    }
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case 11:
        jj_consume_token(11);
        fu = Format();
        jj_consume_token(12);
        break;
      case 4:
      case 5:
      case 6:
      case 8:
      case 9:
        fu = FormatElement();
        break;
      default:
        jj_la1[5] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
    }
    {
      if (true) return new FormatRepeatedItem(r, fu);
    }
    throw new Error("Missing return statement in function");
  }

  static final public void FormatGroup(Format f) throws ParseException {
    FormatRepeatedItem fri;
    OptionalFormatSlashesOrStrings(f);
    switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
      case INTEGER:
      case 4:
      case 5:
      case 6:
      case 8:
      case 9:
      case 11:
        fri = FormatRepeatedItem();
        f.addElement(fri);
        OptionalFormatSlashesOrStrings(f);
        break;
      default:
        jj_la1[6] = jj_gen;
        ;
    }
  }

// This rather messy syntax allows us to have slashes and/or
// strings either side of a format element or repeated group
// without needing to separate them from each other or the element
// with commas.
// It also means that we can have empty format groups and format
// groups that don't transfer any data elements. So for example,
// the format ,/, is valid under this grammar.
  static final public Format Format() throws ParseException {
    FormatRepeatedItem fri;
    Format f = new Format();
    FormatGroup(f);
    label_2:
    while (true) {
      switch ((jj_ntk == -1) ? jj_ntk() : jj_ntk) {
        case 13:
          ;
          break;
        default:
          jj_la1[7] = jj_gen;
          break label_2;
      }
      jj_consume_token(13);
      FormatGroup(f);
    }
    {
      if (true) return f;
    }
    throw new Error("Missing return statement in function");
  }

  static private boolean jj_initialized_once = false;
  static public FormatParserTokenManager token_source;
  static ASCII_CharStream jj_input_stream;
  static public Token token, jj_nt;
  static private int jj_ntk;
  static private int jj_gen;
  static final private int[] jj_la1 = new int[8];
  static final private int[] jj_la1_0 = {0x170, 0x370, 0x408, 0x408, 0x4, 0xb70, 0xb74, 0x2000, };

  public FormatParser(java.io.InputStream stream) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    jj_input_stream = new ASCII_CharStream(stream, 1, 1);
    token_source = new FormatParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  static public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  public FormatParser(FormatParserTokenManager tm) {
    if (jj_initialized_once) {
      System.out.println("ERROR: Second call to constructor of static parser.  You must");
      System.out.println("       either use ReInit() or set the JavaCC option STATIC to false");
      System.out.println("       during parser generation.");
      throw new Error();
    }
    jj_initialized_once = true;
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  public void ReInit(FormatParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 8; i++) jj_la1[i] = -1;
  }

  static final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null)
      token = token.next;
    else
      token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static final public Token getNextToken() {
    if (token.next != null)
      token = token.next;
    else
      token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  static final public Token getToken(int index) {
    Token t = token;
    for (int i = 0; i < index; i++) {
      if (t.next != null)
        t = t.next;
      else
        t = t.next = token_source.getNextToken();
    }
    return t;
  }

  static final private int jj_ntk() {
    if ((jj_nt = token.next) == null)
      return (jj_ntk = (token.next = token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  static private java.util.Vector jj_expentries = new java.util.Vector();
  static private int[] jj_expentry;
  static private int jj_kind = -1;

  static final public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[14];
    for (int i = 0; i < 14; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 8; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1 << j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 14; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[]) jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  static final public void enable_tracing() {
  }

  static final public void disable_tracing() {
  }

}