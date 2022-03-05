package com.metait.javafxemailclient.imap.gmail;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

public class GmailSessionReturn {
    public Message[] messages = null;
    public Store store = null;
    public Folder folder = null;
}
