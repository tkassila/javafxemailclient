package com.metait.javafxemailclient.imap.gmail;

import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.metait.javafxemailclient.imap.gmail.help.HelpController;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.paint.Color;

import javax.mail.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CountAndListEmailsOfAllGmailMessagesController {

    @FXML
    private TextField textFieldUsername;
    @FXML
    private PasswordField textFieldPassWord;
    @FXML
    private Button buttonConnect;
    @FXML
    private Button buttonCancelExec;
    @FXML
    private CheckBox checkBoxShowPassword;
    // @FXML
    // private Pane panePassword;
    @FXML
    private ListView<EmailAddressCounter> listViews;
    @FXML
    private Label labelMsg;
    @FXML
    private Button buttonReadOldResult;
    @FXML
    private TextField textFieldSearch;
    @FXML
    private Button buttonSearch;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonPrevious;
    @FXML
    private ProgressBar progressIndicator;
    @FXML
    private Button buttonHelp;
    @FXML
    private HBox paneExecution;
    @FXML
    private HBox paneGroup2;
    @FXML
    private HBox paneGroup3;
    @FXML
    private HBox hboxUserName;
    @FXML
    private HBox hboxPassWord;
    @FXML
    private HBox hboxSearch;
    @FXML
    private CheckBox checkBoxClearResults;
    @FXML
    private CheckBox checkBoxSortAfterDomainNames;
    @FXML
    private TreeView treeView;
    @FXML
    private BorderPane borderPane2Top;
    @FXML
    protected ScrollPane scrollPaneTree;
    @FXML
    private BorderPane borderPane1Top;
    @FXML
    private CheckBox checkBoxFolders;

    private TreeItem<String> rootTreeItem = new TreeItem<>();
    private ObservableList<TreeItem<String>> fx_messageFolders = FXCollections.observableArrayList();
    private ChangeListener buttoonPassWordChangeListener = null;
    private String java_user_home = null;
    private AtomicBoolean atomicBoolean = new AtomicBoolean();
    private double dProgressIndicator = 0;
    private int indSearchSelect = -1;
    private TextField pwTextfield = null;
    private boolean bReadOnlyEmail = true;
    private HashMap<String, EmailAddressCounter> hashMapEmails = new HashMap<String, EmailAddressCounter>();

    private ObservableList<EmailAddressCounter> listitems = null;
    private Stage m_primaryStage = null;
    private Application.Parameters m_args = null;
    private String username = null;
    private String password = null;
    private String pword = null;
    private volatile boolean bExcecuted = false;
    private Task<Void> task = null;
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent clipboardConttent = new ClipboardContent();
    /**
     * The calling application, which contains backend code
     */
    private CountAndListEmailsOfAllGmailMessages m_app;
//    private Comparator<EmailAddressCounter> compareByiCount =
//            (EmailAddressCounter o1, EmailAddressCounter o2) -> new Integer(o1.iCount).compareTo( new Integer(o2.iCount) );
    private BiggestEmailCounter compareByiCount = new BiggestEmailCounter();
    private DomainNameEmailSorter domainNameEmailSorter = new DomainNameEmailSorter();
    private EmailAddressCounter [] arrItems = null;

    public void setApplication(CountAndListEmailsOfAllGmailMessages app) { m_app = app; }
    public void setParameters(Application.Parameters args) {
        m_args = args;
        List<String> listargs = m_args.getRaw();
        if (listargs != null && listargs.size() == 2)
        {
            username = listargs.get(0);
            password = listargs.get(1);
        }
        else
        {
            String [] appClassJavaFXArgs = CountAndListEmailsOfAllGmailMessages.launchArgsCommandline;
            if (appClassJavaFXArgs != null && appClassJavaFXArgs.length > 1)
            {
                username = appClassJavaFXArgs[0];
                password = appClassJavaFXArgs[1];
            }
        }
        System.out.println("args=" +m_args.getNamed());
    }
    public void setPrimaryStage(Stage stage) { m_primaryStage = stage; }

    @FXML
    protected void pressedButtonHelp()
    {
        System.out.println("pressedButtonHelp");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("./help/javafxemailclienthelp.fxml"));
            HelpController dialogController = new HelpController();
            fxmlLoader.setController(dialogController);
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent, 500, 400);
            Stage stage = new Stage();
            // stage.setIconified(true);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(scene);
            buttonHelp.setDisable(true);
            // m_primaryStage.setScene(scene);
            stage.showAndWait();
            buttonHelp.setDisable(false);
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    @FXML
    protected void pressedButtonSearch()
    {
        // System.out.println("pressedButtonSearch");
        if (textFieldSearch.getText().trim().length() == 0)
        {
            updateMsg("No eearch text!");
            buttonNext.setDisable(true);
            buttonPrevious.setDisable(true);
            return;
        }
        if (listViews.getItems().size()==0)
        {
            updateMsg("No email on the list!");
            buttonNext.setDisable(true);
            buttonPrevious.setDisable(true);
            return;
        }
        String strSearch = textFieldSearch.getText();
        indSearchSelect = -1;
        int i = -1;
        for(EmailAddressCounter ec : listViews.getItems())
        {
            i++;
            if (ec == null)
                continue;
            if (ec.toString().contains(strSearch)) {
                indSearchSelect = i;
                break;
            }
        }
        if (indSearchSelect == -1)
        {
            updateMsg("Any search result.");
            return;
        }
        listViews.getSelectionModel().select(indSearchSelect);
        listViews.getFocusModel().focus(indSearchSelect);
        listViews.scrollTo(indSearchSelect);
        buttonNext.setDisable(false);
        buttonPrevious.setDisable(false);
        updateMsg("Founded.");
    }

    @FXML
    protected void pressedButtonNext()
    {
        // System.out.println("pressedButtonnNext");
        if (textFieldSearch.getText().trim().length() == 0)
        {
            updateMsg("No eearch text!");
            buttonNext.setDisable(true);
            buttonPrevious.setDisable(true);
            return;
        }
        if (listViews.getItems().size()==0)
        {
            updateMsg("No email on the list!");
            buttonNext.setDisable(true);
            buttonPrevious.setDisable(true);
            return;
        }
        String strSearch = textFieldSearch.getText();
        int indNext = indSearchSelect;
        int i = -1;
        for(EmailAddressCounter ec : listViews.getItems())
        {
            i++;
            if (i <= indNext)
                continue;
            if (ec == null)
                continue;
            if (ec.toString().contains(strSearch)) {
                indNext = i;
                break;
            }
        }
        if (indSearchSelect == indNext)
        {
            updateMsg("Any search result for pressing on the next button.");
            return;
        }
        indSearchSelect = indNext;
        listViews.getSelectionModel().select(indSearchSelect);
        listViews.getFocusModel().focus(indSearchSelect);
        listViews.scrollTo(indSearchSelect);
        buttonNext.setDisable(false);
        buttonPrevious.setDisable(false);
        updateMsg("Founded.");
    }

    @FXML
    protected void pressedButtonPrevious()
    {
        // System.out.println("pressedButtonPrevious");
        if (textFieldSearch.getText().trim().length() == 0)
        {
            updateMsg("No eearch text!");
            buttonNext.setDisable(true);
            buttonPrevious.setDisable(true);
            return;
        }
        if (listViews.getItems().size()==0)
        {
            updateMsg("No email on the list!");
            buttonNext.setDisable(true);
            buttonPrevious.setDisable(true);
            return;
        }
        String strSearch = textFieldSearch.getText();
        List<EmailAddressCounter> reverseList = new ArrayList<>();
        for (EmailAddressCounter ec : listViews.getItems())
        {
            reverseList.add(ec);
        }
        Collections.reverse(reverseList);
        int indPrevious = indSearchSelect;
        int i = reverseList.size();
        for(EmailAddressCounter ec : reverseList)
        {
            i--;
            if (i >= indPrevious)
                continue;
            if (ec == null)
                continue;
            if (ec.toString().contains(strSearch)) {
                indPrevious = i;
                break;
            }
        }
        if (indSearchSelect == indPrevious)
        {
            updateMsg("Any search result for pressing on the previous button.");
            return;
        }
        indSearchSelect = indPrevious;
        listViews.getSelectionModel().select(indSearchSelect);
        listViews.getFocusModel().focus(indSearchSelect);
        listViews.scrollTo(indSearchSelect);
        buttonNext.setDisable(false);
        buttonPrevious.setDisable(false);
        updateMsg("Founded.");
    }

    @FXML
    protected void pressedButtonReadOldResult() {
        // System.out.println("pressedButtonReadOldResult");
        File dataFile = new File(CountAndListEmailsOfAllGmailMessages.getDataFileNamePath());
        if (dataFile.exists())
        {
            int iLoopStart = m_app.readHashMapFromFile(dataFile);
            if (iLoopStart > 0) {
                hashMapEmails = m_app.getHashMapEmails();
                listitems.clear();
                int max = hashMapEmails.values().size();
                arrItems = new EmailAddressCounter[max];
                arrItems = hashMapEmails.values().toArray(arrItems);
                if (!checkBoxSortAfterDomainNames.isSelected())
                    Arrays.sort(arrItems, compareByiCount);
                else
                    Arrays.sort(arrItems, domainNameEmailSorter);
                List<EmailAddressCounter> listEmails = new ArrayList<>();
                for(int j = 0; j < max; j++)
                {
                    listEmails.add(arrItems[j]);
                }
                if (!checkBoxSortAfterDomainNames.isSelected())
                    Collections.reverse(listEmails);
                listitems.addAll(listEmails);
                if (listEmails.size()>0)
                {
                    textFieldSearch.setDisable(false);
                }
            }
        }
    }

    @FXML
    public void initialize() {


        rootTreeItem.setValue("Email folders:");
        treeView.setRoot(rootTreeItem);

        /*
        .focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (! isNowFocused) {
                setFocus(node);
            }
        })
         */

        treeView.setDisable(true);
       // borderPane2Top.prefWidthProperty().bind(borderPane1Top.widthProperty()); //
      //  scrollPaneTree.prefWidthProperty().bind(borderPane2Top.widthProperty()); //
        scrollPaneTree.prefWidthProperty().bind(borderPane1Top.widthProperty()); //
        /*
        Border border = BorderRectangle.getBorderRectangleInstance(Color.GRAY, 1, 2);
        paneExecution.setBorder(border);
        border = BorderRectangle.getBorderRectangleInstance(Color.GRAY, 1, 2);
        paneGroup2.setBorder(border);
        border = BorderRectangle.getBorderRectangleInstance(Color.GRAY, 1, 2);
        paneGroup3.setBorder(border);
        border = BorderRectangle.getBorderRectangleInstance(Color.GRAY, 1, 2);
        hboxUserName.setBorder(border);
        border = BorderRectangle.getBorderRectangleInstance(Color.GRAY, 1, 2);
        hboxPassWord.setBorder(border);
        border = BorderRectangle.getBorderRectangleInstance(Color.GRAY, 1, 2);
        hboxSearch.setBorder(border);

         */

        java_user_home = m_app.getUserHome();

        buttonSearch.setDisable(true);
        buttonNext.setDisable(true);
        buttonPrevious.setDisable(true);

        labelMsg.setStyle("-fx-font-weight: bold");
        listViews.setStyle("-fx-font-weight: bold");

        listViews.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                EmailAddressCounter selectetd = (EmailAddressCounter)listViews.getSelectionModel().getSelectedItem();
                if (selectetd != null) {
                    System.out.println("clicked on " + selectetd);
                    clipboardConttent.putString(selectetd.address);
                    // clipboardConttent.putHtml("<b>Some</b> text");
                    clipboard.setContent(clipboardConttent);
                    updateMsg("Email address in the clipboard! " +selectetd.toString());
                }
            }
        });

        File dataFile = new File(CountAndListEmailsOfAllGmailMessages.getDataFileNamePath());
        if (!dataFile.exists())
        {
            buttonReadOldResult.setDisable(true);
        }
        m_primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (task != null)
                    task.cancel();
                Platform.exit();
                System.exit(0);
            }
        });
        buttonConnect.setDisable(true);
        buttonCancelExec.setDisable(true);

        listitems = FXCollections.observableArrayList();
        listViews.setItems(listitems);

        textFieldSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("");
                if (newValue != null && newValue.trim().length() == 0) {
                    buttonSearch.setDisable(true);
                    buttonNext.setDisable(true);
                    buttonPrevious.setDisable(true);
                }
                else
                {
                    buttonSearch.setDisable(false);
                }
            }
        });

        checkBoxShowPassword.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue)
                {
                    pword = textFieldPassWord.getText();
                    hboxPassWord.getChildren().clear();
                    if (pwTextfield == null) {
                        pwTextfield = new TextField();
                        pwTextfield.textProperty().addListener(buttoonPassWordChangeListener);
                    }
                    pwTextfield.setPrefHeight(textFieldPassWord.getPrefHeight());
                    pwTextfield.setPrefWidth(textFieldPassWord.getPrefWidth());
                    pwTextfield.setText(textFieldPassWord.getText());
                    hboxPassWord.getChildren().clear();
                    hboxPassWord.getChildren().add(pwTextfield);
                }
                else
                {
                    pword = pwTextfield.getText();
                    textFieldPassWord.setPrefHeight(pwTextfield.getPrefHeight());
                    textFieldPassWord.setPrefWidth(pwTextfield.getPrefWidth());
                    textFieldPassWord.setText(pword);
                    hboxPassWord.getChildren().clear();
                    hboxPassWord.getChildren().add(textFieldPassWord);
                }
            }
        });

        buttoonPassWordChangeListener = new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue == null || newValue.trim().length() == 0
                        || textFieldUsername.getText() == null || textFieldUsername.getText().trim().length() == 0) {
                    buttonCancelExec.setDisable(true);
                    if (!bExcecuted)
                        buttonConnect.setDisable(true);
                } else {
                    if (!bExcecuted)
                        buttonCancelExec.setDisable(false);
                    buttonConnect.setDisable(false);
                }
            }
        };

        textFieldPassWord.textProperty().addListener(buttoonPassWordChangeListener);

        /*
        checkBoxShowPassword.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (checkBoxShowPassword.isSelected()) {
              //  textFieldPassWord.setText("");
               // textFieldPassWord.setDisable(true);
            }else {
                // textFieldPassWord.setText(pword);
                // if (pwTextfield != null) {
                // textFieldPassWord.setDisable(false);
            }
        });
         */
        /*
        checkBoxShowPassword.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> {
            if (!checkBoxShowPassword.isSelected()) {
                // textFieldPassWord.setText(pword);
                textFieldPassWord.setPromptText("Password");
            }
        });
         */
        if (username != null)
            textFieldUsername.setText(username);
        if (password != null)
            textFieldPassWord.setText(password);
        if (password != null && username != null)
            buttonConnect.setDisable(false);

        if (textFieldUsername.getText() != null
           && textFieldUsername.getText().trim().length() > 0
           && textFieldPassWord.getText() != null
           && textFieldPassWord.getText().trim().length() > 0) {
            if (!bExcecuted)
                buttonCancelExec.setDisable(false);
            buttonConnect.setDisable(false);
        }

        checkBoxSortAfterDomainNames.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                boolean bSorted = false;
                if (newValue)
                {
                    if (arrItems != null) {
                        Arrays.sort(arrItems, domainNameEmailSorter);
                        bSorted = true;
                    }
                }
                else
                {
                    if (arrItems != null) {
                        Arrays.sort(arrItems, compareByiCount);
                        bSorted = true;
                    }
                }
                if (bSorted) {
                    List<EmailAddressCounter> listEmails = new ArrayList<>();
                    int max = arrItems.length;
                    for(int j = 0; j < max; j++)
                    {
                        listEmails.add(arrItems[j]);
                    }
                    if (!newValue)
                        Collections.reverse(listEmails);
                    Platform.runLater(new Runnable() {
                        public void run() {
                            listitems.clear();
                            listitems.addAll(listEmails);
                            if (listEmails.size() > 0) {
                                textFieldSearch.setDisable(false);
                            }
                        }
                    });
                }
            }
        });

    } // end of initialize method

    private String getPassWordText()
    {
        String ret = null;
        if (checkBoxShowPassword.isSelected())
            ret = pwTextfield.getText();
        else
            ret = textFieldPassWord.getText();
        return ret;
    }


    @FXML
    protected void pressedBttonCancelExec()
    {
     //   System.out.println("pressedBttonCancelExec");
        if (task != null && task.getState().equals(State.RUNNING))
        {
            boolean bValue = atomicBoolean.compareAndSet(false, true);
            if (!bValue) {
                try {
                    Thread.sleep(3000);
                    bValue = atomicBoolean.compareAndSet(false, true);
                    if (!bValue) {
                        return;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }
            }
            task.cancel(true);
        }
    }

    private void updateMsg(String msg)
    {
      if (msg != null)
      if (msg != null)
        Platform.runLater(new Runnable() {
            public void run() {
                labelMsg.setText(msg);
            }
        });
    }

    @FXML
    protected void pressedButtonConnect()
    {
        // System.out.println("pressedButtonConnect");
        if (textFieldUsername.getText().trim().length() == 0)
        {
            setWarningDialog("Username textfield is missing a text value!");
            return;
        }
        if (textFieldPassWord.getText().trim().length() == 0)
        {
            setWarningDialog("Password textfield is missing a text value!");
            return;
        }

        buttonReadOldResult.setDisable(true);

        if (checkBoxClearResults.isSelected() && hashMapEmails.size()>0) {
            hashMapEmails.clear();
        }

        //Task for computing the Panels:
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                    /*
                    Platform.runLater(new Runnable() {
                        public void run() {
                            progress.setProgress(prog);
                        }
                    });
                     */
                buttonConnect.setDisable(true);
                textFieldSearch.setDisable(true);
                buttonNext.setDisable(true);
                buttonPrevious.setDisable(true);

                Folder emailFolder = null;
                Store store = null;
                try {
                    buttonCancelExec.setDisable(false);
                    bExcecuted = true;
                    EmailAddressCounter counter = null;
                    String email = null, strEmailText = null;
                    int iLoopStart = 0;
                    File dataFile = new File(CountAndListEmailsOfAllGmailMessages.getDataFileNamePath());
                    if (!checkBoxClearResults.isSelected() && dataFile.exists())
                    {
                        iLoopStart = m_app.readHashMapFromFile(dataFile);
                        if (iLoopStart > 0) {
                            hashMapEmails = m_app.getHashMapEmails();
                            int max = hashMapEmails.values().size();
                            arrItems = new EmailAddressCounter[max];
                            arrItems = hashMapEmails.values().toArray(arrItems);
                            if (!checkBoxSortAfterDomainNames.isSelected())
                                Arrays.sort(arrItems, compareByiCount);
                            else
                                Arrays.sort(arrItems, domainNameEmailSorter);
                            List<EmailAddressCounter> listEmails = new ArrayList<>();
                            for(int j = 0; j < max; j++)
                            {
                                listEmails.add(arrItems[j]);
                            }
                            if (!checkBoxSortAfterDomainNames.isSelected())
                                Collections.reverse(listEmails);
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    listitems.clear();
                                    listitems.addAll(listEmails);
                                    if (listEmails.size()>0)
                                    {
                                        textFieldSearch.setDisable(false);
                                    }
                                }
                            });
                            updateMsg("Old datafile (" +dataFile.getAbsolutePath() +") is read and correspond statisticall messages (" +iLoopStart +") set as base to continue." );
                        }
                    }
                    else {
                        updateMsg("Execcution has started from 1 message....");
                        Platform.runLater(new Runnable() {
                            public void run() {
                                listitems.clear();
                            }
                        });
                    }

                    GmailSessionReturn gsession = null;
                    Message message;
                    Message [] messages = null;
                    MessageFolder [] messageFolders = null;
                    boolean bHandleSomething = false;
                    gsession = m_app.getConnectSessionMessages(m_app.getGmailHot(), m_app.getMailStoreType(), textFieldUsername.getText(), getPassWordText());
                    messages = gsession.messages;
                    emailFolder = gsession.folder;
                    store = gsession.store;
                    messageFolders = gsession.messageFolders;

                    if (messageFolders != null && messageFolders.length > 0)
                    {
                        TreeItem<String> item ;
                        for(MessageFolder mf : messageFolders)
                        {
                            if (mf == null)
                                continue;
                            item = new TreeItem<>();
                            item.setValue("" + mf.folederName +" ( " +mf.iFolerEmails +")");
                            fx_messageFolders.add(item);
                        }
                        rootTreeItem.getChildren().addAll(fx_messageFolders);
                        rootTreeItem.setExpanded(true);
                    }
                    int i = iLoopStart;
                    int n = messages.length;
                    dProgressIndicator = ((((1.0)*i)/((1.0F*n))*100.0)/100.0);
                    Platform.runLater(new Runnable() {
                        public void run() {
                            progressIndicator.setProgress(dProgressIndicator);
                        }
                    });

                    for (; i < n; i++) {
                        message = messages[i];
                         // System.out.println("" +i +" Subject: " + message.getSubject());
                        strEmailText = message.getFrom()[0].toString();
                        // System.out.println("From: " +email );
                        email = strEmailText;
                        if (bReadOnlyEmail)
                            email = m_app.readOnlyEmail(strEmailText);
                        counter = hashMapEmails.get(email);
                        if (counter == null) {
                            counter = new EmailAddressCounter();
                            counter.iCount = 1;
                            counter.address = email;
                            counter.addressText = strEmailText;
                        }
                        else
                        {
                            counter.iCount = counter.iCount +1;
                        }
                        hashMapEmails.put(email, counter);
                        bHandleSomething = true;
                        if ((i % 100) == 0) {
                            System.out.println("i: " + i);
                            updateMsg("Handled message index: " +i);
                            dProgressIndicator = ((((1.0)*i)/((1.0*n))*100.0)/100.0);
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    progressIndicator.setProgress(dProgressIndicator);
                                }
                            });
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    listitems.clear();
                                }
                            });
                            int max = hashMapEmails.values().size();
                            arrItems = new EmailAddressCounter[max];
                            arrItems = hashMapEmails.values().toArray(arrItems);
                            if (!checkBoxSortAfterDomainNames.isSelected())
                                Arrays.sort(arrItems, compareByiCount);
                            else
                                Arrays.sort(arrItems, domainNameEmailSorter);
                            List<EmailAddressCounter> listEmails = new ArrayList<>();
                            for(int j = 0; j < max; j++)
                            {
                                listEmails.add(arrItems[j]);
                            }
                            if (!checkBoxSortAfterDomainNames.isSelected())
                                Collections.reverse(listEmails);

                            dProgressIndicator = (((((1.0F)*i)/((1.0F)*n))*100.0)/100.0);
                            System.out.println("iProgressIndicator=" + dProgressIndicator);
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    progressIndicator.setProgress(dProgressIndicator);
                                    listitems.clear();
                                    listitems.addAll(listEmails);
                                    if (listEmails.size()>0)
                                    {
                                        textFieldSearch.setDisable(false);
                                    }
                                }
                            });
                        }
                        if ((i == (n-1)) || ((i % 1000) == 0 && i != 0)) {
                            boolean bValue = atomicBoolean.compareAndSet(false, true);
                            if (!bValue) {
                                continue;
                            }
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    listitems.clear();
                                }
                            });
                            int max = hashMapEmails.values().size();
                            arrItems = new EmailAddressCounter[max];
                            arrItems = hashMapEmails.values().toArray(arrItems);
                            if (!checkBoxSortAfterDomainNames.isSelected())
                                Arrays.sort(arrItems, compareByiCount);
                            else
                                Arrays.sort(arrItems, domainNameEmailSorter);
                            List<EmailAddressCounter> listEmails = new ArrayList<>();
                            for(int j = 0; j < max; j++)
                            {
                                listEmails.add(arrItems[j]);
                            }
                            Collections.reverse(listEmails);
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    listitems.clear();
                                    listitems.addAll(listEmails);
                                    if (listEmails.size()>0)
                                    {
                                        textFieldSearch.setDisable(false);
                                    }
                                }
                            });
                            m_app.setHashData(hashMapEmails);
                            m_app.writeHashMapIntoFile(i);
                            atomicBoolean.set(false);
                            updateMsg("Handled message index: " +i);
                        }
                        if (isCancelled())
                        {
                            /*
                            atomicBoolean.set(true);
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    listitems.clear();
                                }
                            });
                            int max = hashMapEmails.values().size();
                            arrItems = new EmailAddressCounter[max];
                            arrItems = hashMapEmails.values().toArray(arrItems);
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    List<EmailAddressCounter> listEmails = new ArrayList<>();
                                    for(int j = 0; j < max; j++)
                                    {
                                        listEmails.add(arrItems[j]);
                                    }
                                    Collections.reverse(listEmails);
                                    listitems.addAll(listEmails);
                                    if (listEmails.size()>0)
                                    {
                                        textFieldSearch.setDisable(false);
                                    }
                                }
                            });
                            m_app.setHashData(hashMapEmails);
                            m_app.writeHashMapIntoFile(i);
                            updateMsg("Handled mesage index: " +i);
                             */
                            break;
                        }
//                System.out.println("Text: " + message.getContent().toString());

                    }

                    if (!isCancelled())
                    {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                if (task.getState() == State.RUNNING)
                                    progressIndicator.setProgress(1.0);
                            }
                        });
                    }
                    // m_app.check(m_app.getGmailHot(), m_app.getMailStoreType(), textFieldUsername.getText(), getPassWordText());
//                        return null;
                    } catch (NoSuchProviderException e) {
                        e.printStackTrace();
                        buttonConnect.setDisable(false);
                        buttonCancelExec.setDisable(true);
                        updateMsg("Error: " +e.getMessage());
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    buttonConnect.setDisable(false);
                    buttonCancelExec.setDisable(true);
                    updateMsg("Email error: " +e.getMessage());
                    } catch (Exception e) {
                        e.printStackTrace();
                } finally {
                    //close the store and folder objects
                    emailFolder.close(false);
                    store.close();
                }
                return null;
                }};

        //stateProperty for Task:
        task.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends State> observable,
                                State oldValue, Worker.State newState) {
                if(newState==Worker.State.SUCCEEDED){
                    labelMsg.setText("Execution succeeded. All messages calculated.");
                    buttonCancelExec.setDisable(true);
                    buttonConnect.setDisable(false);
                }
                else
                if(newState== State.CANCELLED){
                    buttonCancelExec.setDisable(true);
                    buttonReadOldResult.setDisable(false);
                    buttonConnect.setDisable(false);
                    labelMsg.setText("Execution canceled by the user. You can continue from where you left message calculating.");
                }
            }
        });

        //start Task
        buttonCancelExec.setDisable(false);
        new Thread(task).start();
    }

    private void setWarningDialog(String msg)
    {

    }

    @FXML
    protected void pressed_checkBoxFolders() {
        System.out.println("pressed_checkBoxFolders");
        if (rootTreeItem.getChildren().size() > 0)
            if (treeView.isDisable())
                treeView.setDisable(false);
            else
                treeView.setDisable(true);
    }

    /* @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
     */
}