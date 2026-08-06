package alyoshenka;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BrowserWindow {

    private static final String CSS = """
            .root { -fx-font-family: "Segoe UI"; }
            .menu-bar { -fx-background-color: #FFFFFF; -fx-padding: 2 2 2 2; -fx-border-color: #D8DEE8; -fx-border-width: 0 0 1 0; }
            .menu-bar .container { -fx-background-color: #FFFFFF; }
            .menu .label { -fx-text-fill: #1B1B1B; -fx-font-size: 13px; -fx-padding: 3 9 3 9; }
            .menu:hover { -fx-background-color: #E3ECFA; }
            .menu:showing { -fx-background-color: #E3ECFA; }
            .menu-item .label { -fx-text-fill: #1B1B1B; }
            .menu-item:hover { -fx-background-color: #D7E3F6; }
            """;

    private static final String RETRO_NAV = """
            -fx-background-color: linear-gradient(to bottom, #FFFFFF 0%, #DCE9F7 45%, #C2D8EF 100%);
            -fx-background-radius: 6;
            -fx-border-color: #7E9DC0;
            -fx-border-radius: 6;
            -fx-border-width: 1;
            -fx-text-fill: #084A8E;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 2, 0.3, 0, 1);
            -fx-cursor: hand;
            """;
    private static final String RETRO_NAV_HOVER = """
            -fx-background-color: linear-gradient(to bottom, #F5FAFF 0%, #E4F0FD 50%, #CBE0F7 100%);
            -fx-background-radius: 6;
            -fx-border-color: #6F93BC;
            -fx-border-radius: 6;
            -fx-border-width: 1;
            -fx-text-fill: #06437F;
            -fx-cursor: hand;
            """;
    private static final String RETRO_NAV_PRESSED = """
            -fx-background-color: linear-gradient(to bottom, #B9D2EE 0%, #CBDEF2 60%, #DDEAF8 100%);
            -fx-background-radius: 6;
            -fx-border-color: #5E83AC;
            -fx-border-radius: 6;
            -fx-border-width: 1;
            -fx-text-fill: #04376B;
            """;
    private static final String RETRO_ADDR = """
            -fx-background-color: #FFFFFF;
            -fx-border-color: #6E7B8C #9FB0C3 #9FB0C3 #6E7B8C;
            -fx-border-width: 1;
            -fx-border-radius: 2;
            -fx-background-radius: 2;
            -fx-font-size: 14px;
            -fx-padding: 5 9 5 9;
            -fx-text-fill: #111111;
            """;

    private static final String[] FOREIGN = {
            "youtube.com", "youtu.be",
            "google.com", "google.ru",
            "instagram.com", "facebook.com", "fb.com",
            "twitter.com", "x.com",
            "tiktok.com", "netflix.com", "amazon.com",
            "reddit.com", "whatsapp.com", "twitch.tv", "discord.com",
            "linkedin.com", "pinterest.com", "snapchat.com",
            "roblox.com", "spotify.com", "ebay.com"
    };

    private static final String[][] FOREIGN_ANALOG = {
            {"youtube.com", "https://vkvideo.ru/"},
            {"youtu.be", "https://vkvideo.ru/"},
            {"google.com", "https://ya.ru/"},
            {"google.ru", "https://ya.ru/"},
            {"instagram.com", "https://vk.com/"},
            {"facebook.com", "https://vk.com/"},
            {"fb.com", "https://vk.com/"},
            {"twitter.com", "https://vk.com/"},
            {"x.com", "https://vk.com/"},
            {"tiktok.com", "https://vkvideo.ru/"},
            {"netflix.com", "https://ivi.ru/"},
            {"amazon.com", "https://market.yandex.ru/"},
            {"reddit.com", "https://pikabu.ru/"},
            {"whatsapp.com", "https://web.max.ru/"},
            {"twitch.tv", "https://vkvideo.ru/"},
            {"discord.com", "https://web.max.ru/"},
            {"linkedin.com", "https://vk.com/"},
            {"pinterest.com", "https://vk.com/"},
            {"snapchat.com", "https://vk.com/"},
            {"spotify.com", "https://music.yandex.ru/"},
            {"ebay.com", "https://market.yandex.ru/"}
    };

    private static final String[] RUSSIAN_DOMAINS = {
            "yandex.ru", "ya.ru", "yandex.com", "dzen.ru",
            "mail.ru", "ok.ru", "vk.com", "vkvideo.ru",
            "max.ru", "gosuslugi.ru", "rbc.ru", "ria.ru",
            "tass.ru", "lenta.ru", "gazeta.ru", "kommersant.ru",
            "kinopoisk.ru", "ivi.ru", "pikabu.ru",
            "mts.ru", "sberbank.ru", "sber.ru",
            "hh.ru", "ozon.ru", "wildberries.ru", "avito.ru",
            "dns-shop.ru", "citilink.ru", "edu.gov.ru",
            "music.yandex.ru", "market.yandex.ru", "web.max.ru"
    };

    private static final String[][] ADS = {
            {"Госуслуги", "Все гос. сервисы в одном месте", "#D5001C", "https://www.gosuslugi.ru/"},
            {"МАКС", "Национальный мессенджер", "#00A651", "https://web.max.ru/"},
            {"ВК Видео", "Смотри любимые фильмы", "#0077FF", "https://vkvideo.ru/"},
            {"ВКонтакте", "Будь на связи с друзьями", "#0077FF", "https://vk.com/"}
    };

    private static final double AD_PROB = 0.6;

    private static final String HOME_HTML = homeHtml();

    private final WebView webView = new WebView();
    private final WebEngine engine = webView.getEngine();
    private final WebHistory history = engine.getHistory();

    private final Button backButton = navIconButton("assets/icon_back.png", "Назад");
    private final Button forwardButton = navIconButton("assets/icon_forward.png", "Вперёд");
    private final Button refreshButton = smallIconButton("assets/icon_refresh.png", "Обновить");
    private final Button stopButton = smallIconButton("assets/icon_stop.png", "Остановить");
    private final Button homeButton = smallIconButton("assets/icon_home.png", "Домашняя страница");
    private final Button maxButton = smallIconButton("assets/maxicon.png", "Мессенджер МАКС");
    private final TextField addressField = new TextField();
    private final Label statusLabel = new Label("Готово");

    private final Label interLogo = new Label();
    private final Label interTitle = new Label();
    private final Label interSub = new Label();
    private final Button interOpen = new Button();
    private final Button interSkip = new Button("Пропустить рекламу");
    private final StackPane adOverlay;
    private final StackPane securityOverlay;
    private final ImageView securityIcon = new ImageView();
    private final Label securityTitle = new Label();
    private final Label securityMsg = new Label();
    private final Label securityHeader = new Label();
    private final Label securityAddress = new Label();
    private final Label securityFooter = new Label();
    private final HBox securityDots = new HBox(26);
    private final HBox securityActions = new HBox(14);
    private final Button securityBack;
    private final Button securityAnalog;
    private final Circle[] securityCircles = new Circle[3];
    private final ImageView loadIndicator = new ImageView();
    private final Label nationalLabel = new Label();

    private final Timeline adCheckDelay = new Timeline();
    private final Timeline interstitialTimer = new Timeline();
    private final Timeline securityCloseTimer = new Timeline();
    private final Timeline loadDoneTimer = new Timeline();
    private final Timeline securityTimeout = new Timeline();
    private AnimationTimer securityDotsTimer;
    private boolean checkPending = false;
    private boolean securityResolved = false;
    private String lastFailedLoc = null;
    private String adUrl = ADS[0][3];
    private final Image[] discImages = new Image[21];

public BrowserWindow() {
        securityBack = new Button("Вернуться назад");
        securityAnalog = new Button("Открыть российский аналог");
        adOverlay = buildInterstitialOverlay();
        securityOverlay = buildSecurityOverlay();
        loadDiscImages();
        configure();
    }

    public void loadHome() {
        goHome();
    }

    public Scene createScene() {
        webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        webView.setPrefSize(800, 600);

        BorderPane main = new BorderPane();
        main.setStyle("-fx-background-color: #FFFFFF;");
        main.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        main.setMinSize(0, 0);
        main.setTop(buildTop());
        main.setCenter(webView);
        main.setBottom(buildStatusBar());

        StackPane window = new StackPane();
        window.getChildren().add(main);

        securityOverlay.setVisible(false);
        StackPane.setAlignment(securityOverlay, Pos.CENTER);
        window.getChildren().add(securityOverlay);

        adOverlay.setVisible(false);
        StackPane.setAlignment(adOverlay, Pos.CENTER);
        window.getChildren().add(adOverlay);

        loadIndicator.setFitWidth(34);
        loadIndicator.setFitHeight(34);
        loadIndicator.setPreserveRatio(true);
        loadIndicator.setVisible(false);
        StackPane.setAlignment(loadIndicator, Pos.BOTTOM_LEFT);
        StackPane.setMargin(loadIndicator, new Insets(0, 0, 16, 16));
        window.getChildren().add(loadIndicator);

        Scene scene = new Scene(window, 1000, 700);
        scene.getStylesheets().add(toDataUrl(CSS));
        return scene;
    }

    private Node buildTop() {
        VBox top = new VBox();
        top.setStyle("-fx-background-color: #D8DEE8;");
        top.getChildren().add(buildMenuBar());
        top.getChildren().add(buildToolbar());
        return top;
    }

    private MenuBar buildMenuBar() {
        MenuBar bar = new MenuBar();

        Menu file = new Menu("Файл");
        MenuItem homeItem = new MenuItem("Домашняя страница");
        homeItem.setOnAction(e -> goHome());
        MenuItem exitItem = new MenuItem("Выход");
        exitItem.setOnAction(e -> Platform.exit());
        file.getItems().addAll(homeItem, new SeparatorMenuItem(), exitItem);

        Menu view = new Menu("Вид");
        MenuItem refreshItem = new MenuItem("Обновить");
        refreshItem.setOnAction(e -> engine.reload());
        MenuItem stopItem = new MenuItem("Остановить");
        stopItem.setOnAction(e -> engine.getLoadWorker().cancel());
        view.getItems().addAll(refreshItem, stopItem);

        Menu bookmarks = new Menu("Закладки");
        MenuItem yandexItem = new MenuItem("Поиск Яндекс");
        yandexItem.setOnAction(e -> engine.load("https://ya.ru"));
        MenuItem mailItem = new MenuItem("Почта Mail.ru");
        mailItem.setOnAction(e -> engine.load("https://mail.ru"));
        MenuItem maxItem = new MenuItem("Мессенджер МАКС");
        maxItem.setOnAction(e -> engine.load("https://web.max.ru"));
        bookmarks.getItems().addAll(yandexItem, mailItem, maxItem);

        Menu help = new Menu("Справка");
        MenuItem aboutItem = new MenuItem("О браузере");
        aboutItem.setOnAction(e -> showAbout());
        help.getItems().add(aboutItem);

        bar.getMenus().addAll(file, view, bookmarks, help);
        return bar;
    }

    private Region buildToolbar() {
        HBox toolbar = new HBox(8);
        toolbar.setPadding(new Insets(8, 10, 8, 10));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setStyle("-fx-background-color: linear-gradient(to bottom, #FFFFFF, #EDF1F7);"
                + "-fx-border-color: #C8D2E2;"
                + "-fx-border-width: 0 0 1 0;");

        addressField.setPrefHeight(34);
        addressField.setMaxHeight(34);
        addressField.setPromptText("Введите адрес сайта или поисковый запрос");
        addressField.setStyle(RETRO_ADDR);
        addressField.setOnAction(e -> navigate());

        toolbar.getChildren().addAll(
                backButton, forwardButton, refreshButton, stopButton,
                maxButton, buildBrand(), addressField, homeButton);

        HBox.setHgrow(addressField, Priority.ALWAYS);
        return toolbar;
    }

    private HBox buildBrand() {
        HBox brand = new HBox(0);
        brand.setAlignment(Pos.CENTER_LEFT);
        String word = "Алешенька";
        String[] colors = {"#FFFFFF", "#0039A6", "#D52B1E"};
        for (int i = 0; i < word.length(); i++) {
            Text letter = new Text(String.valueOf(word.charAt(i)));
            letter.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
            String color = i < 3 ? colors[i] : "#23303C";
            letter.setFill(i < 3 ? paint(color) : paint("#23303C"));
            if (i == 0) {
                letter.setStroke(paint("#1C4B7C"));
                letter.setStrokeWidth(1.6);
            }
            brand.getChildren().add(letter);
        }
        return brand;
    }

    private Color paint(String css) {
        return Color.web(css);
    }

    private Region buildStatusBar() {
        HBox status = new HBox(10);
        status.setPadding(new Insets(3, 10, 3, 10));
        status.setAlignment(Pos.CENTER_LEFT);
        status.setStyle("-fx-background-color: #E4EAF2; -fx-border-color: #C8D2E2;"
                + "-fx-border-width: 1 0 0 0;");
        statusLabel.setFont(Font.font("Segoe UI", 12));
        statusLabel.setTextFill(Color.web("#333333"));
        status.getChildren().add(statusLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        status.getChildren().add(spacer);

        nationalLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        nationalLabel.setVisible(false);
        status.getChildren().add(nationalLabel);
        return status;
    }

    private StackPane buildSecurityOverlay() {
        StackPane overlay = new StackPane();
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlay.setStyle("-fx-background-color: linear-gradient(to bottom, #EEF4FC 0%, #DDE9F7 55%, #CDDFF2 100%);");

        VBox page = new VBox(0);
        page.setAlignment(Pos.CENTER);
        page.setMaxWidth(620);
        page.setPrefWidth(620);
        page.setStyle("-fx-background-color: #FFFFFF;"
                + "-fx-background-radius: 18 18 18 18;"
                + "-fx-border-color: #C6D4E6; -fx-border-radius: 18 18 18 18; -fx-border-width: 1;");
        page.setEffect(new DropShadow(28, Color.rgb(6, 22, 46, 0.45)));

        Region bandTop = new Region();
        bandTop.setPrefHeight(9);
        bandTop.setMaxWidth(Double.MAX_VALUE);
        bandTop.setStyle("-fx-background-color: linear-gradient(to right, #0039A6 0%, #5B7DBE 45%, #D52B1E 100%);"
                + "-fx-background-radius: 18 18 0 0;");
        page.getChildren().add(bandTop);

        VBox inner = new VBox(12);
        inner.setPadding(new Insets(16, 40, 20, 40));
        inner.setAlignment(Pos.CENTER);

        HBox headerRow = new HBox(10);
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setMaxWidth(Double.MAX_VALUE);
        Image shield = loadImage("assets/icon.png");
        if (shield != null) {
            ImageView sv = new ImageView(shield);
            sv.setFitWidth(22);
            sv.setFitHeight(22);
            sv.setPreserveRatio(true);
            headerRow.getChildren().add(sv);
        }
        securityHeader.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        securityHeader.setText("Алешенька");
        securityHeader.setTextFill(Color.web("#0B2E59"));
        headerRow.getChildren().add(securityHeader);

        securityAddress.setFont(Font.font("Consolas", 12));
        securityAddress.setTextFill(Color.web("#5A6B7F"));
        securityAddress.setMaxWidth(380);
        securityAddress.setTextOverrun(OverrunStyle.ELLIPSIS);
        securityAddress.setStyle("-fx-background-color: #EEF3F9;"
                + "-fx-background-radius: 12; -fx-padding: 3 12 3 12;");
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        headerRow.getChildren().addAll(headerSpacer, securityAddress);

        HBox flagStripe = new HBox();
        flagStripe.setPrefHeight(6);
        flagStripe.setMaxWidth(180);
        flagStripe.setPrefWidth(180);
        flagStripe.getChildren().addAll(
                flagWedge("#0039A6", 1), flagWedge("#FFFFFF", 1), flagWedge("#D52B1E", 1));
        flagStripe.setEffect(new DropShadow(3, Color.rgb(0, 57, 166, 0.25)));

        securityIcon.setFitWidth(92);
        securityIcon.setFitHeight(92);
        securityIcon.setPreserveRatio(true);
        securityIcon.setSmooth(true);

        securityTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 26));
        securityTitle.setTextFill(Color.web("#0B2E59"));
        securityTitle.setAlignment(Pos.CENTER);

        securityMsg.setFont(Font.font("Segoe UI", 15));
        securityMsg.setTextFill(Color.web("#33404F"));
        securityMsg.setWrapText(true);
        securityMsg.setAlignment(Pos.CENTER);

        securityDots.setAlignment(Pos.CENTER);
        Color[] colors = {LoadingScreen.FLAG_BLUE, Color.WHITE, LoadingScreen.FLAG_RED};
        for (int i = 0; i < securityCircles.length; i++) {
            Circle c = new Circle(12, colors[i]);
            c.setStroke(Color.web("#0039A6"));
            c.setStrokeWidth(1.5);
            c.setEffect(new DropShadow(4, Color.rgb(0, 57, 166, 0.35)));
            securityCircles[i] = c;
            securityDots.getChildren().add(c);
        }

        securityBack.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        securityBack.setPrefWidth(230);
        securityBack.setStyle(RETRO_NAV);
        securityBack.setOnAction(e -> dismissBlock());

        securityAnalog.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        securityAnalog.setPrefWidth(230);
        securityAnalog.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #1E6FD9, #0B4FA3);
                -fx-background-radius: 6;
                -fx-border-color: #0A3E82;
                -fx-border-radius: 6;
                -fx-text-fill: #FFFFFF;
                """);
        securityAnalog.setVisible(false);
        securityAnalog.setOnAction(e -> openAnalog());

        securityActions.setAlignment(Pos.CENTER);
        securityActions.getChildren().addAll(securityAnalog, securityBack);

        securityFooter.setFont(Font.font("Segoe UI", 11));
        securityFooter.setText("Интернет под защитой национального браузера «Алешенька»");
        securityFooter.setTextFill(Color.web("#9AA9BB"));

        inner.getChildren().addAll(headerRow, flagStripe, securityIcon, securityTitle,
                securityMsg, securityDots, securityActions, securityFooter);
        page.getChildren().add(inner);
        overlay.getChildren().add(page);
        return overlay;
    }

    private String hostOf(String url) {
        try {
            String h = new URI(url).getHost();
            return h == null ? url : h;
        } catch (Exception e) {
            return url;
        }
    }

    private StackPane buildInterstitialOverlay() {
        StackPane overlay = new StackPane();
        overlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        overlay.setStyle("-fx-background-color: linear-gradient(to bottom, #E5EFFB 0%, #FFFFFF 55%, #E7EEF7 100%);");

        VBox page = new VBox(14);
        page.setAlignment(Pos.CENTER);
        page.setMaxWidth(620);
        page.setPrefWidth(620);
        page.setPadding(new Insets(46, 46, 40, 46));
        page.setStyle("-fx-background-color: #FFFFFF;"
                + "-fx-background-radius: 22 22 22 22;"
                + "-fx-border-color: #C6D4E6; -fx-border-radius: 22 22 22 22; -fx-border-width: 1;");
        page.setEffect(new DropShadow(30, Color.rgb(6, 22, 46, 0.5)));

        interLogo.setPrefSize(118, 118);
        interLogo.setMinSize(118, 118);
        interLogo.setMaxSize(118, 118);
        interLogo.setAlignment(Pos.CENTER);
        interLogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 54));
        interLogo.setTextFill(Color.WHITE);
        interLogo.setStyle("-fx-background-color: #0077FF; -fx-background-radius: 26;");
        interLogo.setEffect(new DropShadow(12, Color.rgb(0, 57, 166, 0.45)));

        Label brand = new Label("РЕКЛАМА · СПОНСОР ПОКАЗА");
        brand.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        brand.setTextFill(Color.web("#98A4B3"));

        interTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        interTitle.setTextFill(Color.web("#0B2E59"));

        interSub.setFont(Font.font("Segoe UI", 16));
        interSub.setTextFill(Color.web("#5A6B7F"));
        interSub.setWrapText(true);
        interSub.setAlignment(Pos.CENTER);

        interOpen.setFont(Font.font("Segoe UI", FontWeight.BOLD, 17));
        interOpen.setPrefSize(320, 54);
        interOpen.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #1E6FD9, #0B4FA3);
                -fx-background-radius: 8;
                -fx-border-color: #0A3E82;
                -fx-border-radius: 8;
                -fx-text-fill: #FFFFFF;
                """);
        interOpen.setOnMouseEntered(e -> interOpen.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #2B7EE8, #115CB9);
                -fx-background-radius: 8;
                -fx-border-color: #0A3E82;
                -fx-border-radius: 8;
                -fx-text-fill: #FFFFFF;
                """));
        interOpen.setOnMouseExited(e -> interOpen.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #1E6FD9, #0B4FA3);
                -fx-background-radius: 8;
                -fx-border-color: #0A3E82;
                -fx-border-radius: 8;
                -fx-text-fill: #FFFFFF;
                """));
        interOpen.setOnAction(e -> openInterstitialAd());

        interSkip.setFont(Font.font("Segoe UI", 13));
        interSkip.setTextFill(Color.web("#5A6B7F"));
        interSkip.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;"
                + "-fx-cursor: hand;");
        interSkip.disableProperty().addListener((o, a, dis) ->
                interSkip.setTextFill(Color.web(dis ? "#C5CDD8" : "#5A6B7F")));
        interSkip.setOnMouseEntered(e -> {
            if (!interSkip.isDisabled()) {
                interSkip.setTextFill(Color.web("#0B2E59"));
            }
        });
        interSkip.setOnMouseExited(e -> interSkip.setTextFill(interSkip.isDisabled()
                ? Color.web("#C5CDD8") : Color.web("#5A6B7F")));
        interSkip.setOnAction(e -> finishInterstitial());

        page.getChildren().addAll(interLogo, brand, interTitle, interSub, interOpen, interSkip);
        overlay.getChildren().add(page);
        return overlay;
    }

    private void openInterstitialAd() {
        interstitialTimer.stop();
        adOverlay.setVisible(false);
        if (adUrl != null) {
            engine.load(adUrl);
        } else {
            showSecurityChecking();
        }
    }

    private void finishInterstitial() {
        interstitialTimer.stop();
        adOverlay.setVisible(false);
        showSecurityChecking();
    }

    private Region flagWedge(String color, int grow) {
        Region r = new Region();
        r.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(r, Priority.ALWAYS);
        r.setStyle("-fx-background-color: " + color + ";"
                + "-fx-border-color: #C6D4E6; -fx-border-width: 0 0.5 0 0.5;");
        return r;
    }

    private Button smallIconButton(String imagePath, String tip) {
        return iconButton(20, imagePath, tip, 36, 34);
    }

    private Button navIconButton(String imagePath, String tip) {
        return iconButton(28, imagePath, tip, 60, 52);
    }

    private Button iconButton(double px, String imagePath, String tip, double w, double h) {
        Button b = new Button();
        Image img = loadImage(imagePath);
        if (img != null) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(px);
            iv.setFitHeight(px);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            b.setGraphic(iv);
        }
        b.setPrefSize(w, h);
        b.setMinSize(w, h);
        b.setTooltip(new Tooltip(tip));
        applyRetro(b);
        return b;
    }

    private static Image loadImage(String path) {
        Image res = loadFromResource(path);
        if (res != null) return res;
        try {
            return new Image(new FileInputStream(path));
        } catch (Exception e) {
            return null;
        }
    }

    static Image loadFromResource(String path) {
        String rooted = path.startsWith("/") ? path : "/" + path;
        try (java.io.InputStream in = BrowserWindow.class.getResourceAsStream(rooted)) {
            if (in != null) return new Image(in);
        } catch (Exception e) {
            // fall through to file
        }
        try (java.io.InputStream in = BrowserWindow.class.getClassLoader().getResourceAsStream(path)) {
            if (in != null) return new Image(in);
        } catch (Exception e) {
            // fall through to file
        }
        return null;
    }

    private void applyRetro(Button b) {
        b.setStyle(RETRO_NAV);
        b.setOnMouseEntered(e -> {
            if (!b.isDisabled()) {
                b.setStyle(RETRO_NAV_HOVER);
            }
        });
        b.setOnMouseExited(e -> b.setStyle(b.isDisabled() ? disabledStyle() : RETRO_NAV));
        b.setOnMousePressed(e -> b.setStyle(RETRO_NAV_PRESSED));
        b.setOnMouseReleased(e -> b.setStyle(RETRO_NAV));
        b.disabledProperty().addListener((o, a, dis) ->
                b.setStyle(dis ? disabledStyle() : RETRO_NAV));
    }

    private String disabledStyle() {
        return """
                -fx-background-color: linear-gradient(to bottom, #F1F4F8, #E0E6EE);
                -fx-background-radius: 6;
                -fx-border-color: #B9C5D4;
                -fx-border-radius: 6;
                -fx-border-width: 1;
                -fx-text-fill: #8A97A6;
                -fx-opacity: 0.7;
                """;
    }

    private void configure() {
        addressField.setText("");
        statusLabel.setText("Готово");

        backButton.setOnAction(e -> goBack());
        forwardButton.setOnAction(e -> goForward());
        refreshButton.setOnAction(e -> engine.reload());
        stopButton.setOnAction(e -> engine.getLoadWorker().cancel());
        homeButton.setOnAction(e -> goHome());
        maxButton.setOnAction(e -> engine.load("https://web.max.ru"));

        engine.locationProperty().addListener((o, old, url) -> {
            if (!addressField.isFocused()) {
                addressField.setText(url);
            }
            if (url != null) {
                updateNationalLabel(url);
                if (isRealUrl(url)) {
                    beginCheckSequence();
                } else {
                    hideSecurity();
                }
            }
        });

        engine.getLoadWorker().progressProperty().addListener((o, a, p) -> {
            if (p != null) {
                updateLoadProgress(p.doubleValue());
            }
        });

        engine.getLoadWorker().stateProperty().addListener((o, old, state) -> {
            switch (state) {
                case RUNNING -> {
                    statusLabel.setText("Загрузка...");
                }
                case SUCCEEDED -> {
                    statusLabel.setText("Готово");
                    lastFailedLoc = null;
                    if (isRealUrl(engine.getLocation())) {
                        resolveSecurity();
                        showLoaded();
                    } else {
                        hideLoadIndicator();
                        hideSecurity();
                    }
                }
                case FAILED -> {
                    hideLoadIndicator();
                    String loc = engine.getLocation();
                    if (loc != null && loc.startsWith("http") && !loc.equals(lastFailedLoc)) {
                        lastFailedLoc = loc;
                        statusLabel.setText("Не удалось загрузить страницу (пробуем ещё раз)...");
                        String attempt = loc;
                        Platform.runLater(() -> engine.load(attempt));
                    } else {
                        lastFailedLoc = null;
                        securityError();
                    }
                }
                case CANCELLED -> {
                    statusLabel.setText("Остановлено");
                    hideLoadIndicator();
                    securityCloseTimer.stop();
                    securityTimeout.stop();
                    adCheckDelay.stop();
                    interstitialTimer.stop();
                    adOverlay.setVisible(false);
                    securityOverlay.setVisible(false);
                    checkPending = false;
                    stopDots();
                }
                default -> { }
            }
            updateHistoryState();
        });

        history.currentIndexProperty().addListener((o, a, idx) -> updateHistoryState());
        history.getEntries().addListener(
                (javafx.collections.ListChangeListener<WebHistory.Entry>) c -> updateHistoryState());
        updateHistoryState();
    }

    private void navigate() {
        String text = addressField.getText().trim();
        if (text.isEmpty()) {
            goHome();
            return;
        }
        lastFailedLoc = null;
        boolean looksLikeUrl = text.contains("://")
                || text.startsWith("about:")
                || text.startsWith("data:")
                || (text.contains(".") && !text.contains(" "));
        if (looksLikeUrl) {
            if (!text.contains("://") && !text.startsWith("about:") && !text.startsWith("data:")) {
                text = "https://" + text;
            }
            engine.load(text);
        } else {
            String q = URLEncoder.encode(text, StandardCharsets.UTF_8);
            engine.load("https://yandex.ru/search/?text=" + q);
        }
    }

    private void goBack() {
        int idx = history.getCurrentIndex();
        if (idx > 0) {
            history.go(-1);
        }
    }

    private void goForward() {
        int idx = history.getCurrentIndex();
        if (idx < history.getEntries().size() - 1) {
            history.go(1);
        }
    }

    private void goHome() {
        lastFailedLoc = null;
        nationalLabel.setVisible(false);
        hideSecurity();
        hideLoadIndicator();
        engine.loadContent(HOME_HTML, "text/html");
    }

    private void updateHistoryState() {
        int idx = history.getCurrentIndex();
        backButton.setDisable(idx <= 0);
        forwardButton.setDisable(idx >= history.getEntries().size() - 1);
    }

    private boolean isForeign(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) {
                return false;
            }
            String h = host.toLowerCase();
            for (String d : FOREIGN) {
                if (h.equals(d) || h.endsWith("." + d)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return false;
    }

    private void beginCheckSequence() {
        securityCloseTimer.stop();
        securityTimeout.stop();
        adCheckDelay.stop();
        interstitialTimer.stop();
        adOverlay.setVisible(false);
        securityOverlay.setVisible(false);
        checkPending = false;
        securityResolved = false;
        if (Math.random() < AD_PROB) {
            showInterstitial();
        } else {
            adCheckDelay.getKeyFrames().setAll(
                    new KeyFrame(Duration.millis(250), e -> showSecurityChecking()));
            adCheckDelay.play();
        }
    }

    private void showInterstitial() {
        int i = (int) (Math.random() * ADS.length);
        String[] ad = ADS[i];
        adUrl = ad[3];
        String c = ad[2];
        interLogo.setText(String.valueOf(ad[0].charAt(0)).toUpperCase());
        interLogo.setStyle("-fx-background-color: " + c + "; -fx-background-radius: 26;");
        interTitle.setText(ad[0]);
        interSub.setText(ad[1]);
        interOpen.setText("Открыть " + ad[0]);
        interSkip.setDisable(true);
        adOverlay.setVisible(true);
        interstitialTimer.stop();
        interstitialTimer.getKeyFrames().setAll(
                new KeyFrame(Duration.millis(2500), e -> interSkip.setDisable(false)),
                new KeyFrame(Duration.millis(9000), e -> finishInterstitial()));
        interstitialTimer.play();
    }

    private void showSecurityChecking() {
        if (securityResolved) {
            return;
        }
        securityCloseTimer.stop();
        startDots();
        checkPending = true;
        securityIcon.setImage(loadImage("assets/applet_error.png"));
        securityTitle.setText("Проверка безопасности");
        securityTitle.setTextFill(Color.web("#0B2E59"));
        securityMsg.setText("Загружаем страницу и проверяем, всё ли в порядке...");
        securityAddress.setText(hostOf(engine.getLocation()));
        securityDots.setVisible(true);
        securityActions.setVisible(false);
        securityBack.setVisible(false);
        securityOverlay.setVisible(true);

        securityTimeout.stop();
        if (engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            securityTimeout.getKeyFrames().setAll(
                    new KeyFrame(Duration.millis(600), e -> resolveSecurity()));
        } else {
            securityTimeout.getKeyFrames().setAll(
                    new KeyFrame(Duration.seconds(8), e -> resolveSecurity()));
        }
        securityTimeout.play();
    }

    private void resolveSecurity() {
        if (!checkPending) {
            return;
        }
        checkPending = false;
        securityTimeout.stop();
        adCheckDelay.stop();
        interstitialTimer.stop();
        adOverlay.setVisible(false);
        stopDots();
        securityResolved = true;
        if (isForeign(engine.getLocation())) {
            securityBlocked();
        } else {
            securityOk();
        }
    }

    private void securityOk() {
        securityIcon.setImage(loadImage("assets/applet_okay.png"));
        securityTitle.setText("Всё хорошо");
        securityTitle.setTextFill(Color.web("#1E7A3E"));
        securityMsg.setText("Проверка пройдена. Сайт безопасен.");
        securityDots.setVisible(false);
        securityActions.setVisible(false);
        securityCloseTimer.getKeyFrames().setAll(
                new KeyFrame(Duration.millis(1300), e -> securityOverlay.setVisible(false)));
        securityCloseTimer.play();
    }

    private void securityBlocked() {
        securityIcon.setImage(loadImage("assets/applet_critical.png"));
        securityTitle.setText("ВНИМАНИЕ");
        securityTitle.setTextFill(Color.web("#C5262E"));
        securityMsg.setText("Вы пытаетесь зайти на не русский сайт.\nМы не можем вас пропустить.");
        securityAddress.setText(hostOf(engine.getLocation()));
        securityDots.setVisible(false);
        securityActions.setVisible(true);
        securityAnalog.setVisible(findAnalog(engine.getLocation()) != null);
        securityBack.setVisible(true);
    }

    private void securityError() {
        checkPending = false;
        securityTimeout.stop();
        adCheckDelay.stop();
        interstitialTimer.stop();
        adOverlay.setVisible(false);
        stopDots();
        securityResolved = true;
        securityIcon.setImage(loadImage("assets/applet_critical.png"));
        securityTitle.setText("ВНИМАНИЕ");
        securityTitle.setTextFill(Color.web("#C5262E"));
        securityMsg.setText("Не удалось загрузить страницу.");
        securityAddress.setText(hostOf(engine.getLocation()));
        securityDots.setVisible(false);
        securityActions.setVisible(true);
        securityAnalog.setVisible(false);
        securityBack.setVisible(true);
    }

    private void openAnalog() {
        String analog = findAnalog(engine.getLocation());
        hideSecurity();
        if (analog != null) {
            engine.load(analog);
        } else {
            goHome();
        }
    }

    private String findAnalog(String url) {
        try {
            URI uri = new URI(url);
            String h = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
            for (String[] a : FOREIGN_ANALOG) {
                if (h.equals(a[0]) || h.endsWith("." + a[0])) {
                    return a[1];
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private void updateNationalLabel(String url) {
        if (url == null || !isRealUrl(url)) {
            nationalLabel.setVisible(false);
            return;
        }
        nationalLabel.setVisible(true);
        if (isRussian(url)) {
            nationalLabel.setText("Российский сайт");
            nationalLabel.setTextFill(Color.web("#1E7A3E"));
        } else {
            nationalLabel.setText("Иностранный сайт");
            nationalLabel.setTextFill(Color.web("#C5262E"));
        }
    }

    private boolean isRussian(String url) {
        try {
            URI uri = new URI(url);
            String h = uri.getHost() == null ? "" : uri.getHost().toLowerCase();
            if (h.isEmpty()) {
                return false;
            }
            if (h.endsWith(".ru") || h.endsWith(".su")
                    || h.endsWith(".xn--p1ai") || h.endsWith(".рф")) {
                return true;
            }
            for (String d : RUSSIAN_DOMAINS) {
                if (h.equals(d) || h.endsWith("." + d)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return false;
    }

    private void hideSecurity() {
        securityCloseTimer.stop();
        securityTimeout.stop();
        adCheckDelay.stop();
        interstitialTimer.stop();
        adOverlay.setVisible(false);
        checkPending = false;
        securityResolved = false;
        stopDots();
        securityOverlay.setVisible(false);
    }

    private void startDots() {
        if (securityDotsTimer == null) {
            securityDotsTimer = new AnimationTimer() {
                private long startNanos = -1;

                @Override
                public void handle(long now) {
                    if (startNanos < 0) {
                        startNanos = now;
                    }
                    double t = (now - startNanos) / 1_000_000_000.0;
                    for (int i = 0; i < securityCircles.length; i++) {
                        double phase = ((t * 0.9) + i * 0.3333) % 1.0;
                        double s = 0.5 + 0.5 * Math.sin(phase * Math.PI);
                        securityCircles[i].setScaleX(s);
                        securityCircles[i].setScaleY(s);
                        securityCircles[i].setOpacity(s * 0.6 + 0.4);
                    }
                }
            };
        }
        securityDotsTimer.start();
    }

    private void stopDots() {
        if (securityDotsTimer != null) {
            securityDotsTimer.stop();
        }
    }

    private void loadDiscImages() {
        for (int i = 0; i <= 20; i++) {
            int n = i * 5;
            String tag = n == 0 ? "00" : (n < 10 ? "0" + n : String.valueOf(n));
            discImages[i] = loadImage("assets/brasero_disc_" + tag + ".png");
        }
    }

    private void updateLoadProgress(double p) {
        int idx = (int) Math.round(p * 20);
        if (idx < 0) {
            idx = 0;
        }
        if (idx > 20) {
            idx = 20;
        }
        Image img = discImages[idx];
        if (img != null) {
            loadIndicator.setImage(img);
            loadIndicator.setVisible(true);
        }
    }

    private void showLoaded() {
        loadDoneTimer.stop();
        Image ok = loadImage("assets/dialog_apply.png");
        if (ok != null) {
            loadIndicator.setImage(ok);
        }
        loadIndicator.setVisible(true);
        loadDoneTimer.getKeyFrames().setAll(
                new KeyFrame(Duration.millis(1300), e -> loadIndicator.setVisible(false)));
        loadDoneTimer.play();
    }

    private void hideLoadIndicator() {
        loadDoneTimer.stop();
        loadIndicator.setVisible(false);
    }

    private boolean isRealUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    private void dismissBlock() {
        securityOverlay.setVisible(false);
        stopDots();
        checkPending = false;
        if (history.getCurrentIndex() > 0) {
            goBack();
        } else {
            goHome();
        }
    }

    private void showAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("О браузере");
        a.setHeaderText("Алешенька");
        a.setContentText("Русский национальный браузер.\nСделан на Java и JavaFX.");
        a.showAndWait();
    }

    private static String homeHtml() {
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                <meta charset="UTF-8">
                <title>Алешенька — Новая вкладка</title>
                <style>
                  html, body { height: 100%; margin: 0; font-family: "Segoe UI", Arial, sans-serif;
                               background: linear-gradient(to bottom, #f7f9fc, #dbe4f0); }
                  .center { height: 100%; display: flex; flex-direction: column;
                            align-items: center; justify-content: center; text-align: center; }
                  h1 { font-size: 58px; margin: 0 0 6px 0; letter-spacing: 1px; text-shadow: 1px 1px 0 #ffffff; }
                  .sub { color: #566985; font-size: 17px; margin-bottom: 26px; }
                  .tri { height: 8px; width: 260px; border-radius: 4px; overflow: hidden;
                         box-shadow: 1px 1px 0 #ffffff; margin-bottom: 28px; }
                  .tri div { float: left; height: 100%; }
                  .w { width: 33.3%; background: #fff; }
                  .b { width: 33.3%; background: #0039A6; }
                  .r { width: 33.4%; background: #D52B1E; }
                  form { width: 560px; }
                  input[type=text] { width: 100%; box-sizing: border-box; font-size: 20px; padding: 12px 16px;
                      border: 1px solid #8598ad; background: #fff; border-radius: 3px;
                      box-shadow: inset 1px 1px 3px rgba(0,0,0,.15); }
                  input[type=submit] { margin-top: 14px; font-size: 16px; padding: 8px 26px;
                      background: linear-gradient(to bottom, #fff, #dbe7f3);
                      border: 1px solid #7e9dc0; border-radius: 4px; cursor: pointer; }
                  input[type=submit]:hover { background: linear-gradient(to bottom, #f2f8ff, #c7dcf3); }
                  .note { margin-top: 22px; color: #6d7f96; font-size: 13px; }
                </style>
                </head>
                <body>
                <div class="center">
                  <h1><span style="color:#fff;text-shadow:0 0 1px #999">А</span><span style="color:#0039A6">Л
                  </span><span style="color:#D52B1E">Е</span><span style="color:#3a4a5c">ШЕНЬКА</span></h1>
                  <div class="sub">русский национальный браузер</div>
                  <div class="tri"><div class="w"></div><div class="b"></div><div class="r"></div></div>
                  <form action="https://yandex.ru/search/" method="get" target="_top">
                    <input type="text" name="text" placeholder="Поиск в Яндексе...">
                    <input type="submit" value="Найти">
                  </form>
                  <div class="note">Просто введите адрес сайта сверху, например yandex.ru</div>
                </div>
                </body>
                </html>
                """;
    }

    private static String toDataUrl(String css) {
        String e = css.replace("\n", " ").replace("\"", "%22").replace("'", "%27");
        return "data:text/css," + e;
    }
}