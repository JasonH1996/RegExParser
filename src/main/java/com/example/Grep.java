package src.main.java.com.example;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

import java.util.regex.Matcher;


public class Grep {
  @FXML 
  public VBox appBox;
  private Main main;
  //have to create variables for each of the elements of the UI being interacted with
  boolean do_substring;
  public TextArea filebox;
  public TextArea resultbox;
  public File selectedFile;
  public TextField fullquery;
  public TextField querycomposer;
  public TextField fTextField;
  public String filename;
  public String command;
  
  //methods for various core UI buttons

  public void Browser() {
    JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    int returnValue = fc.showOpenDialog(null);
    filebox.setText("");
    filebox.setWrapText(true);
    if (returnValue == JFileChooser.APPROVE_OPTION){
      selectedFile = fc.getSelectedFile();
      System.out.println("Test: File chosen is " + selectedFile.getAbsolutePath());
      filename = selectedFile.getAbsolutePath();
      try{
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String fileline;
      while ((fileline = br.readLine()) != null){
        filebox.appendText(fileline + "\r\n");
        
      }
      br.close();
      }
      catch (Exception exception) {
        filebox.setText(Arrays.toString(exception.getStackTrace()));
      }
      
    }
  }

  public void Searcher() {
    try {
      fileSearch();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
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
    filename = "beemovie.txt";
    filebox.setText("");
    filebox.setWrapText(true);
    try{
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String fileline;
      while ((fileline = br.readLine()) != null){
        filebox.appendText(fileline + "\r\n");
      }
      br.close();
      }
      catch (Exception exception) {
        filebox.setText(Arrays.toString(exception.getStackTrace()));
      }
  }

  public void CheatSheetLoader() {
    filename = "cheatsheet.txt";
    resultbox.setText("");
    resultbox.setWrapText(true);
    try{
      BufferedReader br = new BufferedReader(new FileReader(filename));
      String fileline;
      while ((fileline = br.readLine()) != null){
        resultbox.appendText(fileline + "\r\n");
      }
      br.close();
      }
      catch (Exception exception) {
        resultbox.setText(Arrays.toString(exception.getStackTrace()));
      }
  }












  //Methods for various checks that are needed to ensure consistent functionality of the buttons. 


  //warns people if a button can't be pressed due to the cursor being in a literal sequence
  //returns false if literal sequence is active, true if not active
  public Boolean LiteralCheck() {
    FocusCheck();
    int caretpos = fTextField.getCaretPosition();
    if(caretpos != 0){
      String query = fTextField.getText();
      String front = query.substring(0, fTextField.getCaretPosition());
     //FOR TESTING PURPOSES: System.out.println(front);
        String regex = "\\\\Q(?!.*\\\\E)";
        Pattern re = Pattern.compile(regex);
          Matcher matcher = re.matcher(front);
          if (matcher.find()) {
                //FOR TESTING: System.out.println("Test: If this appears, LiteralCheck says that there is a \\Q with no \\E literal sequence exit.");
                String message = "You're trying to set a function in a literal sequence. \n A literal is when the search uses the exact characters entered. \n This is represented by \\Q at the beginning and \\E at the end. \n This alert appears when a function is being used after a \\Q that is not followed by a \\E before the input.";
                WarningMessage(message);
                return false;
          }
          else return true;
        }
       else return true;
  }

  // checks if text is selected in focused 
  // if true, then text is selected. if false, text is not selected.
  public boolean SelectionCheck() {
    FocusCheck();
    if (fTextField.getSelectedText().length() > 0) {
      return true;
    }
    else return false;
  }

   //checks if selection or caret is within a set of parentheses already
  //if false, then it is within a set of parentheses. if true, it is not.
  //this is to ensure no misinput via certain buttons, such as OR
  public boolean ParenthesesCheck() {
    FocusCheck();
    int caretpos;
    caretpos = TrueCaretPos();
    if(caretpos > 0){
        String query = fTextField.getText();
        String front = query.substring(0, caretpos);
        //splits query based on cursor position
        String regex = ".*\\((?!.*\\))";
        Pattern re = Pattern.compile(regex);
        Matcher matcher = re.matcher(front);
          if (matcher.find()) {
            return false;
          }
          else
          return true;
        }
       else 
       return true;
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
      if(querycomposer.getLength() > 0) {
      querycomposer.positionCaret(querycomposer.getLength()-1);}
      else {querycomposer.positionCaret(0);}
      fTextField = querycomposer;
    }
  }













  //Various methods for reuse throughout the code, in relation to the function buttons.

  //If text is selected, this returns the position of the start of that selection.
  public int SelectionStart() {
    return  TrueCaretPos() - fTextField.getSelectedText().length();
  }
  
  
  //Any function should know to include selected text, even if caret is on
  //the left side of the selection. Thus, the caret should always be considered
  //to be the rightmost point of any text selection.
  //This uses math to figure out if the caret is on the left or right of a selection.
  //From there, it returns the proper caret position.
  public int TrueCaretPos(){
    FocusCheck();
    String query = fTextField.getText();
    String front = query.substring(0, fTextField.getCaretPosition());
    int caretPosPlusSelection =  fTextField.getCaretPosition() + fTextField.getSelectedText().length();
    //if the if statement is true, then the caret is already on the right end of the selection
    if (front.length() - 1 == caretPosPlusSelection) {
      return front.length();
    }
    else return fTextField.getCaretPosition() + fTextField.getSelectedText().length();
  }
 

  //displays a message in a separate window stating that the button cannot
  //be used, due to the caret or selection being in a literal sequence
  //it also explains what a literal sequence is and how it is represented
  public void WarningMessage (String message) {
    Alert alert = new Alert(AlertType.INFORMATION);

    alert.setTitle("Invalid Function Input!");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.show();
    Thread alertThread = new Thread(new Runnable(){
      @Override
      public void run() {
        try {
            Thread.sleep(25000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                alert.close();
            }
            });
        }
    });
    alertThread.start();
  }
 

















  
  //methods for the query assembly buttons

  public void Any() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}
    else{
    fTextField.insertText(caretpos, ".");
    }
  }
  
  public void ZeroOrOneOf() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}
    else if (caretpos != 0){
    fTextField.insertText(caretpos, "?");
    }
    else {return;}
  }
  
  public void ZeroOrMoreOf() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}
    else if (caretpos != 0){
    fTextField.insertText(caretpos, "*");
    }
    else {return;}
  }
  
  public void OneOrMoreOf() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}
    else if (caretpos != 0){
    fTextField.insertText(caretpos, "+");
    }
    else {return;}
  }
  
  public void nOf() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}
    else if (caretpos != 0){
    fTextField.insertText(caretpos, "{n}");
    fTextField.positionCaret(caretpos + 2);
    }
    else {return;}
  }

  public void Exact() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}
    if (fTextField.getText().isEmpty()) {
      fTextField.setText("\\Q\\E");
      fTextField.positionCaret(2);
    }
    else {
      fTextField.insertText(SelectionStart(), "\\Q");
      fTextField.insertText(caretpos +2, "\\E");
      fTextField.positionCaret(caretpos+2);
    }
  }

  public void CaseSensitive() {
    FocusCheck();
    if(!LiteralCheck()){return;}
    command = "(?i)";
    int caretpos = TrueCaretPos();
    fTextField.insertText(SelectionStart(), command);
    fTextField.positionCaret(caretpos + command.length());
  }
  
  public void CaseInsensitive() {
    FocusCheck();
    if(!LiteralCheck()){return;}
    command = "(?-i)";
    int caretpos = TrueCaretPos();
    fTextField.insertText(SelectionStart(), command);
    fTextField.positionCaret(caretpos + command.length());
  }

  public void Or() {
    FocusCheck();
    int caretpos = TrueCaretPos();

    if(!LiteralCheck()){return;}
    //if text is selected and parentheses check passes, do this
    else{
    if(SelectionCheck() && ParenthesesCheck()) { 
      //inserts first parentheses at beginning of selected text
      fTextField.insertText(SelectionStart(), "(");
      //inserts divider and second parentheses at end of selected text
      fTextField.insertText(caretpos + 1, "|)");
      //adjusts caret position
      fTextField.positionCaret(caretpos + 2);
    }

    //if text is selected and the parentheses check does not pass, do this
    else if (SelectionCheck() && !ParenthesesCheck()){
      fTextField.insertText(SelectionStart(), "|");
      fTextField.positionCaret(caretpos+1);
    }

    //if no text is selected and parentheses check passes, do this
    else if (!SelectionCheck() && ParenthesesCheck()) {
      fTextField.insertText(0, "(");
      fTextField.insertText(caretpos + 1, "|)");
      fTextField.positionCaret(caretpos + 2);
    }

    //if no text is selected and the parentheses check does not pass, do this
    else {
      fTextField.insertText(caretpos,"|");
      fTextField.positionCaret(caretpos+1);
    }
  }
  }
  
  public void Not() {
    FocusCheck();
    if(!LiteralCheck()){return;}
    int caretpos = TrueCaretPos();

    //regex not operator must be used at beginning of sequence starting with parentheses
    //at least within the context of this simplified program, anyway.

    //if text is selected, do this
    if(SelectionCheck()){
      //inserts first parentheses at beginning of selected text
      fTextField.insertText(SelectionStart(), "(^");
      //inserts second parentheses at end of selected text
      fTextField.insertText(caretpos +2, ")");
      //adjusts caret position
      fTextField.positionCaret(caretpos+3);
    }

    //if text is not selected, do this
    else {
      fTextField.insertText(caretpos, "(^)");
      fTextField.positionCaret(caretpos+2);
    }


    
  }

  public void To() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    
    System.out.println("caretpos before running = " + caretpos);

    //if text is selected, do this
    if (SelectionCheck()){
      String query = fTextField.getText();
      int length = 0;
      System.out.println("caretpos = " + caretpos);
      for(int i = caretpos; i > SelectionStart(); i--){
        if(Character.isDigit(query.charAt(i-1))) {
          System.out.println("System has worked " + length + "times");
          length++;
        }
        else if (!Character.isLetterOrDigit(query.charAt(i-1))) {
          System.out.println("alert: non-alphanumeric character");
          break;
        }
        else {
          length++;
          break;
        }
      }
      if (length > 0){
        fTextField.insertText(caretpos-length, "[");
        fTextField.insertText(caretpos + 1, "-]");
        fTextField.positionCaret(caretpos+2);
      }
      else {
        System.out.println("Starts with alphanumeric character :(");
      }
    }
    //if text is not selected and the textfield isn't empty, do this
    else if(fTextField.getLength() > 0){
       String query = fTextField.getText();
        int length = 0;
        //for each character that is an individual number, increase length count
        for(int i = caretpos; i > 0; i--){
        if(Character.isDigit(query.charAt(i-1))) {
          System.out.println("System has worked " + length + "times");
          length++;
        }
        else if (!Character.isLetterOrDigit(query.charAt(i-1))) {
          break;
        }
        else {
          length++;
          break;
        }
      }
          if (length > 0){
            fTextField.insertText(caretpos-length, "[");
            fTextField.insertText(caretpos + 1, "-]");
            fTextField.positionCaret(caretpos+2);
          }
          else {
            System.out.println("Starts with alphanumeric character :(");
          }
        }
    //if the textfield is empty, do this
    else {
        fTextField.insertText(0, "[-]");
        fTextField.positionCaret(2);
      }
  }

  public void LineStartsWith() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}

    if(SelectionCheck() && ParenthesesCheck()) {
      fTextField.insertText(SelectionStart(), "^");
      fTextField.positionCaret(caretpos + 1);
    }


    else if (!SelectionCheck() && ParenthesesCheck()) {
      fTextField.insertText(caretpos, "^");
      fTextField.positionCaret(caretpos + 1);
    }

    else{
      String message = "This was an invalid input; this function cannot be used within parentheses or brackets!";
      WarningMessage(message);
    }
  }
  
  public void LineEndsWith() {
    FocusCheck();
    if(!LiteralCheck()){return;}
    int caretpos = TrueCaretPos();
    fTextField.insertText(caretpos, "$");
  }
  
  public void IfThen() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}

    if(fTextField.getLength() < 1) {
      fTextField.insertText(0, "?()");
      fTextField.positionCaret(2);
    }

    else if (SelectionCheck()) {
      fTextField.insertText(SelectionStart(), "(");
      fTextField.insertText(caretpos+1, ")?()");
      fTextField.positionCaret(caretpos+4);
    }

    else {
      fTextField.insertText(caretpos, "?()");
      fTextField.positionCaret(caretpos+2);
    }
  }
  
  public void Else() {
    FocusCheck();
    int caretpos = TrueCaretPos();
    if(!LiteralCheck()){return;}

    String query = fTextField.getText();
    String front = query.substring(0, caretpos);
    //splits query based on cursor position
    String regex = ".*\\?\\(.*\\)";
    Pattern re = Pattern.compile(regex);
    Matcher matcher = re.matcher(front);
    System.out.println("Get to before search");
    if (matcher.find()) {
      fTextField.insertText(caretpos, "|");
    }
    else {
      String message = "This was an invalid input; this function must be used *directly after* an IF, THEN input, which ends after the parentheses.";
      WarningMessage(message);
    }
  }
  












  
  //constructor method
  public Grep() {
  }

  public void fileSearch() {
    resultbox.setText("");
    resultbox.setWrapText(true);
    int count = 0;
    String regex = fullquery.getText();
    System.out.print(regex);
      Pattern re = Pattern.compile(regex);
      Matcher matcher = re.matcher(filebox.getText());
      while (matcher.find()) {
        resultbox.appendText(matcher.group() + "\r\n");
        count++;
      }
      resultbox.appendText("\r\n" + count + " matches.");
  }

  void setMain(Main main) {
    this.main = main;
  }
}
