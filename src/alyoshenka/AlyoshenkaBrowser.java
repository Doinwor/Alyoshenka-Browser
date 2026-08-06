package alyoshenka;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.InputStream;

public class AlyoshenkaBrowser extends Application {

    public static final String APP_NAME = "Алешенька — русский национальный браузер";

    @Override
    public void start(Stage stage) {
        applyIcon(stage);

        LoadingScreen loading = new LoadingScreen();
        Scene loadingScene = loading.createScene();

        BrowserWindow browser = new BrowserWindow();

        stage.setTitle(APP_NAME);
        stage.setMinWidth(820);
        stage.setMinHeight(580);
        stage.setScene(loadingScene);
        stage.show();

        loading.setOnFinished(() -> {
            browser.loadHome();
            stage.setScene(browser.createScene());
            stage.setTitle(APP_NAME);
        });
        loading.play();
    }

    private static void applyIcon(Stage stage) {
        Image icon = tryLoad("assets/icon.png");
        if (icon != null) {
            stage.getIcons().add(icon);
        }
    }

    private static Image tryLoad(String path) {
        Image res = BrowserWindow.loadFromResource(path);
        if (res != null) return res;
        try {
            return new Image(new FileInputStream(path));
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}