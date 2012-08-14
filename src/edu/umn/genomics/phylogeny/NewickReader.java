package edu.umn.genomics.phylogeny;

import java.io.*;
import java.util.*;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import javax.swing.tree.*;
import edu.umn.genomics.table.ExceptionHandler;

class Debug {
  public static boolean isOn = false;
  //static { isOn= (System.getProperty("debug")!=null); }
  public static int val = 0;
  public final static int val() { return val; }
  public final static void setVal(int v) { val= v; isOn= (val!=0); }
  public final static void setState(boolean turnon) { isOn= turnon; }
  public final static void print(char c) { if (isOn) System.err.print(c); }
  public final static void print(String s) { if (isOn) System.err.print(s); }
  public final static void println(String s) { if (isOn) System.err.println(s); }
  public final static void println() { if (isOn) System.err.println(); }
}

public class NewickReader {  
  /**
  a.k.a. New Hampshire (NH) tree format reader
  # my relatives
  (Bovine:0.69395,(Gibbon:0.36079,(Orang:0.33636,(Gorilla[comment1]:0.17147,
  (Chimp:0.19268, Human:0.11927):0.08386):0.06124):0.15057)[comment2]:0.54939, 
  Mouse:1.21460);
  # more trees
  http://www.genetics.wustl.edu/eddy/forester/NHX.html
  An example of a (rooted) Tree in NHX:

  (((ADH2:0.1[&&NHX:S=human:E=1.1.1.1],ADH1:0.11[&&NHX:S=human:E=1.1.1.1]):0.05
    [&&NHX:S=Primates:E=1.1.1.1:D=Y:B=100],ADHY:0.1[&&NHX:S=nematode:E=1.1.1.1],
     ADHX:0.12[&&NHX:S=insect:E=1.1.1.1]):0.1[&&NHX:S=Metazoa:E=1.1.1.1:D=N],
     (ADH4:0.09[&&NHX:S=yeast:E=1.1.1.1],ADH3:0.13[&&NHX:S=yeast:E=1.1.1.1],
     ADH2:0.12[&&NHX:S=yeast:E=1.1.1.1],
     ADH1:0.11[&&NHX:S=yeast:E=1.1.1.1]):0.1[&&NHX:S=Fungi])[&&NHX:E=1.1.1.1:D=N]; 

  **/

  public StreamTokenizer st = null;
  public boolean fEatHTML;
  int depth = 0;
  public Vector trees = new Vector();
  
  public NewickReader() {
    this.fEatHTML= true; // if data is from URL !?
  }

  public List getTrees() {
    return trees;
  }

  public void readtokens(Reader reader) throws Exception {
    st= new StreamTokenizer(reader);
    mainTokenizer();
    do {
      DefaultMutableTreeNode root= null;
      Debug.println("NewickReader.readtokens new Phylotree");
      root= readTree();
      if (root != null) {
        trees.add(root);
      }
    } while (st.ttype != StreamTokenizer.TT_EOF); 
  }

  public DefaultMutableTreeNode readTree() throws Exception {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(new PhylogeNode());
    for (DefaultMutableTreeNode tn = readNode(); tn != null; tn = readNode()) {
      node.add(tn);
      if (st.ttype == ';' || st.ttype == StreamTokenizer.TT_EOF) 
        break;
    }
    return node.getChildCount() > 0 ? node : null; 
  }

  public void readComment()
  {
  } 

  public void mainTokenizer() {
    st.resetSyntax();
    st.wordChars('a', 'z');
    st.wordChars('A', 'Z');
    st.wordChars('_', '_');
    st.wordChars(128 + 32, 255);
    st.whitespaceChars(0, ' ');
    //st.commentChar('#');
    st.ordinaryChar('#');
    st.quoteChar('"');
    st.quoteChar('\'');
    //st.parseNumbers(); 
    st.wordChars('0', '9');
    st.wordChars('-','-'); // for number parsing!
    st.wordChars('.','.'); // for number parsing!
    
    st.ordinaryChar('[');
    st.ordinaryChar(']');
    st.ordinaryChar('(');
    st.ordinaryChar(')');
    st.ordinaryChar(',');
    st.ordinaryChar(';');
    st.eolIsSignificant(false);
  }

  public void dataCommentTokenizer() {
    // #comments such as
    // #name "my name"
    // #date 10-jun-96
    // # other comments...
    st.resetSyntax();
    st.wordChars('a', 'z');
    st.wordChars('A', 'Z');
    st.wordChars('_', '_');
    st.wordChars(128 + 32, 255);
    st.whitespaceChars(0, ' ');
    st.quoteChar('"');
    st.quoteChar('\'');
    st.eolIsSignificant(true);
  }
  
  public void nodeCommentTokenizer() {
    // comments are bracketed in []
    st.resetSyntax();
    st.whitespaceChars(0, ' '-1);
    st.wordChars(' ',127);
    st.wordChars(128 + 32, 255);
    //st.ordinaryChar('[');
    st.ordinaryChar(']');
  }
         
  String fDataComment, fDataLabel;

  public boolean readDataComment() throws Exception
  {
    fDataComment= null; 
    fDataLabel= null;
    dataCommentTokenizer();
    for (boolean more= true; more; ) 
      switch ( st.nextToken()) {
        case StreamTokenizer.TT_EOF: 
          st.pushBack();
          more= false;
          break;
        case StreamTokenizer.TT_EOL:
          more= false;
          break;
        case '"':
        case '\'':
        case StreamTokenizer.TT_WORD:
          if (fDataLabel==null) fDataLabel= st.sval;
          else fDataComment= st.sval;
          break;
       }
    if (Debug.val()>1) {
      Debug.print("#");
      if (fDataLabel!=null) Debug.print(fDataLabel);
      if (fDataComment!=null) Debug.print(" " + fDataComment);
      Debug.println();
      }
    mainTokenizer();
    return (fDataLabel!=null);
  }

  public String readNodeComment() throws Exception {
    String comment= null;
    nodeCommentTokenizer();
    for (boolean more= true; more; ) 
      switch ( st.nextToken()) {
        case StreamTokenizer.TT_EOF: 
          st.pushBack();
          more= false;
          break;
        case StreamTokenizer.TT_WORD:
          comment= st.sval;
          break;
        case /*[*/']':
          more= false;
          break;
       }
    mainTokenizer();
    return comment;
  }
  
  public DefaultMutableTreeNode readNode()  throws Exception {
    if (Debug.val()>2) Debug.println("\n>>>readNode" + ++depth);
    DefaultMutableTreeNode node = null;
    int  lasttype= 0;
    boolean closepar= false;
    for (boolean more= true; more; ) {
      int toknum=  st.nextToken();

      //if (toknum == StreamTokenizer.TT_WORD) 
        //toknum= checkForNum(toknum);
      if (toknum == StreamTokenizer.TT_WORD)  {
        if (Debug.val()>2) Debug.println("\ntoken " + st.sval + "\t@ " + node);
      } else if (toknum == StreamTokenizer.TT_NUMBER)  {
        if (Debug.val()>2) Debug.println("\ntoken " + st.nval + "\t@ " + node);
      } else  {
        if (Debug.val()>2) Debug.println("\ntoken " + Character.toString((char)toknum) + " " + toknum + "\t@ " + node);
      }  

      switch (toknum) {
      case '"':
      case '\'':
      case StreamTokenizer.TT_WORD:
        if (closepar && lasttype != ':') {
          // this is inner node comment, in clustalw-nh format, we hope...
          ((PhylogeNode)node.getUserObject()).setComment(st.sval); 
          if (Debug.val()>1) Debug.print( "[" + st.sval + "]");
          }
        else if (lasttype == ':') {
          double val = 0.;
          try {
            val = Double.parseDouble(st.sval);  
          } catch (NumberFormatException nfex) {
            if (st.sval != null && st.sval.toUpperCase().indexOf("E") >= 0) {
              try {
                String ns = st.sval.replace('e','E');
                int iE = ns.indexOf('E');
                if (iE >= 0 && ns.charAt(iE+1) == '+') {
                  ns = ns.substring(0,iE+1) + ns.substring(iE+2);
                }
                DecimalFormat dfmt = new DecimalFormat();
                ParsePosition pp = new ParsePosition(0);
                val = dfmt.parse(ns,pp).doubleValue();
              } catch (Exception ex) {
               ExceptionHandler.popupException("Failed to parse " + st.sval + " " + ex);
              }
            } 
          }
          ((PhylogeNode)node.getUserObject()).setDistance(val); 
          }
        else {
          node = new DefaultMutableTreeNode(new PhylogeNode());
          String val= st.sval;
          if (Debug.val()>1) Debug.print(val);
          ((PhylogeNode)node.getUserObject()).setName(val); 
          }
        lasttype= st.ttype;
         break;

      case '<':
        if (fEatHTML) {
          do st.nextToken(); 
          while (st.ttype != '>' && st.ttype != st.TT_EOF);
          if (st.ttype == st.TT_EOF) st.pushBack();
          }
        break;
        
      case '#':
        if (readDataComment()) {
          if (fDataLabel.equalsIgnoreCase("name")||fDataLabel.equalsIgnoreCase("title")) {
            ((PhylogeNode)node.getUserObject()).setComment(fDataComment); 
          }
        }
        break;
        
      case '[': // start node comment/label
        String comment= readNodeComment(); // may be list of comments/node !?
        ((PhylogeNode)node.getUserObject()).setComment(comment); 
        if (Debug.val()>1) Debug.print( "[" + comment + "]");
        lasttype= st.ttype;
        closepar= false;
        break;
      case ']': // end comment/label
        lasttype= st.ttype;
         closepar= false;
        break;
        
      case '(': 
        if (Debug.val()>1) Debug.print("(");
        node = new DefaultMutableTreeNode(new PhylogeNode());
        lasttype= st.ttype;
        closepar= false;  
        for (DefaultMutableTreeNode tn = readNode(); tn != null; tn = readNode()) {
          node.add(tn);
          if (st.ttype == ')')
            break;
        }
        lasttype= st.ttype;
        closepar= true; //??
        //more= false;
        break;
        
      case ')':
        if (Debug.val()>1) Debug.println(")");
        more= false;
        lasttype= st.ttype;
        closepar= true; // but we pop out of this recursion & lose use of this.
        break;

      case StreamTokenizer.TT_EOF: 
        // fall into eotree mark
      case ';': // end of tree mark
        if (Debug.val()>1) Debug.println(";");
        lasttype= st.ttype;
         closepar= false;
        more= false; 
        break;
     
      case ',':    // same-level node separator
        if (Debug.val()>1) Debug.println(",");
         closepar= false;
        more= false; // new node wanted after this
        break;

      case ':':    // name:distance separator
        lasttype= st.ttype;
        if (Debug.val()>1) Debug.print(":");
        break;

      case StreamTokenizer.TT_NUMBER:
        // we need to parse 5.9E-4 as a number  
        if (lasttype != ':' && closepar) {
          // this is inner node comment, in clustalw-nh format, we hope...
          ((PhylogeNode)node.getUserObject()).setComment(Double.toString(st.nval)); 
          if (Debug.val()>1) Debug.print( "[" + st.nval + "]");
          }
        else {
          double val = st.nval;
          int eTok = st.nextToken();
          if (eTok == StreamTokenizer.TT_WORD && st.sval.equalsIgnoreCase("E")) {
            int nTok = st.nextToken();
            if (nTok == StreamTokenizer.TT_NUMBER) {
              val *= Math.pow(10,st.nval);
            }
          } else {
            st.pushBack();
          }
          ((PhylogeNode)node.getUserObject()).setDistance(val); 
          if (Debug.val()>1) Debug.print( ""+val);
          }
        lasttype= st.ttype;
        break;
      } // switch(tok)
    }
    if (node == null && st.ttype != ';' && st.ttype != StreamTokenizer.TT_EOF) {
      node = new DefaultMutableTreeNode(new PhylogeNode());
    }
    if (Debug.val()>2) Debug.println("\n<<<readNode" + depth-- + "  " + node);
    return node;   
  }
  
      
  public boolean isMydata(byte[] data) 
  {
    boolean isNewick = false;
    char  ch;
    int   testLen = data.length;
    int   ic, onlyp, na, nn, ne, nb, ns, np, nd, ncomma, ncolon,nscolon, no;
    onlyp= na = nb = ne = np = nn = ns = nd= ncomma= ncolon= nscolon= no = 0;
System.err.println("JJ isMydata " + data.length);
    
    for (ic=0; ic<testLen; ic++) {
      ch = (char) data[ic];
      // OS/2 java 1.0.1 doesn't have Character.isLetter() !!
      //if (Character.isLetter(ch)) na++;
      //else if (Character.isDigit(ch)) nd++;
      if (ch >= 'A' && ch <= 'Z') na++;
      else if (ch >= 'a' && ch <= 'z') na++;
      else if (ch >= '0' && ch <= '9') nd++;
      else if (ch <= ' ') ; // skip white
      else switch (ch) {
        case ',': ncomma++; break;
        case ':': ncolon++; break;
        case ';': nscolon++; break;
        case '(': 
        case ')': np++; break;  
        case '[': 
        case ']': nb++; break;   
         case '{': 
         case '}': ns++; break;  
         // ignore <> from HTML codes in data
        //case '<': 
        //case '>': ne++; break;
        default : no++; break;
        }
      }
System.err.println("JJ isMydata " + data.length + " ic=" + ic + " np=" + np + " nb=" + nb + " ne=" + ne + " ns=" + ns + " na=" + na + " ncomma=" + ncomma );
    ic = np; ch = '('; // )
    if (nb > ic) { ch = '['; ic = nb;// ]
             } 
    if (ne > ic) { ch = '<'; ic = ne;  }
    if (ns > ic) { ch = '{'; ic = ns;  } // }

    if (np > 0 && ncomma > 0 && na > 0)
      isNewick= true;
    /*******
    ic = 0; np = 0; nb = 0;
    String st= new String( data, 0);
    do {
      ic = st.indexOf( ch, ic);
      if (ic>=0) {
        ic++;
        //look for '(word[:num,word:num],('  ))
        isNewick = Character.isLetter( (char)data[ic]) && (data[ic+1] == ch); 
        if (isNewick) nb++;
        }
    } while (np < 3 && nb < 3 && ic >= 0);
    isNewick = nb > np;
    ********/
System.err.println("JJ isMydata " + data.length + " " + isNewick);
    return isNewick;
  }    
   
  
};
  
 
