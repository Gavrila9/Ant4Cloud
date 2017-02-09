package org.cloudbus.cloudsim;

import java.util.*;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 *蚂蚁类
 *@author Gavrila
 */
 public class Ant{
	public class position{
		public int vm;
		public int task;
		public position(int a, int b){
			vm = a;
			task = b;
		}
	} 
	public double[][] delta;//每个节点增加的信息素
	public int Q = 100;
	public List<position> tour;//蚂蚁获得的路径（解，任务分配给虚拟机的分法）
	public double tourLength;//蚂蚁获得的路径长度（分配好后，总的花费时间）
	public long[] TL_task;//每个虚拟机的任务总量
	public List<Integer> tabu;//禁忌表
	private int VMs;//城市的个数（相当于虚拟机的个数）
	private int tasks;//任务个数
	private List<? extends Cloudlet> cloudletList;	//云任务列表
	private List<? extends Vm> vmList;				//虚拟机列表
	/**
	 *随机分配蚂蚁到某个节点中，同时完成蚂蚁包含字段的初试化工作
	 *@param list1 任务列表
	 *@param list2 虚拟机列表
	 */
	public void RandomSelectVM(List<? extends Cloudlet> list1, List<? extends Vm> list2){
		cloudletList = list1;
		vmList = list2;
		VMs = vmList.size();
		tasks = cloudletList.size();
		delta = new double[VMs][tasks];
		TL_task = new long[VMs];
		for(int i=0; i<VMs; i++)TL_task[i] = 0;
		tabu = new ArrayList<Integer>();
		tour=new ArrayList<position>();
		
		//随机选择蚂蚁的位置
		int firstVM = (int)(VMs*Math.random());
		int firstExecute = (int)(tasks*Math.random());
		tour.add(new position(firstVM, firstExecute));
		tabu.add(new Integer(firstExecute));
		TL_task[firstVM] += cloudletList.get(firstExecute).getCloudletLength();
	}
	/**
	  * calculate the expected execution time and transfer time of the task on vm
	  * @param vm 虚拟机序号
	  * @param task 任务序号
	  */
	public double Dij(int vm, int task){
		double d;
	    d = TL_task[vm]/vmList.get(vm).getMips() + cloudletList.get(task).getCloudletLength()/vmList.get(vm).getBw();
		return d;
	}
	 /**
	  * 选择下一个节点
	  * @param pheromone 全局的信息素信息
	  */
	  public void SelectNextVM(double[][] pheromone){
		  double[][] p;//每个节点被选中的概率
		  p = new double[VMs][tasks];
		  double alpha = 1.0;
		  double beta = 1.0;
		  double sum = 0;//分母
		  //计算公式中的分母部分  
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  if(tabu.contains(new Integer(j))) continue;
				  sum += Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta);
			  }
		  }
		  //计算每个节点被选的概率
		  for(int i=0; i<VMs; i++){
			  for(int j=0; j<tasks; j++){
				  p[i][j] = Math.pow(pheromone[i][j], alpha)*Math.pow(1/Dij(i,j),beta)/sum;
				  if(tabu.contains(new Integer(j)))p[i][j] = 0;
			  }
		  }
		double selectp = Math.random();
        //轮盘赌选择一个VM
        double sumselect = 0;
        int selectVM = -1;
        int selectTask = -1;
        boolean flag=true;
        for(int i=0; i<VMs&&flag==true; i++){
        	for(int j=0; j<tasks; j++){
        		sumselect += p[i][j];
        		if(sumselect>=selectp){
        			selectVM = i;
        			selectTask = j;
        			flag=false;
        			break;
        		}
        	}
        }
        if (selectVM==-1 | selectTask == -1)  
            System.out.println("选择下一个虚拟机没有成功！");
    		tabu.add(new Integer(selectTask));
		tour.add(new position(selectVM, selectTask));
		TL_task[selectVM] += cloudletList.get(selectTask).getCloudletLength();  		
	  }
	  
	  
	  
	public void CalTourLength(){
		System.out.println();
		double[] max;
		max = new double[VMs];
		for(int i=0; i<tour.size(); i++){
			max[tour.get(i).vm] += cloudletList.get(tour.get(i).task).getCloudletLength()/vmList.get(tour.get(i).vm).getMips(); 
		}		
		tourLength = max[0];
		for(int i=0; i<VMs; i++){
			if(max[i]>tourLength)tourLength = max[i];
			System.out.println("第"+i+"台虚拟机的执行时间："+max[i]);
		}
		return;
	}
	/**
	 * 计算信息素增量矩阵
	 */
    public void CalDelta(){
    	for(int i=0; i<VMs; i++){
    		for(int j=0; j<tasks; j++){
    			if(i==tour.get(j).vm&&tour.get(j).task==j)delta[i][j] = Q/tourLength;
    			else delta[i][j] = 0;
    		}
    	}
    }
 }
