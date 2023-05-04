package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

import java.net.URL;
import java.net.URLEncoder;
import java.net.URI;
import java.net.HttpURLConnection;
import java.io.InputStream;


import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;

import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Priority;

import javafx.scene.layout.HBox;
import java.util.Scanner;
import com.google.gson.annotations.SerializedName;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    Stage stage;
    Scene scene;
    VBox root;
    HBox dropDownContainer;
    VBox top;
    VBox bottom;
    VBox info;
    Label dropDownLabel;
    Label location = new Label ("Location: ");
    Label state = new Label ("State: ");
    Label sunrise = new Label ("Sunrise: ");
    Label sunset = new Label ("Sunset: ");
    Label solarNoon = new Label ("Solar Noon: ");
    Label dayLength = new Label ("Day Length: ");
    private float longi;
    private float lat;

    private Button getSunrise = new Button("Go!");
    String stringTwo = "Find the sunrise,sunset,solar noon, ";
    String stringThree = stringTwo + "and day length of the top 10 sunrise/sunset views in the US!";
    private Label intro  = new Label(stringThree);
    private Label title  = new Label("Sunshine Statistics");
    String string = "Select a zip code from the dropdown, and then click the go button.";
    private Label label  = new Label (string);
    private static final String ZIPPOPOTAM_API = "http://api.zippopotam.us/us/";
    private static final String SUN_API = "https://api.sunrise-sunset.org/json?lat=";
    private String uri;
    private ApiResult apiResult;
    private ImageView banner;
    Label timeNote = new Label ("** NOTE: All times are provided in UTC! **");

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .create();

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        root = new VBox();
        top = new VBox();
        info = new VBox();
        bottom = new VBox();
        dropDownContainer = new HBox();
        dropDownLabel = new Label("Choose a zip code: ");
        banner = new ImageView(new Image("rising-sun-facebook-cover.jpg"));
        banner.setFitWidth(750);
        banner.setFitHeight(300);
        banner.setPreserveRatio(true);
//        scene.setHeight(720);
    } // ApiApp

    private ComboBox<String> zipCodeOptionsOne = new ComboBox<String>();
    //private ComboBox<String> zipCodeOptionsTwo = new ComboBox<String>();

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        Platform.runLater(() -> this.stage.setResizable(false));

        // some labels to display information
//        Label notice = new Label("Modify the starter code to suit your needs.");
        scene = new Scene(root, 750, 480);

        // setup stage
        stage.setTitle("Sunshine Statistics");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
        top.setAlignment(Pos.CENTER);
        bottom.setAlignment(Pos.CENTER);
        VBox.setVgrow(bottom, Priority.ALWAYS);
        dropDownContainer.setAlignment(Pos.CENTER);
//        timeNote.setAlignment(Pos.CENTER_LEFT);
//        timeNote.setAlignment(Pos.BOTTOM_CENTER);
        root.setStyle("-fx-background-color: lightblue;");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        timeNote.setTextFill(Color.RED);
        title.setTextFill(Color.DARKSLATEBLUE);
//        scene.setHeight(720);
    } // start

    /** {@inheritDoc} */
    @Override
    public void init() {
        HBox.setHgrow(dropDownContainer, Priority.ALWAYS);
        this.dropDownContainer.getChildren().addAll
            (dropDownLabel, zipCodeOptionsOne, getSunrise);
        this.top.getChildren().addAll(title, intro, label);
        root.getChildren().addAll(top, banner, dropDownContainer, info, bottom);
        bottom.getChildren().addAll(timeNote);
        info.getChildren().addAll(location, state, sunrise, sunset, solarNoon, dayLength);
        zipCodeOptionsOne.setPrefWidth(210);
//        zipCodeOptionsTwo.setPrefWidth(210);
        dropDownTypes();
        Runnable task = () -> {
            getSunrise.setDisable(true);
            ApiResponce apiResponce = getLocation();
            getSunrise.setDisable(false);
            SunResponse sunResponse = getSun();
        };
        EventHandler<ActionEvent> getLocationHandler = event -> runNow(task);
        getSunrise.setOnAction(getLocationHandler);
//        Label sunrise = new Label ("Sunrise: " + sunriseValue);
    }

/**
 * This method sets the different zip code options in the dropdown.
 */
    public void dropDownTypes() {
        zipCodeOptionsOne.getItems().add("33040");
//        zipCodeOptionsOne.getItems().add("V6T 1Z4");
        //      zipCodeOptionsOne.getItems().add("110001");
        zipCodeOptionsOne.getItems().add("93108");
        zipCodeOptionsOne.getItems().add("96790");
        zipCodeOptionsOne.getItems().add("87501");
        zipCodeOptionsOne.getItems().add("92651");
        zipCodeOptionsOne.getItems().add("86023");
        zipCodeOptionsOne.getItems().add("33131");
        zipCodeOptionsOne.getItems().add("90401");
        zipCodeOptionsOne.getItems().add("02568");
        zipCodeOptionsOne.getItems().add("84103");
        zipCodeOptionsOne.setValue("33040");
    }

    private String stateName;

/**
 * This method uses and retrieves the necessary information from the first API.
 * @return APIResponce gives the state and coordinates of the location.
 */
    public ApiResponce getLocation() {
        ApiResponce apiResponce = new ApiResponce();
        try {
//            apiResponce = new ApiResponce();
            String postalCode = URLEncoder.encode(zipCodeOptionsOne.getValue(),
                StandardCharsets.UTF_8);
            uri = ZIPPOPOTAM_API + postalCode;
            HttpRequest userRequest = HttpRequest.newBuilder().uri(URI.create(uri)).build();
            HttpResponse<String> systemResponse = HTTP_CLIENT
                .send(userRequest, BodyHandlers.ofString());
            if (systemResponse.statusCode() != 200) {
                throw new IOException(systemResponse.toString());
            }
            String json = systemResponse.body();
            apiResponce = GSON.fromJson(json, cs1302.api.ApiResponce.class);
            ApiResult[] places = apiResponce.places;
//            System.out.println("testing new stuff");
            if (places.length > 0) {
                ApiResult place = places[0];
                System.out.println("Latitude: " + place.latitude);
                System.out.println("State: " + place.state);
                stateName = place.state;
                lat = Float.parseFloat(place.latitude);
                System.out.println("Longitude: " + place.longitude);
                longi = Float.parseFloat(place.longitude);
//                System.out.println(lat + longi);
            }
            System.out.println(uri);
            System.out.println();
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong...try again!");
            System.out.println(e);
        }
        return apiResponce;
    }

    /**
     * This class retrieves the results gathered.
     * @return SunResponse this returns all the information for the app.
     */
    public class SunResponse {
        public Results results;
    }

    /**
     * This class gets the result values,\.
     */
    public class Results {
        @SerializedName("solar_noon") String solarNoon;
        @SerializedName("day_length") String dayLength;
        public String sunrise;
        public String sunset;
//        @SerializedName("solar_noon");
//        public String solarNoon;
        //      @SerializedName("day_length");
        //      public String dayLength;
        /**  public string civil_twilight_begin;
        public string civil_twilight_end;
        public string nautical_twilight_begin;
        public string nautical_twilight_end;
        public string astronomical_twilight_begin;
        public string astronomical_twilight_end;
        */
    }

    private String sunriseValue;
    private String sunsetValue;
    private String solarNoonValue;
    private String dayLengthValue;

//    private String dayLengthValue;
    private ZonedDateTime sunriseTime;
    private ZonedDateTime sunsetTime;

    /**
     * This method finds the sunrise, sunset, solar noon, and day length times from the API.
     * @return sunResponse gets all the information from the API.
     */
    public SunResponse getSun() {
        SunResponse sunResponse = new SunResponse();
        try {
            //String sun = URLEncoder.encode(results.getValue(), StandardCharsets.UTF_8);
            uri = SUN_API + lat + "&lng=" + longi + "&date=today";
            System.out.println(uri);
            HttpRequest userRequest = HttpRequest.newBuilder().uri(URI.create(uri)).build();
            HttpResponse<String> systemResponse = HTTP_CLIENT
                .send(userRequest, BodyHandlers.ofString());
            if (systemResponse.statusCode() != 200) {
                throw new IOException(systemResponse.toString());
            }
            String json = systemResponse.body();
            sunResponse = GSON.fromJson(json,SunResponse.class);
            sunriseValue = sunResponse.results.sunrise;
            System.out.println("Sunrise: " + sunriseValue);
            sunsetValue = sunResponse.results.sunset;
            System.out.println("Sunset: " + sunsetValue);
            solarNoonValue = sunResponse.results.solarNoon;
            System.out.println("Solar Noon: " + solarNoonValue);
            dayLengthValue = sunResponse.results.dayLength;
            System.out.println("Day Length: " + dayLengthValue);
            Platform.runLater(() -> location.setText("Location: (" + longi + ", " + lat + ")"));
            Platform.runLater(() -> state.setText("State: " + stateName));
            Platform.runLater(() -> sunrise.setText("Sunrise: " + sunriseValue));
            Platform.runLater(() -> sunset.setText("Sunset: " + sunsetValue));
            Platform.runLater(() -> solarNoon.setText("Solar Noon: " + solarNoonValue));
            Platform.runLater(() -> dayLength.setText("Day Length: " + dayLengthValue));
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong...try again!");
            System.out.println(e);
        }
        return sunResponse;
    }

    /**
     * This method runs the app by executing the given Runnable object in a new daemon thread.
     * @param tester the Runnable object to be executed in the new thread
     */
    public static void runNow(Runnable tester) {
        Thread t = new Thread(tester);
        t.setDaemon(true);
        t.start();
    }
} // ApiApp
