package com.metait.javafxemailclient.imap.gmail;

public class EmailAddressCounter {
        public String address = null;
        public String addressText = null;
        public int iCount = 0;
        public String toString() {
                return "" +address +" " +iCount +" " +(!address.equals(addressText) ? " = " +addressText : "");
        }
}

