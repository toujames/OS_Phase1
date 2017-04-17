/*
James Touthang
CS-4323 Op Systems
MLFQ Phase 1 Program
Spring 2017

Loader - Manages Memory units 
*/

public class Loader{
	private final Mem_Manager mem_manager;
	private final Scheduler scheduler;


	public Loader(Mem_Manager mem_manager, Scheduler scheduler){
		this.mem_manager = mem_manager;
		this.scheduler = scheduler;
	}

	public void load(int jobID, int jobSize, long cpuTimeNeeded){
		
		if( (jobSize != 0) && mem_manager.allocate(jobSize) ) {
			
			scheduler.setup(jobID,jobSize, cpuTimeNeeded);
		}
	}
}