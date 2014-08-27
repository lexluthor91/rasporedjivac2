package schedulers;

import java.util.ArrayList;

import schedulers.Process.Status;

public class HRRN extends Scheduler {

	int lastActivePID = 0;
	long procTimer = 0;
	int timeSlice = 10;
	//int timeSliceLeft = timeSlice;
	int activePID = 0;
	
	public HRRN(){
		super();
		
		for (Process proc : q) {
			proc.priority = 1.0+((1.0*proc.waited)/proc.getRemainingTime());
		}
	}
	
	public HRRN(ArrayList<Process> alreadyActive){
		if(q != null){
			q.clear();
			q.addAll(alreadyActive);
		}
		else
		{
			q= new ArrayList<Process>();
			q.addAll(alreadyActive);
		}
	}
	
	@Override
	public void addProcess(Process proc) {
		q.add(proc);

	}

	@Override
	public void killProcess(int PID) {
		for(int i=0;i<q.size();i++){
			if(PID == q.get(i).getPID()){
				q.remove(i);
			}
		}
	}

	@Override
	public void sleepProcess(int PID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void wakeProcess(int PID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void iterate(long timer) {
		
		this.procTimer = timer;
		updateStatuses();
		for (Process proc : q) {
			if(proc.status == Status.WAITING && lastActivePID != proc.getPID() && proc.getRemainingTime()!=0){
				proc.waited+=1;
				proc.priority = 1.0+((1.0*proc.waited)/proc.getRemainingTime());
			}
		}//waiting times
		
		int remainingTime = 0;
		if(lastActivePID != 0){
			remainingTime=q.get(getIndexByPID(lastActivePID)).getRemainingTime();
			
		}
		if(lastActivePID==0 || q.get(getIndexByPID(lastActivePID)).getRemainingTime()==0){
			Process next = getNextProcess();
			if(next!=null)
				lastActivePID = next.getPID();
			if(next==null){
				lastActivePID = 0;
				return;
			}
			else if(lastActivePID != 0){
				System.out.println(lastActivePID);
				contextSwitches++;
			}
			
		}
		
		if(lastActivePID!=0){
			Process tempProc = q.get(getIndexByPID(lastActivePID));
			tempProc.work();
			//System.out.println("working");
		}
		
	}
	
	protected int updateStatuses(){
		for(int i=0;i<q.size();i++){
			if(q.get(i).getStart()==this.procTimer){
				q.get(i).setStatus(Status.WAITING);
				//System.out.println("aktivirao: "+q.get(i).getPID());
			}
		}
		return 0;
	}

	@Override
	public ArrayList<Process> getProcesses() {
		return q;
	}
	
	private Process getNextProcess(){
		double max = 0;
		int maxI = -1;
		for (int i = 0; i < q.size(); i++) {
			//System.out.print(q.get(i).getStatus()+" ");
			if(q.get(i).getRemainingTime() > 0 && max < q.get(i).priority){
				maxI = i;
				max = q.get(i).priority;
			}
		}
		//System.out.println();
		if(maxI != -1)
			return q.get(maxI);
		return null;
	}

	@Override
	public ArrayList<Process> getActiveProcesses() {
		ArrayList<Process> tempList = new ArrayList<Process>();
		
		for (int i = 0; i < q.size(); i++) {
			if(q.get(i).getStatus()!=Status.FUTURE)
				tempList.add(q.get(i));
		}
		
		return tempList;
	}

	@Override
	public int getLastActivePID() {
		return lastActivePID;
	}

	@Override
	protected int getIndexByPID(int PID) {
		for(int i=0;i<q.size();i++){
			if(PID == q.get(i).getPID()){
				return i;
			}
		}
		return 0;
	}
	
	public void setTimeSlice(int slice){
		timeSlice = slice;
	}

	public String toString(){
		return "HRRN";
	}
	
	public void reset(){
		lastActivePID = 0;
		q.clear();
		initTestProcesses();
		contextSwitches = 0;
}

}
