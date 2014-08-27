package schedulers;

import java.util.ArrayList;

import schedulers.Process.Status;

public class SRT extends Scheduler {
	int lastActivePID = 0;
	int newProc = 0;
	public SRT(){
		super();
	}
	public SRT(ArrayList<Process> alreadyActive){
		this.q = new ArrayList<Process>();
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
	public void sleepProcess(int PID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void wakeProcess(int PID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void iterate(long timer) {
		procTimer = timer;
		//System.out.println(newProc);
		if(newProc != 0){
			q.get(getIndexByPID(lastActivePID)).setStatus(Status.WAITING);
			newProc = 0;
			lastActivePID = 0;
		}
		
		Process tmp = getNextProcess();
		if(tmp==null){
			lastActivePID = 0;
			return;
		}
		for(int i = 0;i<q.size();i++){
			if(q.get(i).getStatus() == Status.RUNNING){
				tmp = q.get(i);
			}
		}
		if(tmp.getPID() != lastActivePID && lastActivePID != 0){
			//System.out.println(lastActivePID+", "+tmp.getPID());
			contextSwitches++;
		}
		lastActivePID = tmp.getPID();
		tmp.setStatus(Status.RUNNING);
		tmp.work();
		
		newProc = updateStatuses();
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

	protected Process getNextProcess(){
		if(lastActivePID==0){
			return getShortestProc();
		}
		else if(q.get(getIndexByPID(lastActivePID)).getStatus() == Status.FINISHED ){
			return getShortestProc();
		}
		else
		return q.get(getIndexByPID(lastActivePID));
	}
	protected Process getShortestProc(){
		int minTime,minPid;
		minTime = q.get(0).getRemainingTime();
		minPid = q.get(0).getPID();
		
		for (int i = 0; i < q.size(); i++) {
			if((q.get(i).getStatus()==Status.WAITING || q.get(i).getStatus()==Status.RUNNING)){
				minTime = q.get(i).getRemainingTime();
				minPid = q.get(i).getPID();
				break;
			}
			if(i==q.size()-1){
				return null;
			}
		}
		for (int i = 0; i < q.size(); i++) {
			if(q.get(i).getRemainingTime() < minTime && (q.get(i).getStatus()==Status.WAITING || q.get(i).getStatus()==Status.RUNNING)){
				minTime = q.get(i).getRemainingTime();
				minPid = q.get(i).getPID();
			}
		}
		System.out.println(minPid);
		return q.get(getIndexByPID(minPid));
	}

	public String toString(){
		return "SRT";
	}
	
	public void reset(){
		q.clear();
		initTestProcesses();
		lastActivePID = 0;
		newProc = 1;
		contextSwitches = 0;
}
	
}
