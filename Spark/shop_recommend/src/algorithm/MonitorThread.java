package algorithm;

import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.spark.deploy.yarn.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Utils;

public class MonitorThread implements Runnable {
	private Logger log = LoggerFactory.getLogger(getClass());
	private ApplicationId appId;
	private Client client ;
	
	public MonitorThread() {
	}
	public MonitorThread(ApplicationId applicationId,Client client) {
		this.appId = applicationId;
		this.client = client;
	}
	@Override
	public void run() {
		long interval = 1000;// 更新Application 状态间隔
		int count =0; // 时间
		ApplicationReport report = null;
		while (true) {
			try {
				Thread.sleep(interval);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				report = client.getApplicationReport(appId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			YarnApplicationState state = report.getYarnApplicationState();
			log.info("Thread:"+Thread.currentThread().getName()+
						appId.toString()+"，任务状态是："+state.name());
			// 完成/ 失败/杀死
			if (state == YarnApplicationState.FINISHED || state == YarnApplicationState.FAILED
					|| state == YarnApplicationState.KILLED) {
				Utils.cleanupStagingDir(appId);
				// return (state, report.getFinalApplicationStatus);
				//  更新 app状态
				log.info("Thread:"+Thread.currentThread().getName()+
						appId.toString()+"完成，任务状态是："+state.name());
				Utils.updateAppStatus(appId.toString(), state.name());
				return;
			}
			// 获得ApplicationID后就说明已经是SUBMITTED状态了
			if ( state == YarnApplicationState.ACCEPTED) {
		        //  更新app状态
				if(count<Integer.parseInt(Utils.getProperty("als.accepted.progress"))){
					count++;
					Utils.updateAppStatus(appId.toString(), count+"%" );
				}
		      }
			if ( state == YarnApplicationState.RUNNING) {
		        //  更新app状态
				if(count<Integer.parseInt(Utils.getProperty("als.runing.progress"))){
					count++;
					Utils.updateAppStatus(appId.toString(), count+"%" );
				}else {
					Utils.updateAppStatus(appId.toString(), Utils.getProperty("als.runing.progress")+"%" );
				}
		      }
		}
	}

}
