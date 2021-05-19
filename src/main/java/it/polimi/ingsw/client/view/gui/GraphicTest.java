package it.polimi.ingsw.client.view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class GraphicTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxmls/test.fxml")));
        stage.setTitle("test");
        stage.setScene(new Scene(root,300,275));
        stage.show();
    }
    public void show(){
        launch();
    }
}
