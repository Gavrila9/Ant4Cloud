package org.cloudbus.cloudsim;

import java.util.*;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

/**
 * 蚁群优化算法，用来求解任务分配给虚拟机达到时间最短的问题
 * @author Gavrila
 */
public class ACO {
	public class position{
		int vm;
		int task;
		public position(int a, int b){
			vm = a;
			task = b;
		}
	}
	private List<Ant> ants;//定义蚂蚁群
	private int antcount;//蚂蚁的数量
	private int Q = 100;
	private double[][] pheromone;//信息素矩阵
	private double[][] Delta;//总的信息素增量
	private int VMs;//虚拟机数量
	private int tasks;//任务个数
	public position[] bestTour;//最佳解
	private double bestLength;//最优解的长度（时间的大小）
	private List<? extends Cloudlet> cloudletList;
	private List<? extends Vm> vmList;
	/**
	 * 初始化矩阵
	 * @param antNum为系统要用到蚂蚁的数量
	 */
	public void init(int antNum, List<? extends Cloudlet> list1, List<? extends Vm> list2){
		//cloudletList = new ArrayList<? extends Cloudlet>;
		cloudletList = list1;
		vmList = list2;
		antcount = antNum;
		ants = new ArrayList<Ant>(); 
		VMs = vmList.size();
		tasks = cloudletList.size();
		pheromone = new double[VMs][tasks];
		Delta = new double[VMs][tasks];
		bestLength = 1000000;
		//初始化信息素矩阵
		for(int i=0; i<VMs; i++){
			for(int j=0; j<tasks; j++){
				pheromone[i][j] = 0.1;
			}
		}
		bestTour = new position[tasks];
		for(int i=0; i<tasks; i++){
			bestTour[i] = new position(-1, -1);
		}
		//随机放置蚂蚁  
        for(int i=0; i<antcount; i++){  
            ants.add(new Ant());  
            ants.get(i).RandomSelectVM(cloudletList, vmList);
        }  			
	}
	/**
	 * ACO的运行过程
	 * @param maxgen ACO的最多迭代次数
	 */
	public void run(int maxgen){
		for(int runTime=0; runTime<maxgen; runTime++){
			System.out.println("第"+runTime+"次：");
			//每只蚂蚁移动的过程
			for(int i=0; i<antcount; i++){
				for(int j=1; j<tasks; j++){	
					ants.get(i).SelectNextVM(pheromone);
				}
			}
			for(int i=0; i<antcount; i++){
				System.out.println("第"+i+"只蚂蚁");
				ants.get(i).CalTourLength();
				System.out.println("第"+i+"只蚂蚁的路程："+ants.get(i).tourLength);
				ants.get(i).CalDelta();
				if(ants.get(i).tourLength<bestLength){  
					//保留最优路径  
	                bestLength = ants.get(i).tourLength;  
	                System.out.println("第"+runTime+"代"+"第"+i+"只蚂蚁发现新的解："+bestLength);   
	                for(int j=0;j<tasks;j++){  
	                	bestTour[j].vm = ants.get(i).tour.get(j).vm;
	                    bestTour[j].task = ants.get(i).tour.get(j).task;
	                } 
	                //对发现最优解的路更新信息素
	                for(int k=0; k<VMs; k++){
	                	for(int j=0; j<tasks; j++){
	                		pheromone[k][j] = pheromone[k][j] + Q/bestLength;
	                	}
	                }  
				}
			}
			UpdatePheromone();//对每条路更新信息素
				
			//重新随机设置蚂蚁  
			for(int i=0;i<antcount;i++){  
				ants.get(i).RandomSelectVM(cloudletList, vmList);  
		    }  	
		}
	}
	/** 
     * 更新信息素矩阵 
     */  
	public void UpdatePheromone(){
		double rou=0.5;  
        for(int k=0; k<antcount; k++){
        	for(int i=0; i<VMs; i++){
        		for(int j=0; j<tasks; j++){
        			Delta[i][j] += ants.get(k).delta[i][j];
        		}
        	}
        }
        
        for(int i=0; i<VMs; i++){
        	for(int j=0; j<tasks; j++){
        		pheromone[i][j] = (1-rou)*pheromone[i][j] + Delta[i][j];
        	}
        }  
	}
	/** 
     * 输出程序运行结果 
     */  
    public void ReportResult(){  
        System.out.println("最优路径长度是"+bestLength);
        for(int j=0; j<tasks; j++)
        {
        	System.out.println(bestTour[j].task+"分配给："+bestTour[j].vm);
        }
    }  	
}
