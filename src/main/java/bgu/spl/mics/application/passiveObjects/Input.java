package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.application.passiveObjects.Attack;

public class Input {
    private Attack[] attacks;
    int R2D2;
    int Lando;
    int Ewoks;

    public int getNumberOfEwoks() {
        return Ewoks;
    }
/*    public void setNumberOfEwoks(int ewoks) {
        Ewoks = ewoks;
    }*/
    public int getLandoDuration() {
        return Lando;
    }
/*    public void setLandoDuration(int lando) {
        Lando = lando;
    }*/
    public int getR2D2Duration() {
        return R2D2;
    }
/*    public void setR2D2Duration(int r2d2) {
        R2D2 = r2d2;
    }*/
    public Attack[] getAttacks() {
        return attacks;
    }
/*    public void setAttacks(Attack[] attacks) {
        this.attacks = attacks;
    }*/
}
