package com.chemutai.letschat.Chat;

public class ChatObject {

    private String mMessage;
    private Boolean currentUser;


    public ChatObject(String mMessage, Boolean currentUser) {
        this.mMessage = mMessage;
        this.currentUser = currentUser;
    }


    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    public Boolean getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Boolean currentUser) {
        this.currentUser = currentUser;
    }
}
