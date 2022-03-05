package com.metait.javafxemailclient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class JavaFxEmailClientApplication extends Application {

    private VBox vboxPassWord = null;
    private TextField passWord = null;
    private PasswordField pWField = null;
    private TextField pWTextField = null;
    private CheckBox checkBoxPassWord = null;

    @Override
    public void start(Stage stage) throws IOException {

        final String from = "";
        final String username = "";
        final String password = "";

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.user", "username");
        props.put("mail.smtp.password", "password");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", true);

        Session session = Session.getInstance(props, new  javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Group root = new Group();
        Scene scene = new Scene(root, 410, 550);
        stage.setScene(scene);
        stage.setTitle("Mail Sender");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(15);
        grid.setHgap(15);

        scene.setRoot(grid);

        VBox vbox = null;

// "To" Part
        TextField userName = new TextField();
        userName.setPromptText("username");
        TextField fromAdress = new TextField();
        fromAdress.setPromptText("From");
        TextField toadress = new TextField();
        toadress.setPromptText("To");
        toadress.setPrefColumnCount(40);

        pWField = new PasswordField();
        pWTextField = new TextField();
        pWField.setPromptText("password");
        pWTextField.setPromptText("password");
        passWord = pWField;

        vbox = getLabelAndControl("Username", userName);
        GridPane.setConstraints(vbox, 0, 0);
        grid.getChildren().add(vbox);

        vboxPassWord = getLabelAndControl("Password", passWord);
        GridPane.setConstraints(vboxPassWord, 0, 1);
        checkBoxPassWord = new CheckBox();
        checkBoxPassWord.setText("Show password chars");
        vboxPassWord.getChildren().add(checkBoxPassWord);

        checkBoxPassWord.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue ov,Boolean old_val, Boolean new_val) {
                if (new_val)
                {
                    // passWord.setPromptText(pWField.getPromptText());
                    pWTextField.setText(pWField.getText());
                    passWord = pWTextField;
                    vboxPassWord.getChildren().set(1, passWord);
                }
                else
                {
                    // passWord.setPromptText(pWField.getPromptText());
                    pWField.setText(pWTextField.getText());
                    passWord = pWField;
                    vboxPassWord.getChildren().set(1, passWord);
                }
            }
        });

        grid.getChildren().add(vboxPassWord);

        vbox = getLabelAndControl("From", fromAdress);
        GridPane.setConstraints(vbox, 0, 2);
        grid.getChildren().add(vbox);

        vbox = getLabelAndControl("To", toadress);
        GridPane.setConstraints(vbox, 0, 3);
        grid.getChildren().add(vbox);

// "Subject" Part
        TextField subject = new TextField();
        subject.setPromptText("Enter Subject");
        subject.setPrefColumnCount(20);
        subject.setPrefHeight(20);
        vbox = getLabelAndControl("Subject", subject);
        GridPane.setConstraints(vbox, 0, 4);
        grid.getChildren().add(vbox);

        // "Body" Part
        TextArea body = new TextArea();
        body.setPrefRowCount(20);
        body.setPrefColumnCount(100);
        body.setWrapText(true);
        body.setPrefWidth(250);
        body.setPromptText("Some text");
        String cssDefault = "";
        body.setText(cssDefault);
        vbox = getLabelAndControl("Body", body);
        GridPane.setConstraints(vbox, 0, 5);
        grid.getChildren().add(vbox);

        // Button Part
        Button btn = new Button();
        grid.getChildren().add(btn);
        btn.setText("Send");
        GridPane.setConstraints(btn, 0, 6);
        GridPane.setHalignment(btn, HPos.RIGHT);

        stage.show();

        /*
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DropShadow shadow = new DropShadow();
                btn.setEffect(shadow);
                stage.setScene(scene);

                String text = toadress.getText();
                String text1 = subject.getText();
                String text2 = body.getText();
                stage.show();
                try {
                    // Create a default MimeMessage object.
                    Message message = new MimeMessage(session);

                    // Set From: header field of the header.
                    message.setFrom(new InternetAddress(from));

                    // Set To: header field of the header.
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(text));

                    // Set Subject: header field
                    message.setSubject(text1);

                    // Now set the actual message
                    message.setText(text2);

                    // Send message
                    Transport.send(message);
                    System.out.println("Sent message successfully.");
                } catch (MessagingException e) {
                    System.out.println("Sent message failed.");
                    e.printStackTrace();
                }
            }
        });
         */
    }
        /*
        FXMLLoader fxmlLoader = new FXMLLoader(JavaFxEmailClientApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
         */

    public static void main(String[] args) {
        launch();
    }

    private VBox getLabelAndControl(String labelText, Control control)
    {
        VBox ret = new VBox();
        ret.getChildren().add(new Label(labelText));
        ret.getChildren().add(control);
        return ret;
    }
}