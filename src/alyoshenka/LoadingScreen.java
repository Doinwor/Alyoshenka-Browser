package alyoshenka;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.FileInputStream;

public class LoadingScreen {

    public static final Color FLAG_BLUE = Color.web("#0039A6");
    public static final Color FLAG_RED = Color.web("#D52B1E");

    private static final Duration LOAD_DURATION = Duration.seconds(3.0);

    private final Circle[] dots = new Circle[3];
    private AnimationTimer dotsTimer;
    private Runnable onFinished;

    public Scene createScene() {
        StackPane root = new StackPane();
        root.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #123a7a, #081a3a);
                """);

        Image logoImage = loadImage("assets/logo.png");
        ImageView logoView = null;
        if (logoImage != null) {
            logoView = new ImageView(logoImage);
            logoView.setFitWidth(620);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);
        }

        Text caption = new Text("русский национальный браузер");
        caption.setFont(Font.font("Segoe UI", FontWeight.LIGHT, 21));
        caption.setFill(Color.web("#A9C0E4"));

        VBox column = new VBox(34);
        column.setAlignment(javafx.geometry.Pos.CENTER);
        if (logoView != null) {
            column.getChildren().add(logoView);
        }
        column.getChildren().addAll(caption, buildDots());

        root.getChildren().add(column);
        return new Scene(root, 1000, 700);
    }

    private HBox buildDots() {
        Color[] colors = {Color.WHITE, FLAG_BLUE, FLAG_RED};
        HBox box = new HBox(28);
        box.setAlignment(javafx.geometry.Pos.CENTER);

        for (int i = 0; i < dots.length; i++) {
            Circle dot = new Circle(12, colors[i]);
            dot.setStroke(Color.web("#FFFFFF88"));
            dot.setStrokeWidth(1.5);
            dots[i] = dot;
            box.getChildren().add(dot);
        }
        return box;
    }

    public void play() {
        if (dotsTimer == null) {
            dotsTimer = new AnimationTimer() {
                private long startNanos = -1;

                @Override
                public void handle(long now) {
                    if (startNanos < 0) {
                        startNanos = now;
                    }
                    double t = (now - startNanos) / 1_000_000_000.0;
                    for (int i = 0; i < dots.length; i++) {
                        double phase = ((t * 0.9) + i * 0.3333) % 1.0;
                        double s = 0.5 + 0.5 * Math.sin(phase * Math.PI);
                        dots[i].setScaleX(s);
                        dots[i].setScaleY(s);
                        dots[i].setOpacity(s * 0.6 + 0.4);
                    }
                }
            };
        }
        dotsTimer.start();

        Timeline loading = new Timeline(
                new javafx.animation.KeyFrame(LOAD_DURATION, e -> {
                    dotsTimer.stop();
                    if (onFinished != null) {
                        onFinished.run();
                    }
                }));
        loading.setCycleCount(1);
        loading.play();
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    private static Image loadImage(String path) {
        Image res = BrowserWindow.loadFromResource(path);
        if (res != null) return res;
        try {
            return new Image(new FileInputStream(path));
        } catch (Exception e) {
            return null;
        }
    }
}