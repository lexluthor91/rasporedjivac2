package schedulers;

import java.util.ArrayList;

import schedulers.Process.Status;

public class Feedback extends Scheduler {
	ArrayList<ArrayList<Process>> qs;
	int lastActivePID = 1;
	long procTimer = 0;
	int timeSlice = 10;
	// int timeSliceLeft = timeSlice;
	int activePID = 0;
	int activeQ = 0;
	int numOfQs = 4;
	int remainingTime = 0;

	public Feedback() {
		qs = new ArrayList<ArrayList<Process>>(numOfQs);
	}

	public Feedback(ArrayList<Process> alreadyActive) {
			for (Process proc : alreadyActive) {
				if(proc.getRemainingTime() > 0 && proc.getStatus() != Status.FUTURE && proc.getStatus() != Status.FINISHED){
					lastActivePID = proc.getPID();
					break;
				}
				
			}
			qs = new ArrayList<ArrayList<Process>>();
			for(int i = 0;i<numOfQs;i++){
				if(i==0)
					qs.add(new ArrayList<Process>(alreadyActive)) ;
				else
					qs.add(new ArrayList<Process>()) ;
				
			}
			//qs.get(0).addAll(alreadyActive);
			System.out.println(qs.size());
			for (ArrayList<Process> list : qs) {
				System.out.println(list.size());
			}
			
			
			
	}

	@Override
	public void addProcess(Process proc) {
		qs.get(0).add(proc);

	}

	@Override
	public void killProcess(int PID) {
		for (int j = 0; j < qs.size(); j++) {
			ArrayList<Process> q = qs.get(j);
			for (int i = 0; i < q.size(); i++) {
				if (PID == q.get(i).getPID()) {
					q.remove(i);
				}
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
		//ako je proces zavrsio
		/*System.out.println("#######");
		for (ArrayList<Process> list : qs) {
			System.out.print(list.size()+" ");
		}
		System.out.println("\n#######");*/
		System.out.println(timer+": "+lastActivePID);
		
		Process current = null;
		if(lastActivePID!=0)
			current = qs.get(getProcessQPriorityByPID(lastActivePID)).get(getIndexByPID(lastActivePID));
		if(remainingTime == 0 || (current != null && current.getRemainingTime()==0)){
			if(getProcessQPriorityByPID(lastActivePID)+1 < numOfQs && lastActivePID!=0){
				//System.out.println("PID: "+lastActivePID);
				int lvl = getProcessQPriorityByPID(lastActivePID);
				qs.get(lvl).remove(getIndexByPID(lastActivePID));
				qs.get(lvl+1).add(current);
			}
			int i = 0;
			for (ArrayList<Process> lista : qs) {
				for (Process proc : lista) {
					//System.out.println(proc);
					if(proc != null)
						proc.priority = i;
				}
				i++;
				//System.out.println();
			}
			
			Process next = getNext();
			//System.out.println(next);
			if (next != null){
				if(lastActivePID!=0){
					contextSwitches++;
				}
				lastActivePID = next.getPID();
				remainingTime = calculateRemainingTime(getProcessQPriorityByPID(lastActivePID));
				
			}
			if (next == null) {
				lastActivePID = 0;
				return;
			}
			
		}
		if (lastActivePID != 0) {
			
			
			Process tempProc = qs.get(getProcessQPriorityByPID(lastActivePID)).get(getIndexByPID(lastActivePID));
			tempProc.work();
			remainingTime--;
			// System.out.println("working");
		}

	}

	protected int updateStatuses() {
		for (ArrayList<Process> q : qs) {
			for (int i = 0; i < q.size(); i++) {
				if (q.get(i) != null && q.get(i).getStart() == this.procTimer) {
					q.get(i).setStatus(Status.WAITING);
					// System.out.println("aktivirao: "+q.get(i).getPID());
				}
			}
		}
		return 0;
	}

	@Override
	public ArrayList<Process> getProcesses() {
		ArrayList<Process> tempList = new ArrayList<Process>();
		for (ArrayList<Process> q : qs) {
			for (int i = 0; i < q.size(); i++) {
				tempList.add(q.get(i));
			}
		}
		return tempList;
	}

	private Process getNext(){
		Process retProc = null;
		
		if(getTopPriorityWithProc()==-1)
			return retProc;
		
		ArrayList<Process> lista = qs.get(getTopPriorityWithProc());
		boolean nextProc = false;
		for (Process process : lista) {
			if(getProcessQPriorityByPID(lastActivePID) == getTopPriorityWithProc()){
				for (Process proc : lista) {
					if (nextProc && proc.getRemainingTime()>0 && proc.getStatus() != Status.FUTURE && proc.getStatus() != Status.FINISHED && proc.getStatus() != Status.BLOCKED) {
						System.out.println(proc.PID);
						nextProc = false;
						retProc = proc;
						return retProc;
					}
					if (proc.getPID() == lastActivePID)
						nextProc = true;
				}
			}
			else{
				if(lista.size() > 0){
					for (Process proc : lista) {
						if(proc.status != Status.FUTURE && proc.status != Status.FINISHED)
							return proc;
					}
					//return lista.get(0);
				}
			}
		}
		
		
		return retProc;
	}
	/*
	private Process getNextProcess() {
		int i = 0;
		Process retProc = null;

		for (ArrayList<Process> lista : qs) {
			boolean nextProc = false;
			for (Process proc : lista) {
				if(proc==null)
					continue;
				if(proc!=null)
					proc.priority = i;
				// Ako je prethodni proces bio sa ovog nivoa
				if (i == getProcessQPriorityByPID(lastActivePID)) {
					if (nextProc) {
						System.out.println(proc.PID);
						nextProc = false;
						retProc = proc;
						return retProc;
					}
					if (proc.getPID() == lastActivePID)
						nextProc = true;

				} else {
					System.out.println(proc.PID);
					return proc;
				}
			}
			i++;
		}
		return retProc;
	}
	*/
	private int getTopPriorityWithProc(){
		int i=0;
		for (ArrayList<Process> q : qs) {
			if(q.size()>0){
				for(int j=0;j<q.size();j++){
					if(q.get(j).getRemainingTime()>0 && q.get(j).status != Status.FUTURE){
						return i;
					}
				}
			}
				
			i++;
		}
		return -1;
	}
	
	private int calculateRemainingTime(int ql){
		return (int) (timeSlice * Math.pow(2, ql));
	}
	
	private int getProcessQPriorityByPID(int pid) {
		int retVal = 0;
		int i = 0;
		for (ArrayList<Process> lista : qs) {
			for (Process proc : lista) {
				if (proc!=null && pid == proc.getPID()) {
					retVal = i;
					break;
				}
			}
			i++;
		}

		return retVal;
	}

	@Override
	public ArrayList<Process> getActiveProcesses() {
		ArrayList<Process> tempList = new ArrayList<Process>();
		for (ArrayList<Process> q : qs) {
			for (int i = 0; i < q.size(); i++) {
				if (q.get(i) != null && q.get(i).getStatus() != Status.FUTURE)
					tempList.add(q.get(i));
			}
		}
		return tempList;
	}

	@Override
	public int getLastActivePID() {
		return lastActivePID;
	}

	@Override
	protected int getIndexByPID(int PID) {
		for (ArrayList<Process> q : qs) {
			for (int i = 0; i < q.size(); i++) {
				if (q.get(i)!=null && PID == q.get(i).getPID()) {
					return i;
				}
			}
		}

		return 0;
	}

	public void setTimeSlice(int slice) {
		timeSlice = slice;
	}
	
	public void reset(){
		for (ArrayList<Process> q : qs) {
			q.clear();
		}
		lastActivePID = 1;
		remainingTime = 0;
		ArrayList<Process> q = qs.get(0);
		q.add(new Process(1, 10, 100, 1));
		q.add(new Process(2, 15, 120, 1));
		q.add(new Process(3, 210, 160, 1));
		q.add(new Process(4, 190, 90, 1));
		q.add(new Process(5, 50, 100, 1));
		q.add(new Process(6, 120, 125, 1));
		q.add(new Process(7, 220, 190, 1));
		q.add(new Process(8, 250, 95, 1));
		contextSwitches = 0;
	}
	
	public int getNewProcID()
	{
		boolean found = false;
		int id = 1;
		if(qs==null)
			return 1;
		else
			for(int i = 1;;i++){
				found = false;
				for (ArrayList<Process> q : qs) {
					for (Process proc : q) {
						if(proc.PID == i){
							found = true;
							break;
						}
					}
					if(found){
						break;
					}
				}
				
				if(!found)
					return i;
			}
	}

	public String toString(){
		return "FB";
	}

}
