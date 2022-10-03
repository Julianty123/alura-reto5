import com.sun.javafx.runtime.VersionInfo;
import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// https://stackoverflow.com/questions/24587342/how-to-animate-lineargradient-on-javafx
public class Main extends Application implements Initializable {
    public double xTarget, yTarget; // Mouse position
    public AnchorPane rootMain, rootInput, rootMonedas, rootMensaje, rootSelectAnOption, rootTerminado, rootTemperatura;
    public ChoiceBox<String> choiceBoxMain, choiceBoxMonedas;
    public static TextField inputMoneda;
    public Text txtConversion;
    private static String varUserInput; // La palabra reservada static me permite usarlo en otros frames

    @Override
    public void initialize(URL location, ResourceBundle resources) { // Para inicializar el ChoiceBox (Tambien se puede con TableView y otros controles)
        try{
            choiceBoxMain.getItems().addAll("Conversor de Moneda", "Conversor de Temperatura");
            choiceBoxMain.setValue("Conversor de Moneda");

        }catch (Exception ignored){} // Evita una excepcion cuando cambio de frame (Ventana con FXML)

        try{
            choiceBoxMonedas.getItems().addAll("De Pesos a Dólar", "De pesos a Euro",
                    "De pesos a Libra", "De pesos a Yen", "De pesos a Won Coreano", "De Dólar a Pesos",
                    "De Euro a Pesos", "De Libra a Pesos", "De Yen a Pesos", "De Won Coreano a Pesos"); // "De pesos a Yuan"
            choiceBoxMonedas.setValue("De Pesos a Dólar");
        }catch (Exception ignored){}
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
        rootMain = loader.load();

        primaryStage.setScene(new Scene(rootMain, rootMain.getPrefWidth(), rootMain.getPrefHeight())); // Inicia con el tamaño del .FXML
        primaryStage.initStyle(StageStyle.TRANSPARENT); // HIDES default close, minimize and maximize window buttons
        primaryStage.getScene().setFill(Color.TRANSPARENT); // Very important!
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

        startListeners(rootMain);
        startAnimation(rootMain); // Justo cuando muestra la ventana inicia la animación
    }

    private void startListeners(Parent root){
        root.setOnMousePressed(event -> {
            xTarget = event.getSceneX();
            yTarget = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            root.getScene().getWindow().setX(event.getScreenX() - xTarget);
            root.getScene().getWindow().setY(event.getScreenY() - yTarget);
        });
    }

    private void startAnimation(Parent root) {
        System.out.println("java version: "+ System.getProperty("java.version"));
        System.out.println("javafx.version: " + System.getProperty("javafx.version"));
        System.out.println(("JavaFX Runtime Version: " + VersionInfo.getRuntimeVersion()));

        ObjectProperty<Color> baseColor = new SimpleObjectProperty<>();

        KeyValue keyValue1 = new KeyValue(baseColor, Color.valueOf("#222222"));
        KeyValue keyValue2 = new KeyValue(baseColor, Color.valueOf("#d58512"));
        KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO, keyValue1);
        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(5000), keyValue2);
        Timeline timeline = new Timeline(keyFrame1, keyFrame2);

        AtomicInteger line1 = new AtomicInteger(0); // O%
        AtomicInteger line2 = new AtomicInteger(30); // 30%
        AtomicBoolean derecha1 = new AtomicBoolean(true);
        AtomicBoolean derecha2 = new AtomicBoolean(true);

        baseColor.addListener((obs, oldColor, newColor) -> {

            if (line1.get() < 1) {derecha1.set(true);}
            if (line1.get() > 99) {derecha1.set(false);}
            if (line2.get() < 1) {derecha2.set(true);}
            if (line2.get() > 99) {derecha2.set(false);}

            if (derecha1.get()) {line1.getAndIncrement();}
            if (!derecha1.get()) {line1.getAndDecrement();}
            if (derecha2.get()) {line2.getAndIncrement();}
            if (!derecha2.get()) {line2.getAndDecrement();}

            // anchorPane.setBackground(new Background(new BackgroundFill(newColor, CornerRadii.EMPTY, Insets.EMPTY)));
            // anchorPane.setStyle(String.format("-fx-base: #%02x%02x%02x; ",(int)(newColor.getRed()*255),(int)(newColor.getGreen()*255),(int)(newColor.getBlue()*255)));
            root.setStyle(String.format(
                    "-fx-background-color: linear-gradient(to right bottom, #d58512, #222222 %s%%, #d58512, #222222 %s%%, #d58512);", line1, line2));
        });

        // timeline.setAutoReverse(true); or timeline.autoReverseProperty().set(true);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        /*Timeline timeline = new Timeline();
        String[] strings = {
                "-fx-background-color: linear-gradient(to right bottom, #d58512, #222222, #d58512, #222222, #d58512)",
                "-fx-background-color: linear-gradient(to right bottom, #d58512, #d58512, #222222, #d58512, #222222)",
                "-fx-background-color: linear-gradient(to right bottom, #222222, #d58512, #d58512, #222222, #d58512)",
                "-fx-background-color: linear-gradient(to right bottom, #d58512, #222222, #d58512, #d58512, #222222)",
                "-fx-background-color: linear-gradient(to right bottom, #222222, #d58512, #222222, #d58512, #d58512)"
        };
        for(int i = 0; i < strings.length; i++) {
            int finalI = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i), (ActionEvent event) -> {
                anchorPane.setStyle(strings[finalI]);
            }));
        }*/

        /*timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), (ActionEvent event) -> {
            anchorPane.setStyle(strings[0]);
        }));
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), (ActionEvent event) -> {
            anchorPane.setStyle(strings[1]);
        }));*/

        /*Color[] colors = Stream.of("#222222", "black", "#d58512", "#ffa100")
                .map(Color::web)
                .toArray(Color[]::new);

        int[] mills = {-200};
        KeyFrame[] keyFrames = Stream.iterate(0, i -> i+1)
                .limit(100)
                .map(i -> new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE, new Stop(0, colors[i%colors.length]), new Stop(1, colors[(i+1)%colors.length])))
                .map(lg -> new Border(new BorderStroke(lg, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(2))))
                .map(b -> new KeyFrame(Duration.millis(mills[0]+=200), new KeyValue(anchorPane.borderProperty(), b, Interpolator.EASE_IN)))
                .toArray(KeyFrame[]::new);

        Timeline timeline = new Timeline(keyFrames);
        */
    }

    public void clickToClose(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();    // Button button = (Button) mouseEvent.getSource();
        stage.close();
    }

    public void clickToMinimize(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node)mouseEvent.getSource()).getScene().getWindow();
        stage.setIconified(true);   // rootMain.requestFocus();
    }

    public void handleButtonVamos(ActionEvent actionEvent) throws IOException {
        if(choiceBoxMain.getValue().equals("Conversor de Moneda")) {
            changeRoot(rootMain, "Input.fxml");
        }
        else if(choiceBoxMain.getValue().equals("Conversor de Temperatura")){
            changeRoot(rootMain, "Temperatura.fxml");
        }
    }

    public void actionAceptarInput() throws IOException {
        if(!inputMoneda.getText().isEmpty()) {
            changeRoot(rootInput, "Monedas.fxml");
        }
    }

    public void actionCancelarInput() throws IOException {
        changeRoot(rootInput, "Main.fxml");
    }

    public void actionAceptarMonedas() throws IOException {
        changeRoot(rootMonedas, "Mensaje.fxml");
    }

    public void actionCancelarMonedas() throws IOException {
        changeRoot(rootMonedas, "Input.fxml");
    }

    public void actionAceptarMensaje() throws IOException {
        changeRoot(rootMensaje, "SelectAnOption.fxml");
    }

    public void actionSiSelect() throws IOException {
        changeRoot(rootSelectAnOption, "Main.fxml");
    }

    public void actionNoSelect() throws IOException {
        changeRoot(rootSelectAnOption, "Terminado.fxml");
    }

    public void actionAceptarTerminado(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void actionVolverTemperatura() throws IOException {
        changeRoot(rootTemperatura, "Main.fxml");
    }

    public void changeRoot(Parent currentRoot, String nameFXMLToRoot) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(nameFXMLToRoot));
        Parent toRoot = loader.load(); // Le asigna ese .fxml al Parent
        currentRoot.getScene().setRoot(toRoot); // Cambia la escena
        // rootMain.getScene().setRoot(FXMLLoader.load(((Objects.requireNonNull(getClass().getResource("Input.fxml")))))); // Otra forma
        startAnimation(toRoot);  startListeners(toRoot);

        if(toRoot.getId().equals("rootInput")) {
            inputMoneda = (TextField) toRoot.lookup("#inputMoneda");

            // Se llama cuando el contenido del TextField cambia
            inputMoneda.textProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue.matches("\\d*")) {  // "[0-9]+"
                    varUserInput = newValue;
                }
                else {
                    inputMoneda.setText(oldValue);
                }
            });
        }
        else if(toRoot.getId().equals("rootMensaje")) {
            txtConversion = (Text) toRoot.lookup("#txtConversion");
            switch (choiceBoxMonedas.getValue()) {
                case "De Pesos a Dólar":
                    float varDolar = Float.parseFloat(varUserInput) * 0.00022f;
                    txtConversion.setText(varUserInput + " Pesos = " + varDolar + " Dólares");
                    break;
                case "De pesos a Euro":
                    float varEuro = Float.parseFloat(varUserInput) * 0.00023f;
                    txtConversion.setText(varUserInput + " Pesos = " + varEuro + " Euros");
                    break;
                case "De pesos a Libra":
                    float varLibra = Float.parseFloat(varUserInput) * 0.00020f;
                    txtConversion.setText(varUserInput + " Pesos = " + varLibra + " Libras Esterlinas");
                    break;
                case "De pesos a Yen":
                    float varYen = Float.parseFloat(varUserInput) * 0.032f;
                    txtConversion.setText(varUserInput + " Pesos = " + varYen + " Yenes");
                    break;
                case "De pesos a Won Coreano":
                    float varWon = Float.parseFloat(varUserInput) * 0.32f;
                    txtConversion.setText(varUserInput + " Pesos = " + varWon + " Won Coreanos");
                    break;
                case "De Dólar a Pesos":
                    float varDolaraPesos = Float.parseFloat(varUserInput) * 4610.01f;
                    txtConversion.setText(varUserInput + " Dólares = " + varDolaraPesos + " Pesos Colombianos");
                    break;
                case "De Euro a Pesos":
                    float varEuroaPesos = Float.parseFloat(varUserInput) * 4518.32f;
                    txtConversion.setText(varUserInput + " Dólares = " + varEuroaPesos + " Pesos Colombianos");
                    break;
                case "De Libra a Pesos":
                    float varLibraaPesos = Float.parseFloat(varUserInput) * 5158.83f;
                    txtConversion.setText(varUserInput + " Dólares = " + varLibraaPesos + " Pesos Colombianos");
                    break;
                case "De Yen a Pesos":
                    float varYenaPesos = Float.parseFloat(varUserInput) * 31.81f;
                    txtConversion.setText(varUserInput + " Dólares = " + varYenaPesos + " Pesos Colombianos");
                    break;
                case "De Won Coreano a Pesos":
                    float varWonCoreanoaPesos = Float.parseFloat(varUserInput) * 3.20f;
                    txtConversion.setText(varUserInput + " Dólares = " + varWonCoreanoaPesos + " Pesos Colombianos");
                    break;
            }
        }
    }
}
