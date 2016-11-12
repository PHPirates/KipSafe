package com.b18.kipsafe;

import com.firebase.client.Firebase;

/**
 * Created by s152337 on 12-11-2016.
 */

public class BaseClass {

    Firebase firebase;
    boolean open;

    BaseClass(Firebase firebase, boolean open) {
        this.firebase = firebase;
        this.open = open;
    }

    public void changeOpen(boolean open) {
        firebase.child("open").setValue(open);
    }

}
