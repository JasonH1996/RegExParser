import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.regex.Matcher;


public class Grep {
  //have to create variables for each of the elements of the UI being interacted with
  boolean do_substring;
  public TextArea filebox;
  public TextArea resultbox;
  public File selectedFile = null;
  public TextField fullquery;
  public TextField querycomposer;
  public TextField fTextField;
  
  //methods for various core UI functions

  public void Browser() {
    JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    int returnValue = fc.showOpenDialog(null);
    filebox.setText("");
    if (returnValue == JFileChooser.APPROVE_OPTION){
      selectedFile = fc.getSelectedFile();
      System.out.println("Test: File chosen is " + selectedFile.getAbsolutePath());
      
      try{
      BufferedReader br = new BufferedReader(new FileReader(selectedFile));
      String fileline;
      while ((fileline = br.readLine()) != null){
        filebox.appendText(fileline);
      }
      br.close();
      }
      catch (Exception exception) {
        filebox.setText(Arrays.toString(exception.getStackTrace()));
      }
      
    }
  }

  public void Searcher() {
    Grep g = new Grep(false);
    BufferedReader br = g.getReader(filebox.getText());
    g.fileSearch(fullquery.getText(),br);
    //filesearch method takes care of the printing of matches.
  }

  public void ExSearchFetch() {
    fullquery.setText(".*\\b(B|[Hh])([a-z]{3}y\\b.*|ee.*)");
  }

  public void Adder() {
    fullquery.appendText(querycomposer.getText());
    querycomposer.setText("");
  }

  public void ExFileFetch() {
    String filename = "beemovie.txt";
    filebox.setText("");
    try{
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String fileline;
      while ((fileline = br.readLine()) != null){
        filebox.appendText(fileline);
      }
      br.close();
      }
      catch (Exception exception) {
        filebox.setText(Arrays.toString(exception.getStackTrace()));
      }
  }

  public void CheatSheetLoader() {
    String filename = "cheatsheet.txt";
    resultbox.setText("");
    try{
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String fileline;
      while ((fileline = br.readLine()) != null){
        resultbox.appendText(fileline);
      }
      br.close();
      }
      catch (Exception exception) {
        resultbox.setText(Arrays.toString(exception.getStackTrace()));
      }
  }

  //warns people if a button can't be pressed due to the cursor being in a literal sequence
  //returns false if literal sequence is active, true if not active
  public Boolean LiteralCheck() {
    FocusCheck();
    int caretpos = fTextField.getCaretPosition();
    if(caretpos != 0 && caretpos!=fTextField.getLength()){
      String query = fTextField.getText();
      String front = query.substring(0, fTextField.getCaretPosition());
      //splits query based on cursor position

        String regex = "\\Q(?!.*\\E)";
        Pattern re = Pattern.compile(regex);
          Matcher matcher = re.matcher(front);
          if (do_substring && matcher.find() ||
              !do_substring && matcher.matches()) {
                System.out.println("Test: If this appears, LiteralCheck says that there is a \\Q with no \\E literal sequence exit.");
                LiteralWarningMessage();
                return false;
          }
          else return true;
        }
       else return true;
  }

  //checks if selection or caret is within a set of parentheses already
  //if false, then it is within a set of parentheses. if true, it is not.
  public boolean ParenthesesCheck() {
    FocusCheck();
    int caretpos;
    if (fTextField.getSelectedText() != null){
      caretpos = fTextField.toString().lastIndexOf(fTextField.getSelectedText());
    }
    else {
      caretpos = fTextField.getCaretPosition();
    }


    if(caretpos != 0 && caretpos!=fTextField.getLength()){
      String query = fTextField.getText();
      String front = query.substring(0, caretpos);
      //splits query based on cursor position

        String regex = "\\((?!.*\\)";
        Pattern re = Pattern.compile(regex);
          Matcher matcher = re.matcher(front);
          if (do_substring && matcher.find() ||
              !do_substring && matcher.matches()) {

                return false;
          }
          else return true;
        }
       else return true;
  }


  public void LiteralWarningMessage () {
    Dialog dialog = new Dialog<>();
    dialog.setTitle("Function buttons cannot be used in a literal sequence, which ends with \\E.");
    dialog.show();
    Thread dialogThread = new Thread(new Runnable(){
      @Override
      public void run() {
        try {
            Thread.sleep(8000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                dialog.close();
            }
            });
        }
    });
    dialogThread.start();
  }
  
  //checks to see which TextField is focused, sets it to the end of querycomposer if neither is focused
  public void FocusCheck() {
    if(querycomposer.isFocused()) {
      fTextField = querycomposer;
    }
    else if (fullquery.isFocused()){
      fTextField = fullquery;
    }
    else {
      querycomposer.selectPositionCaret(querycomposer.getLength());
      fTextField = querycomposer;
    }
  }

  //methods for the query assembly buttons

  public void Any() {
    FocusCheck();
    if(!LiteralCheck()){return;}
    fTextField.insertText(fTextField.getCaretPosition(), ".");
  }

  public void Exact() {
    FocusCheck();
    if(!LiteralCheck()){return;}
    if (fTextField.getText().isEmpty()) {
      fTextField.setText("\\Q\\E");
      fTextField.positionCaret(2);
    }
    else {
      fTextField.insertText(fTextField.getCaretPosition(), "\\Q\\E");
      fTextField.selectPositionCaret(fTextField.getCaretPosition()-2);
    }
  }

  public void CaseSensitive() {
    FocusCheck();
    if(!LiteralCheck()){return;}
    fTextField.insertText(fTextField.getCaretPosition(), "(?i)");
  }
  
  public void CaseInsensitive() {
    FocusCheck();
    if(!LiteralCheck()){return;}
    fTextField.insertText(fTextField.getCaretPosition(), "(?-i)");
  }

  public void Or() {
    FocusCheck();
    if(!LiteralCheck()){return;}

    //if text is selected and parentheses check passes, do this
    if(fTextField.getSelectedText() != null && ParenthesesCheck()) { 
      //inserts first parentheses at beginning of selected text
      fTextField.insertText(fTextField.toString().lastIndexOf(fTextField.getSelectedText()), "(");
      //inserts divider and second parentheses at end of selected text
      fTextField.insertText(fTextField.toString().lastIndexOf(fTextField.getSelectedText())
      +fTextField.getSelectedText().length(), "|)");
      //adjusts caret position
      fTextField.selectPositionCaret(fTextField.toString().lastIndexOf(fTextField.getSelectedText())
      +fTextField.getSelectedText().length()+1);
    }

    //if text is selected and the parentheses check does not pass, do this
    else if (fTextField.getSelectedText() != null && !ParenthesesCheck()){
      fTextField.insertText(fTextField.toString().lastIndexOf(fTextField.getSelectedText())
      +fTextField.getSelectedText().length(), "|");
      fTextField.selectPositionCaret(fTextField.getCaretPosition()+1);
    }

    //if no text is selected and parentheses check passes, do this
    else if (ParenthesesCheck()) {
      fTextField.insertText(0, "(");
      fTextField.insertText(fTextField.getCaretPosition(), "|)");
      fTextField.selectPositionCaret(fTextField.getCaretPosition() + 2);
    }

    //if no text is selected and the parentheses check does not pass, do this
    else {
      fTextField.insertText(fTextField.getCaretPosition(),"|");
      fTextField.selectPositionCaret(fTextField.getCaretPosition()+1);
    }
  }
  
  public void Not() {
    FocusCheck();
    if(!LiteralCheck()){return;}

    //regex not operator must be used at beginning of sequence starting with parentheses
    //at least within the context of this simplified program, anyway.

    //if text is selected, do this
    if(fTextField.getSelectedText() != null){
      //inserts first parentheses at beginning of selected text
      fTextField.insertText(fTextField.toString().lastIndexOf(fTextField.getSelectedText()), "(^");
      //inserts divider and second parentheses at end of selected text
      fTextField.insertText(fTextField.toString().lastIndexOf(fTextField.getSelectedText())
      +fTextField.getSelectedText().length(), ")");
      //adjusts caret position
      fTextField.selectPositionCaret(fTextField.toString().lastIndexOf(fTextField.getSelectedText())
      +fTextField.getSelectedText().length()+2);
    }

    //if text is not selected, do this
    else {
      fTextField.insertText(fTextField.getCaretPosition(), "(^)");
      fTextField.selectPositionCaret(fTextField.getCaretPosition()+2);
    }


    
  }

  public void To() {
    FocusCheck();
    int caretpos;
    if (fTextField.getSelectedText() != null){
      caretpos = fTextField.toString().lastIndexOf(fTextField.getSelectedText() + fTextField.getLength());
    }
    else {
      caretpos = fTextField.getCaretPosition();
    }
    
    String query = fTextField.getText();
    String front = query.substring(0, caretpos);
    int length = 0;
    if (fTextField.getLength() != 0){
      for(int i = front.length(); i > 0; i--){
        if(Character.isDigit(query.charAt(i))) {
          length++;
        }
        }
        if (length<1) {
          fTextField.insertText(caretpos-1, "[");
          fTextField.insertText(caretpos+1, "-]");
          fTextField.selectPositionCaret(caretpos + 2);
        }
        else {
          fTextField.insertText(caretpos-length, "[");
          fTextField.insertText(caretpos, "-]");
          fTextField.selectPositionCaret(caretpos+1);
      }
    }
    else {
      fTextField.insertText(0, "[-]");
      fTextField.selectPositionCaret(2);
    }
      
}

  //methods for the actual searching functionality

  public Grep(boolean support_substring) {
    this.do_substring=support_substring;
  }
  
  public int fileSearch(String regex, BufferedReader br) {
    int count = 0;
    resultbox.setText("");
    try {
      String oneline="";
      Pattern re = Pattern.compile(regex);
      while ((oneline=br.readLine())!=null) {

        Matcher matcher = re.matcher(oneline);
        if (do_substring && matcher.find() ||
            !do_substring && matcher.matches()) {
          //System.out.println(oneline);
          resultbox.appendText(oneline + "\n");
          count++;
        }
      }
    } catch (Exception e) {
      resultbox.setText("Problem reading file.");
      System.exit(3);
    }
    resultbox.appendText("\n" + count + " lines matching your search.");
    return count;
  }
  
  public BufferedReader getReader(String filename) {
    File file = new File(filename);
    BufferedReader br=null;
    try {
      FileInputStream is = new FileInputStream(file);
      InputStreamReader isr = new InputStreamReader(is);
      br = new BufferedReader(isr);
    } catch (Exception e) {
      System.err.println("Cannot open file.");
      System.exit(2);
      
    }
    return br;
  }
  public static void main ( String [] args ) {
    Scanner input = new Scanner(System.in);
    System.out.println("Enter txt file absolute path here: \n");
    String filename = input.nextLine();
    String regex=".*\\b(B|[Hh])([a-z]{3}y\\b.*|ee.*)";
   // String filename = "beemovie.txt";
    // System.out.println("Enter RegEx search here: \n");
    System.out.println("RegEx "+regex);
    System.out.println("File "+filename);
    System.out.println("Searching... ");
    Grep g = new Grep(false);
    BufferedReader br = g.getReader(filename);
    g.fileSearch(regex,br);
    input.close();
  }
}
