/*
Process Control Block (The Job)!!
James Touthang
Process Object
*/
import java.util.Random; 	// used for random blocking time
public class Process{
	private int jobID;					// ID of Job
	private int jobSize;			// mem units requred size
	
    private Random random;

	private long timeEntered;		// Time Job Entered
	private long timeLeft;			// Time Job Left CPU
	private long executeTime;		// Time Job Executed
	private int cpuShots = 1;			// Number of CPU shots used in job.

	private long cpuTimeNeeded;
	private long blockingTimeNeeded;

	private Queue queue;
	private boolean stateChanged;


	public Process(int jobID, int jobSize, long cpuTimeNeeded){
		this.jobID = jobID;
		this.jobSize = jobSize;
		this.cpuTimeNeeded = cpuTimeNeeded;
		timeEntered = System.currentTimeMillis();
	}

	public void setParentQueue(Queue queue) {
		this.queue = queue;
	}

	public boolean isFinished() {
		return cpuTimeNeeded == 0;
	}


    public void doCPUWork(long time) {
        stateChanged = false;
        if (blockingTimeNeeded == 0) {
            cpuTimeNeeded -= time;
            cpuTimeNeeded = cpuTimeNeeded > 0 ? cpuTimeNeeded : 0;

            if (!isFinished()) {
                //25% chance to enter blocked state
                if (Math.random() < 0.25) {
                    // System.out.println("Process Blocked!");
                    blockingTimeNeeded = 100; //random.nextInt(1000);
                    //process entered blocked state
                    queue.event(this, Scheduler.Interrupt.PROCESS_BLOCKED);
                    stateChanged = true;
                }
            }
        }
    }

    public void doBlockedWork(long time) {
        stateChanged = false;
        blockingTimeNeeded -= time;
        blockingTimeNeeded = blockingTimeNeeded > 0 ? blockingTimeNeeded : 0;
        //process entered running state
        if (blockingTimeNeeded == 0) {
            queue.event(this, Scheduler.Interrupt.PROCESS_READY);
            stateChanged = true;
        }
    } 

    public boolean isStateChanged() {
		return stateChanged;
	}  	

	public void addShot(){
		cpuShots++;
	}

	public void timeLeft(){
		timeLeft = System.currentTimeMillis();
	}



	public void setCPUShots(int shots){ cpuShots = shots; }
	// accessor methods for the PCB
	public int getJobID() { return jobID; }
	public int getJobSize() { return jobSize; }
	public long getTimeEntered() { return timeEntered; }
	public long getTimeLeft() { return timeLeft; }
	public long getExecuteTime() { return timeLeft - timeEntered; }
	public long getCPUShots() { return cpuShots; }

}