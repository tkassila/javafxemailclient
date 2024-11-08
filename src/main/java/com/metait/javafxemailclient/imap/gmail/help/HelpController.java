package com.metait.javafxemailclient.imap.gmail.help;

import com.metait.javafxemailclient.imap.gmail.BiggestEmailCounter;
import com.metait.javafxemailclient.imap.gmail.CountAndListEmailsOfAllGmailMessages;
import com.metait.javafxemailclient.imap.gmail.EmailAddressCounter;
import com.metait.javafxemailclient.imap.gmail.GmailSessionReturn;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.mail.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class HelpController {

    @FXML
    private Button buttonHelpClose;
    @FXML
    private WebView webViewHelp;


    @FXML
    public void initialize() {
        WebEngine webEngine = webViewHelp.getEngine();
        webEngine.load(getClass().getResource("help.html").toString());
    }

    @FXML
    public void pressedButtonHelpClose()
    {
        System.out.println("pressedButtonHelpClose");
        Stage stage = (Stage) buttonHelpClose.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
}