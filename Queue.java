/* 
James Touthang
CS-4323 Op Systems
MLFQ Phase 1 Program
Spring 2017

Scheduler - schedules the jobs
*/

// Ques for the Process 

import java.util.LinkedList;
import java.io.*;

public class Queue extends LinkedList<Process>{
	private Scheduler scheduler;	// The scheduler module it interacts with
	private long quantum;			// the quantum number of the queue
	private long quantumClock;
	private int prioritylevel;
	private QueueType queueType;	// Quuetype for the 


	public static enum QueueType{
		CPU_QUEUE,
		BLOCKED_QUEUE
	}

	// Constructer for the Queue object.
	public Queue(Scheduler scheduler,long quantum, int prioritylevel, QueueType queueType){
		this.prioritylevel = prioritylevel;
		this.scheduler = scheduler;
		this.quantum = quantum;
		this.quantumClock = 0;
		this.queueType = queueType;
	}

	public void manageTimeSlice(Process currentProcess, long time){
		if(currentProcess.isStateChanged()) {
			quantumClock = 0;
			return;
		}

		quantumClock += time;
		// Need to change queue to higher prioriry queue
		if(quantumClock > quantum) {

			quantumClock = 0;
			Process process = remove();
			if(!process.isFinished()) {
				// increase cpu shots on process
				process.addShot();
				// move to lower queue
				scheduler.event(this,process,Scheduler.Interrupt.LOWER_PRIORITY);
			} else {
				// Process Completed!
				done(process);
			}

		}
	}

	// Simulate CPU Work
	public void doCPUWork(long time){
		Process process = element();
		process.doCPUWork(time);
		manageTimeSlice(process, time);
	}

	public void doBlockedWork(long time){
		Process process = element();
		process.doBlockedWork(time);
		manageTimeSlice(process, time);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public int getSize(){
		return size();
	}

	public void event(Process source, Scheduler.Interrupt interrupt){
		remove(source);	// process removed from queue
		switch(interrupt) {
			case PROCESS_BLOCKED:
				// process interes blocked state
				scheduler.event(this, source, Scheduler.Interrupt.PROCESS_BLOCKED);
				break;
			case PROCESS_READY:
				scheduler.event(this, source, Scheduler.Interrupt.PROCESS_READY);
				break;
		}
	}

	public void done(Process process){
		scheduler.stats(process);
	}

	@Override
	public boolean add(Process process){
		process.setParentQueue(this);		//  this process is in parent Queeue, higher priority
		return super.add(process);
	}

	public int getPriorityLevel() {
		return prioritylevel;
	}

	public QueueType getType() {
		return queueType;
	}

}