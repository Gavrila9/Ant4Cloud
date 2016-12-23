package Aco;

import java.util.ArrayList;
import java.util.List;


import org.workflowsim.Task;



public class Tool {
	//资源数量
	public static final int VmNum =10;
	//任务数量
	public static final int TaskNum = 25;
	//迭代次数
	public static final int IterationNum = 200;
	//节点数量
	public static final int PointNum=25;
	//蚂蚁数量
	public static final int AntNum= 10;	
	//alpha
	public static final double ALPHA = 1.0;
	//beta
	public static final double BETA = 2.0;
	//Q 信息所初始值
	public static final double Q = 1.0;
	//rho
	public static final double RHO = 0.5;		
	// 通过对xml转换得到的任务列表	
	public static List<Task> taskList = new ArrayList<Task>();	
	public static List<Task> getTaskList() {
		return taskList;
	}
	public static void setTaskList(List<Task> taskList) {
		Tool.taskList = taskList;
	}	
	//虚拟机列表
	public static List<Double> vmlist = new ArrayList<Double>();
	public static List<Double> getVmlist() {
		return vmlist;
	}
	public static void setVmlist(List<Double> vmlist) {
		Tool.vmlist = vmlist;
	}
	//当前进行任务分配方案
	public static int[] allot= new int[Tool.TaskNum];
	
	

	

}
