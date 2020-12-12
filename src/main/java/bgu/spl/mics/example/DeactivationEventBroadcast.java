package bgu.spl.mics.example;

import bgu.spl.mics.Broadcast;

public class DeactivationEventBroadcast implements Broadcast {

    private String senderId;

    public DeactivationEventBroadcast(){}

    public DeactivationEventBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}

