package com.polytech;

import com.polytech.ui.AppView;
import com.polytech.ui.AppViewModel;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class Main extends Application {

    public static void main(String... args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Capacitated Vehicle Routing Problem");

        ViewTuple<AppView, AppViewModel> viewTuple = FluentViewLoader.fxmlView(AppView.class).load();
        Parent root = viewTuple.getView();
        stage.setScene(new Scene(root));
        stage.show();
    }
}