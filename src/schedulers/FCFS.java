package schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import schedulers.Process.Status;

public class FCFS extends Scheduler {
	//private ArrayList<Process> 
	int lastActivePID = 0;
	long procTimer = 0;
	
	public FCFS(){
		super();
		
		Collections.sort(q, new Comparator<Process>() {
	        @Override
	        public int compare(Process  p1, Process p2)
	        {
	            return (int)(p1.getStart()-p2.getStart());
	        }
	    });
	}
	public FCFS(ArrayList<Process> alreadyActive){
		q = new ArrayList<Process>();
		q.addAll(alreadyActive);
		Collections.sort(q, new Comparator<Process>() {
	        @Override
	        public int compare(Process  p1, Process p2)
	        {
	            return (int)(p1.getStart()-p2.getStart());
	        }
	    });
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
		procTimer = timer;
		boolean gotWork = false;
		for(int i=0;i<q.size();i++){
			Process tempProc = q.get(i);
			if(tempProc.status==Status.WAITING){
				if(lastActivePID!=tempProc.getPID())
						contextSwitches++;
				lastActivePID = tempProc.getPID();
				
				tempProc.work();
				gotWork = true;
				break;
			}
		}
		updateStatuses();
		if(!gotWork){
			lastActivePID = 0;
		}
	}

	@Override
	public ArrayList<Process> getProcesses() {
		return q;
	}

	@Override
	public int getLastActivePID() {
		return lastActivePID;
	}

	
	protected int updateStatuses(){
		for(int i=0;i<q.size();i++){
			if(q.get(i).getStart()==this.procTimer)
				q.get(i).setStatus(Status.WAITING);
		}
		return 0;
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
	protected int getIndexByPID(int PID) {
		for(int i=0;i<q.size();i++){
			if(PID == q.get(i).getPID()){
				return i;
			}
		}
		return 0;
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
		return "FCFS";
	}
	
}
