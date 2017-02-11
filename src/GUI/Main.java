package GUI;

import GUI.Controller;
import Trainer.VocabManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class Main extends Application {
    /**
     * Shows a JavaFX Info Alert and waits for closure.
     * @param title The title of the alert
     * @param message The message of the alert
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.getDialogPane().setPrefSize(200, 100);
        alert.showAndWait();
    }

    /**
     * Starting method
     * @param args Arguments provided by program start
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * JavaFX starting method. Opens fxml file TrainerWindow.fxml.
     * @param primaryStage Main Stage?
     * @throws Exception All Exceptions?
     */
    @Override
    public void start(@NotNull Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TrainerWindow.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        VocabManager verwalter = new VocabManager(controller, primaryStage);
        controller.init(verwalter);
        primaryStage.setTitle("Vocab-Trainer");
        primaryStage.setScene(new Scene(root, 550, 400));
        primaryStage.setResizable(false);

        primaryStage.show();
    }
}
