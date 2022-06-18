import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
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

  
  public Boolean LiteralCheck() {
    int caretpos = querycomposer.getCaretPosition();
    if(caretpos != 0 && caretpos!=querycomposer.getLength()){
      String query = querycomposer.getText();
      String front = query.substring(0, querycomposer.getCaretPosition());
      //splits query based on cursor position

      Grep g = new Grep(false);
      BufferedReader br = g.getReader(front);
      int count = 0;
        String regex = "\\Q(?!.*\\E)";
        Pattern re = Pattern.compile(regex);
          Matcher matcher = re.matcher(front);
          if (do_substring && matcher.find() ||
              !do_substring && matcher.matches()) {
                System.out.println("Test: If this appears, LiteralCheck says that there is a \\Q with no \\E literal sequence exit.");
                return false;
          }
          else return true;
        }
       else return true;
  }
    //warns people if a button shouldn't be pressed due to being in a literal sequence

  //methods for the query assembly buttons

  public void Any() {
    querycomposer.insertText(querycomposer.getCaretPosition(), ".");
  }

  public void Exact() {
    if (querycomposer.getText().isEmpty() == true) {
      querycomposer.setText("\\Q\\E");
      querycomposer.positionCaret(2);
    }
    else {
      querycomposer.appendText(text);
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
