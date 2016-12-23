package Aco;


import java.io.File;


import java.io.FileWriter;
import java.io.IOException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;













import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.workflowsim.CondorVM;
import org.workflowsim.Job;
import org.workflowsim.WorkflowDatacenter;
import org.workflowsim.WorkflowEngine;
import org.workflowsim.WorkflowParser;
import org.workflowsim.WorkflowPlanner;
import org.workflowsim.examples.WorkflowSimBasicExample1;
import org.workflowsim.utils.ClusteringParameters;
import org.workflowsim.utils.OverheadParameters;
import org.workflowsim.utils.Parameters;
import org.workflowsim.utils.ReplicaCatalog;
public class AcoplanningAlgorithmexample extends WorkflowSimBasicExample1{
	

public static void main(String[] args) {
	
	
		
		try {
			ACO aco = new ACO();
			Ant best = new Ant();
			best.makespan = Double.MAX_VALUE;
			int bestNum = 0;
			
			FileWriter fw = new FileWriter("D:\\Aco_"+".txt",true);
			fw.write("D:/workspace/WorkflowSim-1.0-master/config/dax/Montage_"+Integer.toString(Tool.TaskNum)+".xml");
			fw.write("\r\n");
			initWorkflow();
			aco.calETC();
			aco.initPheromone();
			aco.initAnts();
			
			for (int i = 0; i < Tool.IterationNum; i++) {
				
				aco.onTour();
				
				Ant localAnt = new Ant();
				copy(localAnt, aco.findBest());;
				if (localAnt.makespan<best.makespan) {
					copy(best,localAnt);
					bestNum = i+1;
				}
				 
				
			
				printResult(best);
				
				fw.write("--------第"+(i+1)+"代--------");
				fw.write("分配方案:\n");
				for (int x = 0; x < best.assignment.length; x++) {
					fw.write(best.assignment[x]+",");
				}
				fw.write("\nMakespan值："+best.makespan+"\n");
				fw.write("\r\n");
				aco.updatePheromone();
				aco.resetAnts();
			}
			
			
			
			fw.write("****************\n");
			fw.write("最优解出现的代数：" + bestNum+"\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
	public static void printResult(Ant ant)
	{
		System.out.print("分配方案:");
		for (int i = 0; i < ant.assignment.length; i++) {
			System.out.print(ant.assignment[i]+",");
		}
		System.out.println();
		System.out.println("makespan:"+ant.makespan);
	}
	
	public static void copy(Ant a1,Ant a2)
	{
		a1.assignment = a2.assignment.clone();
		a1.makespan = a2.makespan;
	}
	
	public static void initWorkflow()
	{

		/**
         * However, the exact number of vms may not necessarily be vmNum If
         * the data center or the host doesn't have sufficient resources the
         * exact vmNum would be smaller than that. Take care.
         */
        int vmNum = Tool.VmNum;//number of vms;
        /**
         * Should change this based on real physical path
         */
        String daxPath = "D:/workspace/WorkflowSim-1.0-master/config/dax/Montage_"+Integer.toString(Tool.TaskNum)+".xml";;
        
        File daxFile = new File(daxPath);
        if(!daxFile.exists()){
            Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
            return;
        }

        /**
         * Since we are using HEFT planning algorithm, the scheduling algorithm should be static 
         * such that the scheduler would not override the result of the planner
         */
        Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.STATIC;
        Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.ACO;
        ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

        /**
         * No overheads 
         */
        OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);;
        
        /**
         * No Clustering
         */
        ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
        ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

        /**
         * Initialize static parameters
         */
        Parameters.init(vmNum, daxPath, null,
                null, op, cp, sch_method, pln_method,
                null, 0);
        ReplicaCatalog.init(file_system);

        // before creating any entities.
        int num_user = 1;   // number of grid users
        Calendar calendar = Calendar.getInstance();
        boolean trace_flag = false;  // mean trace events
        
        // Initialize the CloudSim library
        CloudSim.init(num_user, calendar, trace_flag);
		
		WorkflowParser wfp = new WorkflowParser(0);
		wfp.parse();
		
		Tool.setTaskList(wfp.getTaskList());
		
		Random bwRandom = new Random(System.currentTimeMillis());
		for (int i = 0; i < Tool.VmNum; i++) {
			//资源的的处理能力介于400~1200
			double ratio = bwRandom.nextDouble()*8+4;
			Tool.getVmlist().add(i, 100*ratio);
		}
	}
	
    ////////////////////////// STATIC METHODS ///////////////////////
    protected static List<CondorVM> createVM(int userId, int vms) {

        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<CondorVM> list = new LinkedList<CondorVM>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 1000;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        CondorVM[] vm = new CondorVM[vms];

        //Random bwRandom = new Random(System.currentTimeMillis());

        for (int i = 0; i < vms; i++) {
            //double ratio = bwRandom.nextDouble();
        	double mipsValue = Tool.getVmlist().get(i);
            vm[i] = new CondorVM(i, userId, mipsValue, pesNumber, ram, (long) (mipsValue), size, vmm, new CloudletSchedulerSpaceShared());
            list.add(vm[i]);
        }
        
        return list;
    }

    /**
     * Creates main() to run this example This example has only one datacenter
     * and one storage
     */
    public static double calMakespan() {

        
        try {
            // First step: Initialize the WorkflowSim package. 

            /**
             * However, the exact number of vms may not necessarily be vmNum If
             * the data center or the host doesn't have sufficient resources the
             * exact vmNum would be smaller than that. Take care.
             */
            int vmNum = Tool.VmNum;//number of vms;
            /**
             * Should change this based on real physical path
             */
            String daxPath ="D:/workspace/WorkflowSim-1.0-master/config/dax/Montage_"+Integer.toString(Tool.TaskNum)+".xml";
            
            File daxFile = new File(daxPath);
            if(!daxFile.exists()){
                Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
                return -1;
            }

            /**
             * Since we are using HEFT planning algorithm, the scheduling algorithm should be static 
             * such that the scheduler would not override the result of the planner
             */
            Parameters.SchedulingAlgorithm sch_method = Parameters.SchedulingAlgorithm.STATIC;
            Parameters.PlanningAlgorithm pln_method = Parameters.PlanningAlgorithm.ACO;
            ReplicaCatalog.FileSystem file_system = ReplicaCatalog.FileSystem.LOCAL;

            /**
             * No overheads 
             */
            OverheadParameters op = new OverheadParameters(0, null, null, null, null, 0);;
            
            /**
             * No Clustering
             */
            ClusteringParameters.ClusteringMethod method = ClusteringParameters.ClusteringMethod.NONE;
            ClusteringParameters cp = new ClusteringParameters(0, 0, method, null);

            /**
             * Initialize static parameters
             */
            Parameters.init(vmNum, daxPath, null,
                    null, op, cp, sch_method, pln_method,
                    null, 0);
            ReplicaCatalog.init(file_system);

            // before creating any entities.
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events
            
            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            WorkflowDatacenter datacenter0 = createDatacenter("Datacenter_0");

            /**
             * Create a WorkflowPlanner with one schedulers.
             */
            WorkflowPlanner wfPlanner = new WorkflowPlanner("planner_0", 1);
            /**
             * Create a WorkflowEngine.
             */
            WorkflowEngine wfEngine = wfPlanner.getWorkflowEngine();
            /**
             * Create a list of VMs.The userId of a vm is basically the id of
             * the scheduler that controls this vm.
             */
            List<CondorVM> vmlist0 = createVM(wfEngine.getSchedulerId(0), Parameters.getVmNum());
            
            /**
             * Submits this list of vms to this WorkflowEngine.
             */
            wfEngine.submitVmList(vmlist0, 0);

            /**
             * Binds the data centers with the scheduler.
             */
            wfEngine.bindSchedulerDatacenter(datacenter0.getId(), 0);
            
			

            CloudSim.startSimulation();


            List<Job> outputList0 = wfEngine.getJobsReceivedList();

            CloudSim.stopSimulation();
            //printJobList(outputList0);
           
            double[] fTime = new double[Tool.TaskNum+1];
            
           DecimalFormat dft = new DecimalFormat("###.##");
            String tab = "\t";
            Log.printLine("========== OUTPUT ==========");
            Log.printLine("TaskID" + tab + "vmID" + tab + "RunTime" + tab + "StartTime" + tab + "FinishTime" + tab + "Depth"+tab+"STATUS");
            
            for (int i = 0; i < outputList0.size(); i++) {
            	Job oneJob = outputList0.get(i);
            	Log.printLine(oneJob.getCloudletId() + tab
                		+ oneJob.getVmId() + tab 
                		+ dft.format(oneJob.getActualCPUTime()) + tab 
                		+ dft.format(oneJob.getExecStartTime()) + tab+tab
                        + dft.format(oneJob.getFinishTime()) + tab +tab
                        + oneJob.getDepth()+ tab +oneJob.getCloudletStatusString() );
            	
            	fTime[oneJob.getCloudletId()] = oneJob.getFinishTime();
			}
            
            double makespan = outputList0.get((outputList0.size()-1)).getFinishTime()-outputList0.get(0).getFinishTime();
            return makespan;
           

        } catch (Exception e) {
            Log.printLine("The simulation has been terminated due to an unexpected error");
            return -1;
        }
    }


	
}
