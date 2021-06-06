package it.polimi.ingsw.client.view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GraphicTest extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        //LoginController controller = new LoginController();

        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/fxmls/" + "test" + ".fxml"));
        //fxmlLoader.setController(controller);

        Parent root = fxmlLoader.load();
        stage.setTitle("test");
        stage.setScene(new Scene(root,800,800));
        stage.show();
    }
    public void show(){
        launch();
    }
}
