package bgu.spl.mics.example;

import bgu.spl.mics.Broadcast;

public class EventAttackBroadcast implements Broadcast {

    private String senderId;

    public EventAttackBroadcast(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

}

