import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.regex.Matcher;


public class Grep {

  public TextArea filebox;
  public TextArea resultbox;
  public File selectedFile = null;
  public TextField fullquery;

  public void Browser() {
    JFileChooser fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    int returnValue = fc.showOpenDialog(null);
    if (returnValue == JFileChooser.APPROVE_OPTION){
      File selectedFile = fc.getSelectedFile();
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
        exception.printStackTrace();
      }
      
    }
  }

  public void Searcher() {
    Grep g = new Grep(false);
    BufferedReader br = g.getReader(selectedFile.getAbsolutePath());
    g.fileSearch(fullquery.getText(),br);
    //filesearch method takes care of the printing of matches.
  }

  boolean do_substring;
  public Grep(boolean support_substring) {
    this.do_substring=support_substring;
  }
  public int fileSearch (String regex, BufferedReader br) {
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
