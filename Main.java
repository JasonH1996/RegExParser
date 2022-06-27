import java.util.concurrent.atomic.AtomicInteger;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


public class Main extends Application {
    
    private Stage primaryStage;
    private Parent root;

    @Override
    public void start(Stage primaryStage) throws Exception{
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");


        System.out.println("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        try {
            this.primaryStage = primaryStage;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("GUI.fxml"));
            Grep controller = new Grep();
            loader.setController(controller);
            controller.setMain(this);
            root = loader.load();
            Scene scene = new Scene(root, 1280, 720);
            primaryStage.setScene(scene);
            primaryStage.show();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    
   // public void startApp() throws Exception {

     //   try {
       //     root = FXMLLoader.load(Main.class.getResource("GUI.fxml"));
         //   Scene scene = new Scene(root, 1280, 720);
           // primaryStage.setScene(scene);
           // primaryStage.show();
       // }
        //catch (Exception e) {
        //    e.printStackTrace();
        //}
   // }


    public static void main(String[] args) {
        launch(args);
    }
}
