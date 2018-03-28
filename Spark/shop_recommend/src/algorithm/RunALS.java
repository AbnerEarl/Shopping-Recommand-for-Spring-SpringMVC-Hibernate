package algorithm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.Utils;

/**
 * Servlet implementation class RunALS
 */
@WebServlet("/RunALS")
public class RunALS extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RunALS() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		<input> <output> <train_percent> <ranks> <lambda> <iteration>
		String input = request.getParameter("input");
		String output = Utils.getProperty("output.data");
		String train_percent = request.getParameter("trainPercent");
		String ranks = request.getParameter("ranks");
		String lambda = request .getParameter("lambda");
		String iteration = request.getParameter("iterations");
		
		String appId ="null";
		try{
			// 启动任务, 启动成功后返回任务appId
			appId = RunSpark.runALS(input, output, train_percent, ranks, lambda, iteration);
		}catch(Exception e){
			appId = "null";
			e.printStackTrace();
		}
		
		
		// 打印输出
		PrintWriter out = response.getWriter();
		out.write(appId==null? "null":appId);
		out.flush();
		
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
