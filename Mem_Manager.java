/*
James Touthang
CS-4323 Op Systems
MLFQ Phase 1 Program
Spring 2017

Mem_Manager - Manages Memory units 
*/
import java.io.*;
public class Mem_Manager{

	private final Scheduler scheduler;
	private final Loader loader;
	private int allocatedMem = 0;  						// used memory 
	private int freeMemory = 1024;						// memory free

	public Mem_Manager(Loader loader, Scheduler scheduler){
		this.scheduler = scheduler;
		this.loader = loader;

		BufferedWriter bw = null;
		FileWriter fw = null;
		// Prints the Header for the JOBS_LOG
		try {
			fw = new FileWriter("SYS_LOG",true);
			bw = new BufferedWriter(fw);
			bw.write("James Touthang\nPhase 1 Program;\n");
			bw.write("SYS_LOG: \n");

			// Header
			String out = String.format("%-5s\t%-20s\t%-15s\t%-15s\t%-15s\t%-15s\t%-15s\n",
				"Time",
				"Allocated Memory",
				"Free Memory",
				"Job_Q Jobs",
				"Blocked_Q Jobs",
				"Ready_Q Jobs",
				"Delivered Jobs");
			bw.write(out);
			for(int i=0; i < 112; i++){
				bw.write("-");
			}
			bw.write("\n");			
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	* @param units	The allocated units needed
	* Returns true if available, otherwise false
	*/
	public boolean allocate(int units){
		freeMemory = freeMemory - units;
		allocatedMem = allocatedMem + units;
		return units < freeMemory;
	}

	/**
	* @param units	The deallocated units needed
	*/
	public void deallocate(int units){
		allocatedMem = allocatedMem + units;
		freeMemory = freeMemory - units;
	}

	/**
	* Ouputs statistic info to SYS_LOG
	*/
	public void stats(long currentTime){
		BufferedWriter bw = null;
		FileWriter fw = null;
		// PROCESS DONE!!
		//scheduler.deallocate(process.getjobSize());	// deallocate space in memroy
		try{
			fw = new FileWriter("SYS_LOG",true);
			bw = new BufferedWriter(fw);

			String out = String.format("%-5d\t%-20d\t%-15d\t%-15d\t%-15d\t%-15d\t%-15d\n",
				currentTime - scheduler.getClock(),
				allocatedMem,
				freeMemory,
				scheduler.getJob_QSize(),
				scheduler.getBlocked_QSize(),
				scheduler.getReady_QSize(),
				scheduler.getJobsDelivered());
			bw.write(out);	
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) bw.close();
				if (fw != null) fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public int getAllocatedMem(){
		return allocatedMem;
	}

	public int getfreeMemory(){
		return freeMemory;
	}
}