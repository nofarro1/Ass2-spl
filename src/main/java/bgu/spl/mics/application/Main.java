package bgu.spl.mics.application;

//import com.google.gson.Gson;

import bgu.spl.mics.Future;
import bgu.spl.mics.application.messages.DeactivationEvent;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/** This is the Main class of the application. You should parse the input file,
 * create the different components of the application, and run the system.
 * In the end, you should output a JSON.
 */
public class Main {
	public static void main(String[] args) {
		Gson input = new Gson();
		Input gameon = null;
		try{
			Reader reader = new FileReader(args[0]);
			gameon=input.fromJson(reader,Input.class);
		}
		catch (FileNotFoundException e){}
		LeiaMicroservice leiaMicroservice = new LeiaMicroservice(gameon.getAttacks());

	}
}
