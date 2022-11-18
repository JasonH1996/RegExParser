package com.example;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;


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
            loader.setLocation(getClass().getClassLoader().getResource("GUI.fxml"));
            Grep controller = new Grep();
            loader.setController(controller);
            root = loader.load();
            controller.setMain(this);
            Scene scene = new Scene(root, 1000, 570);
            primaryStage.setScene(scene);
            primaryStage.setTitle("RegEx Simplified");
            primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("icons8-magnifying-glass-64.png")));
            primaryStage.show();

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println(getClass().getResource("GUI.fxml"));
            System.out.println("If this message appears, main.java is causing some problems, bucko");
        }
    }

    

    public static void main(String[] args) {
        launch(args);
    }
}