/* 
James Touthang
CS-4323 Op Systems
MLFQ Phase 1 Program
Spring 2017

Scheduler - schedules the jobs
*/
import java.util.LinkedList;
import java.io.*;
public class Scheduler{
	private final Mem_Manager mem_manager;
	private final Loader loader;

	// Queues
	private final Queue blockedQueue;					// blocked Queue
	private final Queue[] readyQueue;					// 4 readu subqueue

	private long clock;									// clock -_-
	private int jobsDelivered = 0;
	private long theClock;

	public static enum Interrupt {
		PROCESS_BLOCKED,
		PROCESS_READY,
		LOWER_PRIORITY
	}

	public Scheduler(Mem_Manager mem_manager, Loader loader){
		this.mem_manager = mem_manager;					// linking Mem_Manager
		this.loader = loader;							// link loader

		// Creats the Queues
		blockedQueue = new Queue(this,50,0,Queue.QueueType.BLOCKED_QUEUE);
		readyQueue = new Queue[5];											// size 4 array of queues
		readyQueue[0] = new Queue(this, 20, 0, Queue.QueueType.CPU_QUEUE);	//subqueue1
		readyQueue[1] = new Queue(this, 30, 1, Queue.QueueType.CPU_QUEUE);	//subqueue2
		readyQueue[2] = new Queue(this, 50, 2, Queue.QueueType.CPU_QUEUE);	//subqueue3
		readyQueue[3] = new Queue(this, 80, 3, Queue.QueueType.CPU_QUEUE);	//subqueue4

		readyQueue[4] = new Queue(this, 80, 3, Queue.QueueType.CPU_QUEUE);	//Job_Q

		clock = System.currentTimeMillis();										// initial clock s
		theClock = System.currentTimeMillis();
		
		BufferedWriter bw = null;
		FileWriter fw = null;
		// Prints the Header for the JOBS_LOG
		try {
			fw = new FileWriter("JOBS_LOG",true);
			bw = new BufferedWriter(fw);
			bw.write("James Touthang\nPhase 1 Program;\n");
			bw.write("JOBS_LOG: \n");

			// Header
			String out = String.format("%-6s\t%-15s\t%-15s\t%-15s\t%-10s\n",
				"Job ID",
				"Time Entered",
				"Time Left",
				"Time Executed",
				"CPU Shots");
			bw.write(out);
			for(int i=0; i < 70; i++){
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

	public void setup(int jobID, int jobSize, long cpuTimeNeeded){
		Process process = new Process(jobID,jobSize, cpuTimeNeeded);
		readyQueue[0].add(process);					// added to ready_Q
		
	}

	// Prints process INFO to JOBS_LOG
	public void stats(Process process){
		jobsDelivered++;									// increment as job finishes
		//mem_manager.deallocate(process.getJobSize());
		BufferedWriter bw = null;
		FileWriter fw = null;
		// PROCESS DONE!!
	
		try{
			process.timeLeft();						// set the time left on Process
			fw = new FileWriter("JOBS_LOG",true);
			bw = new BufferedWriter(fw);

					// deallocated memory space

			String out = String.format("%-6d\t%-15d\t%-15d\t%-15d\t%-10d\n",
				process.getJobID(),
				process.getTimeEntered(),
				process.getTimeLeft(),
				process.getExecuteTime(),
				process.getCPUShots());
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

	public void run(){
		long time = System.currentTimeMillis();
	    long workTime = time - clock;
	    clock = time;

	    //work through blocked and cpu processes in parallel

	    //pass some time on the blocked processes
	    if (!blockedQueue.isEmpty()) {
	        blockedQueue.doBlockedWork(workTime);
	    }

	    //do cpu work
	    for (int i = 0; i < 5; i++) {
	        Queue queue = readyQueue[i];
	        if (!queue.isEmpty()) {
	            queue.doCPUWork(workTime);
	            break;
	        }
	    }
	}

	public int getJobsDelivered(){
		return jobsDelivered;
	}

	public int getJob_QSize(){
		return readyQueue[4].getSize();
	}
	public int getBlocked_QSize(){
		return blockedQueue.getSize();
	}

	public int getReady_QSize(){
		return readyQueue[0].getSize() + 
			readyQueue[1].getSize() +
			readyQueue[2].getSize() +
			readyQueue[3].getSize();
	}

	public long getClock(){
		return theClock;
	}

	public boolean allEmpty(){
		for(Queue queue : readyQueue){
			if(!queue.isEmpty()){
				return false;
			}
		}

		return blockedQueue.isEmpty();
	}
    public void add(Process process) {
        readyQueue[0].add(process);
    }

	public void event(Queue queue, Process process, Interrupt interrupt) {
		switch(interrupt) {
			case PROCESS_BLOCKED:
				blockedQueue.add(process);
				break;
			case PROCESS_READY:
				add(process);
				break;
			case LOWER_PRIORITY:
				if(queue.getType() == Queue.QueueType.CPU_QUEUE) {
					int priorityLevel = Math.min(2, queue.getPriorityLevel() + 1);
					readyQueue[priorityLevel].add(process);
				} else {
					blockedQueue.add(process);
				}
				break;
		}
	}
}