package schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import schedulers.Process.Status;

public class RoundRobin extends Scheduler {
	//private ArrayList<Process> 
	int lastActivePID = 0;
	long procTimer = 0;
	int timeSlice = 10;
	int timeSliceLeft = timeSlice;
	int activePID = 0;
	
	public RoundRobin(){
		super();
		
		Collections.sort(q, new Comparator<Process>() {
	        @Override
	        public int compare(Process  p1, Process p2)
	        {
	            return (int)(p1.getStart()-p2.getStart());
	        }
	    });
	}
	
	public RoundRobin(ArrayList<Process> alreadyActive){
		
			q= new ArrayList<Process>();
			q.addAll(alreadyActive);
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
	public void iterate(long timer) {
		this.procTimer = timer;
		updateStatuses();
		if(timeSliceLeft==0 || lastActivePID==0){
			Process next = getNextProcess();
			//System.out.println("unutra"+timeSliceLeft);
			if(next==null){
				lastActivePID = 0;
				return;
			}
			else{
				contextSwitches++;
			}
			
			lastActivePID = next.getPID();
			timeSliceLeft = timeSlice;
		}
		if(lastActivePID!=0){
			Process tempProc = q.get(getIndexByPID(lastActivePID));
			tempProc.work();
			timeSliceLeft--;
			if(tempProc.getRemainingTime()==0){
				timeSliceLeft=0;
			}
		}
		//System.out.println(timeSliceLeft);
	}

	@Override
	public ArrayList<Process> getProcesses() {
		return q;
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
	
	private Process getNextProcess(){
		for (int i = getIndexByPID(lastActivePID); i < q.size(); i++) {
			if(q.get(i).getStatus()==Status.WAITING && q.get(i).getPID()!=lastActivePID && q.get(i).getRemainingTime()>0)
				return q.get(i);
		}
		return null;
	}

	@Override
	public int getLastActivePID() {
		return lastActivePID;
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

	@Override
	public void sleepProcess(int PID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void wakeProcess(int PID) {
		// TODO Auto-generated method stub
		
	}

	public String toString(){
		return "RR";
	}

}
