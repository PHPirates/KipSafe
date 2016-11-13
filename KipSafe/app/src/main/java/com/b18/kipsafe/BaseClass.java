package com.b18.kipsafe;

import com.firebase.client.Firebase;

public class BaseClass {

    Firebase firebase;
    boolean open;

    BaseClass(Firebase firebase, boolean open) {
        this.firebase = firebase;
        this.open = open;
    }

    /**
     * update egg on all phones via firebase
     * @param open boolean egg-state
     */
    public void changeOpen(boolean open) {
        firebase.child("open").setValue(open);
    }

}
