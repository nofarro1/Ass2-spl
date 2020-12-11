package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.awt.*;
import java.util.List;

public class AttackEvent implements Event<Boolean> {
	private int duration;
	private List<Integer> serials;

	public AttackEvent(Attack attack)
    {
        //this.serials=attack.getSerial(); // TODO**********
        //this.duration=attack.getDuration(); // TODO**********

    }

    public int getDuration(){return duration;}

    public List<Integer> getSerials(){return serials;}

}
