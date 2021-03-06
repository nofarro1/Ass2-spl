package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.example.ExampleBroadcast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class MessageBusImplTest {
    private MessageBusImpl messageBus;
    private AttackEvent attackEvent;
    private MicroService hanSolo;
    private Broadcast broadcast;
    private Attack attack;


    @BeforeEach
    public void setUp() {
        messageBus = new MessageBusImpl();
        hanSolo = new HanSoloMicroservice();
        List<Integer> attackList = new LinkedList<>();
        attackList.add(2);
        attackList.add(1);
        attackList.add(4);
        attack = new Attack(attackList, 1000);
        attackEvent = new AttackEvent(attack);
        broadcast = new ExampleBroadcast("1");
    }

    @Test
    public void subscribeEventTest (){
        messageBus.subscribeEvent(attackEvent.getClass() ,hanSolo);
        messageBus.sendEvent(attackEvent);
        try {
            assertEquals(messageBus.awaitMessage(hanSolo), attackEvent);
        }catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void subscribeBroadcastTest (){
        messageBus.subscribeBroadcast(broadcast.getClass() ,hanSolo);
        messageBus.sendBroadcast(broadcast);
        try {
            assertEquals(messageBus.awaitMessage(hanSolo), broadcast);
        }catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void completeTest (){
        Boolean result = true;
        Future<Boolean> future = messageBus.sendEvent(attackEvent);
        messageBus.complete(attackEvent, result);
        assertTrue(future.isDone());
    }

    @Test
    public void sendBroadcastTest (){
        messageBus.subscribeBroadcast(broadcast.getClass() ,hanSolo);
        messageBus.sendBroadcast(broadcast);
        try {
            assertEquals(messageBus.awaitMessage(hanSolo), broadcast);
        }catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void sendEventTest (){
        messageBus.subscribeEvent(attackEvent.getClass() ,hanSolo);
        messageBus.sendEvent(attackEvent);
        try {
            assertEquals(messageBus.awaitMessage(hanSolo), attackEvent);
        }catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void registerTest (){
        messageBus.register(hanSolo);
        messageBus.subscribeEvent(attackEvent.getClass(),hanSolo);
        messageBus.sendEvent(attackEvent);
        try{
            assertEquals(messageBus.awaitMessage(hanSolo),attackEvent);
        }catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }

    @Test
    public void awaitMessageTest (){
        C3POMicroservice c3po = new C3POMicroservice();
        messageBus.register(c3po);
        messageBus.subscribeEvent(attackEvent.getClass(),c3po);
        messageBus.sendEvent(attackEvent);
        try{
            assertEquals(messageBus.awaitMessage(c3po),attackEvent);
        }catch(InterruptedException exc) {
            exc.printStackTrace();
        }
    }
}