package ua.sumdu.j2se.kryshtop.tasks.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import ua.sumdu.j2se.kryshtop.tasks.MainApp;

import java.io.IOException;

public class MainController {
    @FXML
    private Button calendarButton;

    @FXML
    private Button addNewTaskButton;

    @FXML
    private Button exitButton;

    @FXML
    public void initialize(){
        calendarButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    try{
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Calendar.fxml"));
                        Parent root = fxmlLoader.load();
                        Scene scene = new Scene(root, 710, 405);

                        Stage stage = new Stage();

                        stage.setOnCloseRequest(we -> {
                                stage.close();
                                MainApp.primaryStage.show();
                        });

                        stage.setTitle("Calendar");
                        stage.setScene(scene);
                        stage.setResizable(false);
                        stage.show();
                    } catch(IOException e){
                        e.printStackTrace();
                        //TODO: add some logic - giving an error massage and print to log
                    }

                    MainApp.primaryStage.close();
                }
        });

        addNewTaskButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                try{
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/AddEdit.fxml"));
                    Parent root = fxmlLoader.load();
                    Scene scene = new Scene(root, 710, 405);

                    Stage stage = new Stage();

                    stage.setOnCloseRequest(we -> {
                        stage.close();
                        MainApp.primaryStage.show();
                    });

                    stage.setTitle("Add new task");
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } catch(IOException e){
                    e.printStackTrace();
                    //TODO: add some logic - giving an error massage and print to log
                }

                MainApp.primaryStage.close();
            }
        });

        exitButton.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            MainApp.primaryStage.close();
            //TODO: write data(temporary collection) into file
        });
    }

}
