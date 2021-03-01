package Simulation;

import com.google.gson.Gson;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.PrintWriter;

public class Launcher extends Application {
    private static final int sceneWidth = 1400;
    private static final int sceneHeight = 800;
    public void start(Stage primaryStage) throws Exception{
        Simulation.Parameters parameters  = new Gson().fromJson(new FileReader("src/Simulation/parameters.json"), Simulation.Parameters.class);
        GridPane root = new GridPane();
        Scene scene = new Scene(root, sceneWidth, sceneHeight);
        Simulator simulator1 = new Simulator(parameters);
        Simulator simulator2 = new Simulator(parameters);
        root.add(simulator1.getRoot(), 0, 0);
        root.add(simulator2.getRoot(), 1, 0);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
