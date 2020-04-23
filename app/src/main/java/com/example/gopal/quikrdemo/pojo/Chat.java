package com.example.gopal.quikrdemo.pojo;

public class Chat {
    private String chatID;
    private String chatCreator;
    private String chatBody;

    public Chat() {

    }

    public Chat(String chatID, String chatCreator, String chatBody) {
        this.chatID = chatID;
        this.chatCreator = chatCreator;
        this.chatBody = chatBody;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getChatCreator() {
        return chatCreator;
    }

    public void setChatCreator(String chatCreator) {
        this.chatCreator = chatCreator;
    }

    public String getChatBody() {
        return chatBody;
    }

    public void setChatBody(String chatBody) {
        this.chatBody = chatBody;
    }
}
