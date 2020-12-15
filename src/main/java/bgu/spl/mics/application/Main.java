package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewoks;
import bgu.spl.mics.application.passiveObjects.Input;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson gson = new Gson();
		Reader read = null;
		try{
			read = new FileReader("input.json");
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}

		Input input = gson.fromJson(read, Input.class);
		Diary diary = Diary.getInstance();
		Ewoks ewoks = Ewoks.getInstance();
		ewoks.makeEwoks(input.getEwoks());

		MicroService leia = new LeiaMicroservice(input.getAttacks());
		Thread Leia = new Thread(leia);
		MicroService hanSolo = new HanSoloMicroservice();
		Thread HanSolo = new Thread(hanSolo);
		HanSolo.start();
		MicroService c3po = new C3POMicroservice();
		Thread C3PO = new Thread(c3po);
		C3PO.start();
		MicroService r2d2 = new R2D2Microservice(input.getR2D2());
		Thread R2D2 = new Thread(r2d2);
		R2D2.start();
		MicroService lando = new LandoMicroservice(input.getLando());
		Thread Lando = new Thread(lando);
		Lando.start();

		try{
			Leia.sleep(1000);
			Leia.start();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try{
			HanSolo.join();
			C3PO.join();
			R2D2.join();
			Lando.join();
			Leia.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Gson output = new GsonBuilder().setPrettyPrinting().create();
		try{
			//FileWriter file = new FileWriter(args[1]);
			FileWriter file = new FileWriter("Output.json");
			output.toJson(diary, file);
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//TODO: DELETE!!
		System.out.println("finish");
	}
}
