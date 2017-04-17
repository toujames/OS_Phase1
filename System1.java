/* 
James Touthang
CS-4323 Op Systems
MLFQ Phase 1 Program
Spring 2017

SYSTEM - Upper most level of the MLFQ OS simulation

*/
import java.io.*;
import java.util.*;

public class System1{

	// Subprograms
	private static Loader loader;						// loader module
	private static Scheduler scheduler;					// scheduler module
	private static Mem_Manager mem_manager;				// mem_manager module


	private static final String FILENAME = "data.txt";	// filename of the arriving jobs

	public static void main(String[] args) {

		System.out.println("Program Running...");

		BufferedReader br = null;
		FileReader fr = null;

		// connecting modules
		scheduler = new Scheduler(mem_manager,loader);
		mem_manager = new Mem_Manager(loader, scheduler);
		loader = new Loader(mem_manager,scheduler);
		
		try {
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(FILENAME));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] in = sCurrentLine.split("\\s+");
				// in[0] is null
				if(Integer.parseInt(in[1]) != 0) {
					loader.load(Integer.parseInt(in[1]),Integer.parseInt(in[2]), Long.parseLong(in[3]));
					
				} else {
					// no Arriving Jobs
				}
				
				scheduler.run();
			
				// Slow doen the Thread to let it process and print mem_stats every 200ms
				try {
		         	Thread.sleep(200);
		         	mem_manager.stats(System.currentTimeMillis());
		         } catch (InterruptedException e) {
		         	e.printStackTrace();
		         }
			}

		} catch (IOException e) {
			e.printStackTrace();
 
		} finally {
			try {
				if (br != null) br.close();
				if (fr != null) fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}