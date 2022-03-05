package com.metait.javafxemailclient.imap.gmail;

/*
import com.sun.mail.gimap.GmailSSLStore;
import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailMessage;
import com.sun.mail.imap.IMAPStore;
import java.util.Properties;
import javax.mail.FetchProfile;
 */
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.PasswordAuthentication;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.NoSuchProviderException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import javafx.collections.FXCollections;

/*
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import javax.mail.search.SearchTerm;
import javax.mail.search.AndTerm;
import javax.mail.search.SubjectTerm;
import javax.mail.search.BodyTerm;
import javax.mail.Address;
 */

import java.io.File;
import java.util.*;

public class CountAndListEmailsOfAllGmailMessages extends Application {

    private static String java_user_home = System.getProperty("user.home");
    private static String from = "";
    private static String username = "";
    private static String password = "";
    private static boolean bUseUserHomeDir = false;
    private HashMap<String, EmailAddressCounter> hashMapEmails = new HashMap<String, EmailAddressCounter>();
    protected static String dataFileNamePath = "." + File.separator +"emailcount.txt";
    private boolean bReadOnlyEmail = true;
    private static boolean bCommandLineAppWillStarted = false;
    public static String [] launchArgsCommandline = null;
    public String getUserHome() { return  java_user_home; }
    public static String getDataFileNamePath() {
        String ret = dataFileNamePath;
        if (bUseUserHomeDir)
            ret = java_user_home +File.separator +ret;
        return ret;
    }


    private Stage m_primaryStage;
    private static String host = "pop.gmail.com";
    private static String mailStoreType = "pop3";

    private CountAndListEmailsOfAllGmailMessagesController controller = new CountAndListEmailsOfAllGmailMessagesController();
    public CountAndListEmailsOfAllGmailMessagesController getController()
    {
        return controller;
    }
    public String getGmailHot() { return host; }
    public String getMailStoreType() { return mailStoreType; }
    public void setHashData(HashMap<String, EmailAddressCounter> data){ hashMapEmails = data; }
    public HashMap<String, EmailAddressCounter> getHashMapEmails() { return hashMapEmails; }

    public static void printHelp()
    {
        System.err.println("java ... " +CountAndListEmailsOfAllGmailMessages.class.getName() +" -gui|-commandlline -use_user_home|-donotuse_user_home [gmailusername|gmailemailaddress] [password]" );
        System.err.println("- This Java appölication has to be started as commandline or a javafx gui app. The parameter: -gui or -commandlline\n"
        +"- gmailusername = a username of gmail account. password = a password of the gmail account. Give strike chars into the ends\n"
        +"of password if it contains space.\n"
        +"As commandline app it is connecting into gmail server and read all emails for given gmail account. (This will takes tiime.)\n"
        +"On every message first from email address is reading and calculating hom many messages has been send into the same address.\n"
        +"A text file will be written into file (" +getDataFileNamePath() +") on every 10sec and at time of last email message.\n"
        +"You can break the app execution and later start it again with same parameters and on the smae directory.");

        System.err.println("\n- A gui application is showing how many email messages there are, how many message are read and\n"
        +"what are the current result in the same file. Also here is possible to break execution (a button) and\n"
        +"continue the execution later.");
    }

    public static boolean handleArgs(String[] args)
    {
        boolean ret = false;
        if (args.length > 0) {
            String arg1 = args[0];
            if (arg1 != null && arg1.trim().length() > 0) {
                if (arg1.equals("-commandline"))
                    bCommandLineAppWillStarted = true;
                else if (arg1.equals("-gui"))
                    bCommandLineAppWillStarted = false;
                else {
                    printHelp();
                    System.exit(2);
                }
            }

            if (args.length > 1) {
                String arg2 = args[1];
                if (arg2 != null && arg2.trim().length() > 0) {
                    if (arg2.equals("-use_user_home"))
                    bUseUserHomeDir = true;
                }

                if (args.length == 4) {
                    String arg3 = args[2];
                    if (arg3 != null && arg3.trim().length() > 0) {
                        // from = args[0];
                        username = arg3;
                    }
                    String arg4 = args[3];
                    if (arg4 != null && arg4.trim().length() > 0) {
                        password = arg4;
                        launchArgsCommandline = new String[2];
                        launchArgsCommandline[0] = username;
                        launchArgsCommandline[1] = password;
                        ret = true;
                    } else {
                        printHelp();
                        System.exit(4);
                    }
                } else {
                    if (bCommandLineAppWillStarted) {
                        printHelp();
                        System.exit(3);
                    }
                    ret = true;
                }
            }
        }
        return ret;
    }

    public static void main(String[] args) throws MessagingException
    {
        if (!handleArgs(args))
        {
            printHelp();
            System.exit(1);
        }

        if (bCommandLineAppWillStarted)
        {
            CountAndListEmailsOfAllGmailMessages app = new CountAndListEmailsOfAllGmailMessages();
            /*
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "gimaps");
            //props.setProperty("mail.debug", "true");

            final String from = "tuomas.kassila@gmail.com";
            final String username = "tuomas.kassila";
            final String password = "zsbgrjgjispeawsc";

            GmailSSLStore store = null;
            GmailFolder folder = null;
            try {
                Session session = Session.getDefaultInstance(props, null);
                store = (GmailSSLStore) session.getStore("gimaps");
                store.connect(username, password);
                folder = (GmailFolder) store.getFolder("INBOX");
                folder.open(Folder.READ_ONLY);
                Message[] ms = folder.getMessages();
                FetchProfile fp = new FetchProfile();
                fp.add(GmailFolder.FetchProfileItem.MSGID);
                fp.add(GmailFolder.FetchProfileItem.THRID);
                fp.add(GmailFolder.FetchProfileItem.LABELS);

                folder.fetch(ms, fp);

                GmailMessage gm;
                String[] labels;

                for (Message m : ms) {
                    gm = (GmailMessage) m;
                    System.out.println(gm.getMsgId());

                    // Hex version - useful for linking to Gmail
                    //System.out.println(Long.toHexString(gm.getMsgId()));

                    System.out.println(gm.getThrId());

                    labels = gm.getLabels();
                    if (labels != null) {
                        for (String label : labels) {
                            if (label != null) {
                                System.out.println("Label: " + label);
                            }
                        }
                    }
                }
            } catch (MessagingException ex) {
            } finally {
                if (folder != null && folder.isOpen()) {
                    folder.close(true);
                }
                if (store != null) {
                    store.close();
                }
            }
             */

        /*
        HashMap<String, EmailAddressCounter> hashMapEmails = new HashMap<String, EmailAddressCounter>();

        boolean emailReceived = false;
        Store store = null;
        String folder = "kissa";
        String subject = "aihe";
        try {
            Date aDate = new Date(System.currentTimeMillis());
            aDate.setYear(aDate.getYear() + 1);
            store = getConnection();
            Folder mailFolder = store.getFolder(folder);
            mailFolder.open(Folder.READ_WRITE);
            SearchTerm st = new AndTerm(new SubjectTerm(subject), new BodyTerm(subject));
            Message[] messages = mailFolder.search(st);
            String sendedFrom = null;
            Address[] fromEmails = null;
            EmailAddressCounter emailAddress = null;

            for (Message message : messages) {
                System.out.println("message  : " + message.getFrom().toString());
                // if (message.getSubject().contains(subject)) {
                if (message.getSentDate().after(aDate))
                    fromEmails = message.getFrom();
                // System.out.println("Found the email subject : " + subject);
                for (Address addr : fromEmails) {
                    System.out.println("from : " + addr.toString());
                    emailAddress = hashMapEmails.get(addr.toString());
                    if (emailAddress == null) {
                        emailAddress = new EmailAddressCounter();
                        emailAddress.address = addr.toString();
                        emailAddress.iCount = 1;
                    } else {
                        emailAddress.iCount++;
                    }
                    hashMapEmails.put(addr.toString(), emailAddress);
                }
                emailReceived = true;
                break;
            }
            System.out.println("emailReceived=" + emailReceived);

            Collection<EmailAddressCounter> listemils = hashMapEmails.values();
            EmailAddressCounter[] arrEmails = new EmailAddressCounter[listemils.size()];
            arrEmails = listemils.toArray(arrEmails);
            for(EmailAddressCounter ea : arrEmails)
            {
                System.out.println("email=" + ea.address +" iCount=" +ea.iCount);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (store != null) {
                store.close();
            }
        }
         */
            //host = "pop.gmail.com";// change accordingly
            // mailStoreType = "pop3";
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        app.check(host, mailStoreType, username, password);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }
                }
            });
        }
        else {
            launch(launchArgsCommandline);
        }
       // searcher.searchEmail(host, port, userName, password, keyword);
   }

   public GmailSessionReturn getConnectSessionMessages(String host, String storeType, String user, String password)
           throws NoSuchProviderException, MessagingException
   {
       int i = -1;
       boolean bHandleSomething = false;

           //create properties field
           Properties properties = new Properties();

            /*
            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
             */
           properties.put("mail.smtp.starttls.enable", true);
           properties.put("mail.smtp.host", "smtp.gmail.com");
           properties.put("mail.smtp.user", "username");
           properties.put("mail.smtp.password", "password");
           properties.put("mail.pop3.port", "995");
           properties.put("mail.smtp.auth", true);

           Session emailSession = Session.getInstance(properties, new  javax.mail.Authenticator() {
               @Override
               protected PasswordAuthentication getPasswordAuthentication() {
                   return new PasswordAuthentication(username, password);
               }
           });
           // Session emailSession = Session.getDefaultInstance(properties);

           //create the POP3 store object and connect with the pop server
           Store store = emailSession.getStore("imaps");

           store.connect(host, user, password);

           //create the folder object and open it
           Folder emailFolder = store.getFolder("INBOX");
           emailFolder.open(Folder.READ_ONLY);

           // retrieve the messages from the folder in an array and print it
           Message[] messages = emailFolder.getMessages();
           System.out.println("messages.length---" + messages.length);
           GmailSessionReturn ret = new GmailSessionReturn();
           ret.folder = emailFolder;
           ret.messages = messages;
           ret.store = store;
           return ret;
   }

    public void check(String host, String storeType, String user, String password)
            throws InterruptedException
    {

        EmailAddressCounter counter = null;
        String email = null, strEmailText = null;
        int iLoopStart = 0;
        File dataFile = new File(getDataFileNamePath());
        if (dataFile.exists())
        {
            iLoopStart = readHashMapFromFile(dataFile);
        }

        GmailSessionReturn gsession = null;
        Message message;
        Message [] messages = null;
        Folder emailFolder = null;
        Store store = null;
        boolean bHandleSomething = false;
        try {
            gsession = getConnectSessionMessages(host, storeType, user, password);
            messages = gsession.messages;
            emailFolder = gsession.folder;
            store = gsession.store;

        int i = iLoopStart;
        int n = messages.length;

        for (; i < n; i++) {
                message = messages[i];
                /*
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                 */
                // System.out.println("" +i +" Subject: " + message.getSubject());
                strEmailText = message.getFrom()[0].toString();
                // System.out.println("From: " +email );
                email = strEmailText;
                if (bReadOnlyEmail)
                    email = readOnlyEmail(strEmailText);
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
                }
                if ((i == (n-1)) || ((i % 1000) == 0 && i != 0)) {
                    writeHashMapIntoFile(i);
                }

//                System.out.println("Text: " + message.getContent().toString());

            }

            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readOnlyEmail(String strEmailText)
    {
        String ret = strEmailText;
        if (strEmailText != null && strEmailText.trim().length() > 0)
        {
            int ind = strEmailText.indexOf("<");
            if (ind > -1)
            {
                ret = strEmailText.substring(ind +1);
                int ind2 = ret.indexOf(">");
                if (ind2 > -1)
                {
                    ret = ret.substring(0, ind2);
                }
            }
        }
        return ret;
    }

    public synchronized void writeHashMapIntoFile(int iLoopCounter)
    {
        Collection<EmailAddressCounter> collection = hashMapEmails.values();
        Comparator<EmailAddressCounter> compareByiCount =
                (EmailAddressCounter o1, EmailAddressCounter o2) -> new Integer(o1.iCount).compareTo( new Integer(o2.iCount) );

        EmailAddressCounter [] arrCounters = new EmailAddressCounter[collection.size()];
        arrCounters = collection.toArray(arrCounters);
        Arrays.sort(arrCounters, compareByiCount);
      //  Arrays.sort(arrCounters, Collections.reverseOrder());

        StringBuffer sb = new StringBuffer();
        for (EmailAddressCounter ec : arrCounters)
        {
            if (ec == null)
                continue;
            // System.out.println("ec.iCount=" +ec.iCount +" email=" +ec.address +" addresstext=" +ec.addressText);
            sb.append("ec.iCount=" +ec.iCount +" email=" +ec.address +" addresstext=" +ec.addressText +"\n");
        }

        try {
            String newData = sb.toString() +"\n-----\niLoopCounter=" +iLoopCounter +"\n";
            Files.write(Paths.get(getDataFileNamePath()), newData.getBytes());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private  EmailAddressCounter parseEmailAddressCounter(String dataItem)
    {
        EmailAddressCounter ret = null;
        // sb.append("ec.iCount=" +ec.iCount +" email=" +ec.address +"\n");
        int indICouont = dataItem.indexOf("=");
        if (indICouont == -1)
            return null;
        int indEmail = dataItem.indexOf("=", indICouont+1);
        if (indEmail == -1)
            return null;
        String strCount = dataItem.substring(indICouont+1, indEmail-5);
        String strEmail = dataItem.substring(indEmail+1);
        final String cnstAddressText = "addresstext=";
        int indAddressText = strEmail.indexOf(cnstAddressText);
        String addressText = new String(strEmail);
        if (indAddressText > -1)
        {
            strEmail = addressText.substring(0, indAddressText).trim();
            addressText = addressText.substring(indAddressText +cnstAddressText.length()).trim();
        }
        int iCount = Integer.parseInt(strCount.trim());
        ret = new EmailAddressCounter();
        ret.addressText = addressText;
        ret.address = strEmail;
        if (strEmail.contains("<"))
        {
            ret.address = readOnlyEmail(strEmail);
        }
        ret.iCount = iCount;
        return ret;
    }

    public int readHashMapFromFile(File dataFile)
    {
        int iLoopCounter = 0;

        Path filePath = Paths.get(dataFile.getAbsolutePath());
        Charset charset = StandardCharsets.UTF_8;
        StringBuffer sb = new StringBuffer();
        try {
            List<String> lines = Files.readAllLines(filePath, charset);
            for(String line: lines) {
                //System.out.println(line);
                sb.append(line +"\n");
            }
            String strBuffer = sb.toString();
            String search = "iLoopCounter=";
            int ind = strBuffer.indexOf(search);
            if (ind > -1)
            {
                String strBefore = strBuffer.substring(0, ind);
                String strFound = strBuffer.substring(ind +search.length());
                iLoopCounter = Integer.parseInt(strFound.replace("\n",""));
                String [] arrDataItems = strBefore.split("\n");
                EmailAddressCounter ea = null;
                int i = -1;
                for (String dataItem : arrDataItems)
                {
                    if (!dataItem.startsWith("ec.iCount"))
                        continue;
                    ea = parseEmailAddressCounter(dataItem);
                    if (ea == null)
                        continue;
                    hashMapEmails.put(ea.address, ea);
                    i++;
                }
                if (iLoopCounter == 0 && i > -1)
                    iLoopCounter = i +1; // set +1 to strat into next unhabdled email message!!
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }

        return iLoopCounter;
    }

    private static Store getConnection() throws MessagingException {
        Properties properties;
        Session session;
        Store store;
        properties = System.getProperties();
        properties.setProperty("mail.host", "imap.gmail.com");
        properties.setProperty("mail.port", "995");
        properties.setProperty("mail.store.protocol", "imaps");
        // properties.setProperty("mail.transport.protocol", "imaps");
        session = Session.getInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        store = session.getStore("imaps");
        store.connect();
        return store;

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // FXMLLoader fxmlLoader = new FXMLLoader(CountAndListEmailsOfAllGmailMessages.class.getResource("javafxemailclient.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("javafxemailclient.fxml"));
        // remove xml block from .fxml file: fx:controller="com.metait.javafxplayer.PlayerController"
        fxmlLoader.setController(controller);
        m_primaryStage = primaryStage;
        controller.setParameters(getParameters());
        controller.setPrimaryStage(m_primaryStage);
        controller.setApplication(this);
        Parent loadedroot = fxmlLoader.load();

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        Scene scene = new Scene(loadedroot, screenBounds.getMaxX() / 1.5, 440);
        primaryStage.setTitle("Gmail email counter: counts different email address and how many messages / address");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
