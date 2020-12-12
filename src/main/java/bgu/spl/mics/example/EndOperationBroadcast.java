package bgu.spl.mics.example;

import bgu.spl.mics.Broadcast;

public class EndOperationBroadcast implements Broadcast {

    private String senderId;

    public EndOperationBroadcast(){}

    public EndOperationBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}

