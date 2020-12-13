package bgu.spl.mics.application;

//import com.google.gson.Gson;

import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Ewok;
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
		ewoks.makeEwoks(input.getNumberOfEwoks());

		Thread Leia = new Thread(new LeiaMicroservice(input.getAttacks()));
		Thread HanSolo = new Thread(new HanSoloMicroservice());
		HanSolo.start();
		Thread C3PO = new Thread(new C3POMicroservice());
		C3PO.start();
		Thread R2D2 = new Thread(new R2D2Microservice(input.getR2D2Duration()));
		R2D2.start();
		Thread Lando = new Thread(new LandoMicroservice(input.getLandoDuration()));
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
			FileWriter file = new FileWriter(args[1]);
			output.toJson(diary, file);
			file.flush();
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
