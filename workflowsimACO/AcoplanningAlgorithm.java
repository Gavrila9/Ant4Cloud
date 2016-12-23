package Aco;




import org.workflowsim.Task;
import org.workflowsim.planning.BasePlanningAlgorithm;










public class AcoplanningAlgorithm extends BasePlanningAlgorithm{
	
	public void run() throws Exception {
		// TODO Auto-generated method stub
		
		for (int i = 0; i < Tool.TaskNum; i++) {
			Task task = (Task) getTaskList().get(i);
			int vmId = Tool.allot[i];			
			task.setVmId(vmId);
		}
    	
	}
	
}
