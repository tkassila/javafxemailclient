package com.metait.javafxemailclient.imap.gmail;

import java.util.Comparator;

public class DomainNameEmailSorter implements Comparator<EmailAddressCounter> {
        @Override
        public int compare(EmailAddressCounter a, EmailAddressCounter b) {
            try {
                String a_email = a.toString();
                String b_email = b.toString();
                if (a_email == null || a_email.trim().length() == 0)
                    return -1;
                if (b_email == null || b_email.trim().length() == 0)
                    return -1;
                int ind_a = a_email.indexOf('@');
                int ind_b = b_email.indexOf('@');
                if (ind_a < 0)
                    return a_email.compareTo( b_email);
                if (ind_b < 0)
                    return a_email.compareTo( b_email);
                a_email = a_email.substring(ind_a);
                b_email = b_email.substring(ind_b);
                return a_email.compareTo( b_email);
            }catch (Exception e){
                throw e;
            }
        }
    }
