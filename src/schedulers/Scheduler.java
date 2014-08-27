package schedulers;
import java.util.ArrayList;

import schedulers.Process.Status;


public abstract class Scheduler {
	long procTimer = 0;
	public int contextSwitches = 0;
	ArrayList<Process> q;
	public abstract void addProcess(Process proc);
	public abstract void killProcess(int PID);
	public abstract void sleepProcess(int PID);
	public abstract void wakeProcess(int PID);
	public abstract void iterate(long timer);
	public abstract ArrayList<Process> getProcesses();
	public abstract ArrayList<Process> getActiveProcesses();
	public abstract int getLastActivePID();
	
	
	protected abstract int getIndexByPID(int PID);
	public void setTimeSlice(int value) {
		// TODO Auto-generated method stub
		
	}
	
	public Scheduler(){
		q = new ArrayList<Process>();
		initTestProcesses();
	}
	
	public void reset(){
			q.clear();
			initTestProcesses();
			contextSwitches = 0;
	}
	
	protected void initTestProcesses(){
		q.add(new Process(1, 10, 100, 1));
		q.add(new Process(2, 15, 120, 1));
		q.add(new Process(3, 210, 160, 1));
		q.add(new Process(4, 190, 90, 1));
		q.add(new Process(5, 50, 100, 1));
		q.add(new Process(6, 120, 125, 1));
		q.add(new Process(7, 220, 190, 1));
		q.add(new Process(8, 250, 95, 1));
	}
	
	protected int updateStatuses(){
		int ret = 0;
		for(int i=0;i<q.size();i++){
			if(q.get(i).getStart()==this.procTimer){
				q.get(i).setStatus(Status.WAITING);
				ret = q.get(i).getPID();
			}
		}
		return ret;
	}
	
	public int getNewProcID()
	{
		boolean found = false;
		int id = 1;
		if(q==null)
			return 1;
		else
			for(int i = 1;;i++){
				found = false;
				for (Process proc : q) {
					if(proc.PID == i){
						found = true;
						break;
					}
				}
				if(!found)
					return i;
			}
	}
	public int getNumContextSwitches(){
		return contextSwitches;
	}
	public int getNumTicksForContextSwitch(){
		return 3;
	}
}
