package Aco;

import java.util.Random;






public class ACO {
	Random rand = new Random();
	double ETC[][] = new double[Tool.TaskNum][Tool.VmNum];
	
	 // 计算时间矩阵ETC[i][j]=workLoad[i]/computingPower[j]
	
	public void calETC()
	{
		for (int i = 0; i < Tool.TaskNum; i++) {
			for (int j = 0; j < Tool.VmNum; j++) {
				ETC[i][j] = Tool.taskList.get(i).getCloudletLength()/Tool.vmlist.get(j);
			}
		}
	}
	//计算每只蚂蚁的makespan
	public double calMakespan(int assignment[])
	{
		Tool.allot = assignment.clone();		
		double makespan = AcoplanningAlgorithmexample.calMakespan();
		return makespan;
	}
	//初始化蚁群
	Ant[] ants = new Ant[Tool.AntNum];	
	public void initAnts()
	{
		for (int i = 0; i < Tool.AntNum; i++) {
			ants[i] = new Ant();			
			ants[i].assignment[0] = rand.nextInt(Tool.VmNum);
		}
	}
	
	//重置蚁群
	public void resetAnts()
	{
		for (int i = 0; i < Tool.AntNum; i++) {
			for (int j = 0; j < Tool.TaskNum; j++) {
				ants[i].assignment[j] = 0;
			}
			ants[i].makespan = 0.0;
			ants[i].assignment[0] = rand.nextInt(Tool.VmNum);
		}
	}
	//初始化信息素矩阵
	public double pheromone[][] = new double[Tool.TaskNum][Tool.VmNum];
	
	public void initPheromone()
	{
		for (int i = 0; i < Tool.TaskNum; i++) {
			for (int j = 0; j < Tool.VmNum; j++) {
				pheromone[i][j] = 0.01;
			}
		}
	}
	
	//每只蚂蚁完成一个遍历
	public void onTour()
	{
		//每只蚂蚁移动的过程
		for (int i = 0; i <Tool.AntNum; i++) {
			//每个蚂蚁完成所有分配的访问
			Ant cAnt = ants[i];
			for (int j = 1; j < Tool.PointNum; j++) {
				cAnt.assignment[j] = selectNextRes(cAnt,j);
			}
			cAnt.makespan = calMakespan(cAnt.assignment);
		}
	}
	
	//更新信息素
	public void updatePheromone()
	{
		//信息素的挥发
		for (int i = 0; i < Tool.TaskNum; i++) {
			for (int j = 0; j < Tool.VmNum; j++) {
				pheromone[i][j] *= (1.0-Tool.RHO);
			}
		}
		
		//信息素的更新（最优解的）
		Ant bAnt = findBest();
		for (int j = 0; j < Tool.VmNum; j++) {
			pheromone[j][bAnt.assignment[j]] += Tool.Q/bAnt.makespan;
		}
	}
	
	//选怎下一个访问的节点
	public int selectNextRes(Ant ant,int PointIndex)
	{
		double denominator = 0.0;
		//先计算公式的分母部分
		for (int i = 0; i < Tool.VmNum; i++) {
			denominator += Math.pow(pheromone[PointIndex][i], Tool.ALPHA)*
					Math.pow((Tool.Q/ETC[PointIndex][i]), Tool.BETA);
		}
		//计算对于该任务来说每个资源被选择的概率
		double p[] = new double[Tool.VmNum];
		for (int i = 0; i < Tool.VmNum; i++) {
			p[i] = (Math.pow(pheromone[PointIndex][i], Tool.ALPHA)*
					Math.pow((Tool.Q/ETC[PointIndex][i]), Tool.BETA))/denominator;
		}
		//轮盘赌选择一个资源
		int selectRes = -1;
		double sump = 0.0;
		double val = rand.nextDouble();
		for (int i = 0; i < Tool.VmNum; i++) {
			sump += p[i];
			if (val <= sump) {
				selectRes = i;
				break;
			}
		}
		
		return selectRes;
	}
	
	//寻找最优解
	public Ant findBest()
	{
		double temp = ants[0].makespan;
		int loc = 0;
		for (int i = 0; i < Tool.AntNum; i++) {
			if (ants[i].makespan<temp) {
				temp = ants[i].makespan;
				loc = i;
			}
		}
		return ants[loc];
	}

	

}
