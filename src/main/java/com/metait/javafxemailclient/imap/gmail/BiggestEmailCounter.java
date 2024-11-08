package com.metait.javafxemailclient.imap.gmail;

import java.util.Comparator;

public class BiggestEmailCounter implements Comparator<EmailAddressCounter> {
        @Override
        public int compare(EmailAddressCounter a, EmailAddressCounter b) {
            try {
                return new Integer(a.iCount).compareTo( new Integer(b.iCount));
            }catch (Exception e){
                throw e;
            }
        }
    }
