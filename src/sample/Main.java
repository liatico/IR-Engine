// Created by:
//              Liat Cohen 205595283
//              Adir Biran 308567239

package sample;

import ModelPackage.ClickStreamData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        FXMLLoader loader = new FXMLLoader();
        Parent root = null;
        try {
            loader.setLocation(getClass().getResource("IRProjectFxml.fxml"));
            root = loader.load();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(root!=null){

            Controller controller = loader.getController();
            ViewModel viewModel = new ViewModel(controller, null);
            Model model = new Model(viewModel);
            viewModel.setModel(model);
            controller.setViewModel(viewModel);

            primaryStage.setTitle("IR Engine");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        }
    }


    public static void main(String[] args) {

       launch(args);


    }

}