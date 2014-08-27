package schedulers;



public class Process {
	public enum Status{
		FUTURE,WAITING,RUNNING,BLOCKED,FINISHED;
	}
	int PID;
	long start;
	int timeToFinish;
	double priority;
	int waited = 0;
	int done;
	Status status = Status.FUTURE;
	
	public Process(int PID,long start,int ToFinish,int priority){
		this.PID = PID;
		this.start = start;
		this.timeToFinish = ToFinish;
		this.priority = priority;
		done=0;
	}
	
	public void work(){
		
		if(done >= timeToFinish)
			this.status = Status.FINISHED;
		else
			done++;
	}
	
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public int getRemainingTime(){
		int ret =  timeToFinish - done;
		if(ret < 0)
			return 0;
		return ret;
	}
	public int getPID(){
		return PID;
	}
	public long getStart(){
		return start;
	}
	public int getTimeToFinish(){
		return timeToFinish;
	}
	public double getPriority(){
		return this.priority;
	}
}
