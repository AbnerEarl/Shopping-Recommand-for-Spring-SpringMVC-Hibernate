package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.yarn.api.records.ApplicationId;
//import org.apache.hadoop.yarn.api.records.ApplicationReport;
//import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.yarn.Client;
import org.apache.spark.deploy.yarn.ClientArguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.fommil.netlib.BLAS;
import com.google.common.collect.Sets;

import algorithm.MonitorThread;
import datastructure.FixSizePriorityQueue;
import model.MIdRated;
import model.Movie;

public class Utils {
	public static final String DOUBLECOLON = "::";
	public static final String COLON = ":";
	public static final String COMMA = ",";
	private static final String SUBFIX = "part-00000";
	
	private static  String userFeaturePath =null;
	private static  String productFeaturePath = null;
	public static  String RMSEPATH =null;
	private static Configuration configuration = null;

	private static Logger log = LoggerFactory.getLogger(Utils.class);
	
	
	// 允许多用户提交spark任务 TODO 还应解决模型输出目录问题
	private static Map<String, String> allAppStatus = new HashMap<>();

	private static Map<String, String> properties = new HashMap<>();// 配置文件加载变量
	/**
	 * 获取配置
	 * @param key
	 * @return
	 */
	public static String getProperty(String key){
		return properties.get(key);
	}
	static{
		// 加载配置文件
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = Utils.class.getResourceAsStream("/hadoop.properties");
			prop.load(input);
			
			// hadoop configuration 
			properties.put("mapreduce.app-submission.cross-platform", 
					prop.getProperty("mapreduce.app-submission.cross-platform"));
			properties.put("fs.defaultFS", prop.getProperty("fs.defaultFS"));
			properties.put("mapreduce.framework.name", prop.getProperty("mapreduce.framework.name"));
			properties.put("yarn.resourcemanager.address",
					prop.getProperty("yarn.resourcemanager.address"));
			properties.put("yarn.resourcemanager.scheduler.address", 
					prop.getProperty("yarn.resourcemanager.scheduler.address"));
			properties.put("mapreduce.jobhistory.address", 
					prop.getProperty("mapreduce.jobhistory.address"));
			
			
			// spark configuration
			properties.put("spark.yarn.jar", prop.getProperty("spark.yarn.jar"));
			properties.put("spark.yarn.scheduler.heartbeat.interval-ms",
					prop.getProperty("spark.yarn.scheduler.heartbeat.interval-ms"));
			
			// spark als configuration 
			properties.put("als.name", prop.getProperty("als.name"));
			properties.put("als.class", prop.getProperty("als.class"));
			properties.put("als.driver-memory", prop.getProperty("als.driver-memory"));
			properties.put("als.num-executors", prop.getProperty("als.num-executors"));
			properties.put("als.executor-memory", prop.getProperty("als.executor-memory"));
			properties.put("als.jar", prop.getProperty("als.jar"));
			properties.put("als.files", prop.getProperty("als.files"));
			
			// data configuration
			properties.put("movies.data", prop.getProperty("movies.data"));
			properties.put("ratings.data", prop.getProperty("ratings.data"));
			properties.put("output.data", prop.getProperty("output.data"));

			// spark als application status configuration
			properties.put("als.submitted.progress", prop.getProperty("als.submitted.progress"));
			properties.put("als.accepted.progress", prop.getProperty("als.accepted.progress"));
			properties.put("als.runing.progress", prop.getProperty("als.runing.progress"));
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	/**
	 * 获取Configuration配置文件
	 * 
	 * @return
	 */
	public static Configuration getConf() {
		if (configuration == null) {

			configuration = new Configuration();
			configuration.setBoolean("mapreduce.app-submission.cross-platform", 
					Boolean.parseBoolean(getProperty("mapreduce.app-submission.cross-platform")));
			configuration.set("fs.defaultFS",getProperty("fs.defaultFS"));
			configuration.set("mapreduce.framework.name", getProperty("mapreduce.framework.name"));
			configuration.set("yarn.resourcemanager.address", getProperty("yarn.resourcemanager.address"));
			configuration.set("yarn.resourcemanager.scheduler.address", 
					getProperty("yarn.resourcemanager.scheduler.address"));
			configuration.set("mapreduce.jobhistory.address", getProperty("mapreduce.jobhistory.address"));
		}

		return configuration;
	}

	/**
	 * 调用Spark 加入监控模块
	 * 
	 * @param args
	 * @return Application ID字符串
	 */
	public static String runSpark(String[] args) {
		StringBuffer buff = new StringBuffer();
		for(String arg:args){
			buff.append(arg).append(",");
		}
		log.info("runSpark args:"+buff.toString());
		try {
			System.setProperty("SPARK_YARN_MODE", "true");
			SparkConf sparkConf = new SparkConf();
			sparkConf.set("spark.yarn.jar", getProperty("spark.yarn.jar"));
			sparkConf.set("spark.yarn.scheduler.heartbeat.interval-ms",
					getProperty("spark.yarn.scheduler.heartbeat.interval-ms"));

			ClientArguments cArgs = new ClientArguments(args, sparkConf);

			Client client = new Client(cArgs, getConf(), sparkConf);
			// client.run(); // 去掉此种调用方式，改为有监控的调用方式

			/**
			 * 调用Spark ，含有监控
			 */
			ApplicationId appId = null;
			try{
				appId = client.submitApplication();
			}catch(Throwable e){
				e.printStackTrace();
				//  返回null
				return null;
			}
			// 开启监控线程
			updateAppStatus(appId.toString(),getProperty("als.submitted.progress") );// 提交任务完成，返回2%
			log.info(allAppStatus.toString());
			new Thread(new MonitorThread(appId,client)).start();
			return appId.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 参考Spark实现删除相关文件代码
	 * 
	 * TODO Tomcat关闭时，如果还有Spark程序还在运行，那么删除不了文件 
	 * 
	 * @param appId
	 */
	public static void cleanupStagingDir(ApplicationId appId) {
		String appStagingDir = Client.SPARK_STAGING() + Path.SEPARATOR + appId.toString();

		try {
			Path stagingDirPath = new Path(appStagingDir);
			FileSystem fs = FileSystem.get(getConf());
			if (fs.exists(stagingDirPath)) {
				log.info("Deleting staging directory " + stagingDirPath);
				fs.delete(stagingDirPath, true);
			}
		} catch (IOException e) {
			log.warn("Failed to cleanup staging dir " + appStagingDir, e);
		}
	}

	/**
	 * 读取文件内容
	 * 
	 * @param outputPath
	 * @return
	 */
	public static String readHDFS(String outputPath) {
		StringBuffer buffer = new StringBuffer();
		try {
			Path path = new Path(outputPath);
			FileSystem fs = FileSystem.get(getConf());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(path)));
			try {
				String line;
				while ((line = br.readLine()) != null) {
					buffer.append(line).append("\n");
					line = br.readLine();
				}
			} finally {
				br.close();
			}
		} catch (Exception e) {
			return null;
		}
		return buffer.toString();
	}

	/**
	 * 预测 如果没有初始化，则进行初始化
	 * 
	 * @param uid
	 * @param recNum
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws InstantiationException 
	 * @throws IllegalAccessException 
	 */
	public static List<Movie> predict(int uid,int recNum) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		if (userFeatures.size() <= 0 || productFeatures.size() <= 0) {
			try {
				userFeatures = getModelFeatures(userFeaturePath);
				productFeatures = getModelFeatures(productFeaturePath);
			} catch (IOException e) {
				return null;
			}
			if (userFeatures.size() <= 0 || productFeatures.size() <= 0) {
				System.err.println("模型加载失败!");
				return null;
			}
		}

		// 使用模型进行预测
		// 1. 找到uid没有评价过的movieIds
		Set<Integer> candidates = Sets.difference((Set<Integer>) allMovieIds, userWithRatedMovies.get(uid));

		// 2. 构造推荐排序堆栈
		FixSizePriorityQueue<Movie> recommend = new FixSizePriorityQueue<Movie>(recNum);
		Movie movie = null;
		double[] pFeature = null;
		double[] uFeature = userFeatures.get(uid);
		double score = 0.0;
		BLAS blas = BLAS.getInstance();
		for (int candidate : candidates) {
			movie = movies.get(candidate).deepCopy();
			pFeature = productFeatures.get(candidate);
			if (pFeature == null)
				continue;
			score = blas.ddot(pFeature.length, uFeature, 1, pFeature, 1);
			movie.setRated((float) score);
			recommend.add(movie);
		}

		return recommend.sortedList();
	}

	/**
	 * 加载model user/product features
	 * 
	 * @param userfeaturepath2
	 * @return
	 */
	private static Map<Integer, double[]> getModelFeatures(String featurePath) throws IOException {
		Map<Integer, double[]> features = new HashMap<>();
		Path path = new Path(featurePath);
		FileSystem fs = FileSystem.get(getConf());
		BufferedReader br = null;
		InputStreamReader inputReader = null;
		FileStatus[] files = fs.listStatus(path);
		for (FileStatus file : files) {
			if (file.isDirectory() || file.getLen() <= 0) {
				continue;
			}
			try {
				inputReader = new InputStreamReader(fs.open(file.getPath()));
				br = new BufferedReader(inputReader);

				String line;
				String[] words = null;
				int id = -1;
				// id:f1,f2,f3,,,fn
				while ((line = br.readLine()) != null) {
					words = line.split(COLON);
					id = Integer.parseInt(words[0]);
					features.put(id, getDoubleFromString(words[1], COMMA));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				inputReader.close();
				br.close();
			}
		}
		return features;
	}

	/**
	 * 从string 转为double数组
	 * 
	 * @param string
	 * @param splitter
	 * @return
	 */
	private static double[] getDoubleFromString(String string, String splitter) {
		String[] strings = string.split(splitter);
		double[] ddArr = new double[strings.length];
		for (int i = strings.length - 1; i >= 0; i--) {
			ddArr[i] = Double.parseDouble(strings[i]);
		}
		return ddArr;
	}

	/**
	 * 初始化 movies、ratings数据
	 * 
	 * @throws IOException
	 */
	public static void init() throws IOException {
		
		// file path initial
		 String output =getProperty("output.data");
		String MOVIESDATA = getProperty("movies.data");
		String RATINGSDATA = getProperty("ratings.data");

		userFeaturePath = output + "/userFeatures";
		productFeaturePath = output + "/productFeatures";
		RMSEPATH = output + "/rmse/" + SUBFIX;
		
		
		// 读取movies数据到：Map<movieId,Movie-descriptions>
		Path path = new Path(MOVIESDATA);
		FileSystem fs = FileSystem.get(getConf());
		BufferedReader br = null;
		InputStreamReader inputReader = null;
		try {
			inputReader = new InputStreamReader(fs.open(path));
			br = new BufferedReader(inputReader);

			String line;
			String[] words = null;
			int id = -1;
			// MovieID::Title::Genres
			while ((line = br.readLine()) != null) {
				words = line.split(DOUBLECOLON);
				id = Integer.parseInt(words[0]);
				movies.put(id, new Movie(id, words[1], words[2]));
			}
			log.info("Movies data size:" + movies.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inputReader.close();
			br.close();
		}

		// 读取ratings数据到Map<userid, ratedMoviesId> (not recommended)
		path = new Path(RATINGSDATA);
		try {
			inputReader = new InputStreamReader(fs.open(path));
			br = new BufferedReader(inputReader);

			String line;
			String[] words = null;
			int uid = -1;
			HashSet<MIdRated> movieIds = null;
			// UserID::MovieID::Rating::Timestamp
			while ((line = br.readLine()) != null) {
				words = line.split(DOUBLECOLON);
				uid = Integer.parseInt(words[0]);
				if (userWithRatedMovies.containsKey(uid)) {
					userWithRatedMovies.get(uid).add(new MIdRated(words[1],words[2]));

				} else {
					movieIds = new HashSet<>();
					movieIds.add(new MIdRated(words[1],words[2]));
					userWithRatedMovies.put(uid, movieIds);
				}

			}
			log.info("Users data size:" + userWithRatedMovies.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inputReader.close();
			br.close();
		}

		allMovieIds = movies.keySet();
	}

	private static Map<Integer, Movie> movies = new HashMap<>();
	private static Map<Integer, Set<MIdRated>> userWithRatedMovies = new HashMap<>();
	private static Set<Integer> allMovieIds = new HashSet<>();
	private static Map<Integer, double[]> userFeatures = new HashMap<>();
	private static Map<Integer, double[]> productFeatures = new HashMap<>();

	public static void main(String[] args) throws IOException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException {
		init();

		int uid = 1;
		int TOPN=10;
		List<Movie> recMovies = predict(uid,TOPN);
		for (Movie m : recMovies) {
			System.out.println(m);
		}
		System.out.println(recMovies.size());
	}

	/**
	 * 获取appId的状态
	 * @param appId
	 * @return
	 */
	public static String getAppStatus(String appId) {
		return allAppStatus.get(appId);
	}

	/**
	 * 更新appId状态
	 * @param appId
	 * @param appStatus
	 */
	public synchronized static void updateAppStatus(String appId, String appStatus) {
		// 不管是否已经存在改appId，直接更新即可
		allAppStatus.put(appId, appStatus);
	}
	/**
	 * 查找给定用户评价过的电影
	 * @param userId
	 * @return
	 */
	public static List<Movie> check(int userId) {
		Set<MIdRated> ratedMovieIds = userWithRatedMovies.get(userId);
		List<Movie> moviesList = new ArrayList<>();
		Movie ratedMovie = null;
		for(MIdRated movie:ratedMovieIds){
			ratedMovie = movies.get(movie.getMovieId()).deepCopy();
			ratedMovie.setRated(movie.getRated());
			moviesList.add(ratedMovie);
		}
		
		return moviesList;
	}
}
