package recommend;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.Movie;
import util.Utils;

/**
 * Servlet implementation class Recommend
 */
@WebServlet("/Recommend")
public class Recommend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Recommend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uid = request.getParameter("userId");
		String flag = request.getParameter("flag");// flag : recommend/check 
		String recNum =null;
		
		List<Movie> rec =null;
		if("recommend".equals(flag)){
			recNum = request.getParameter("recommendNum");
			try {
				rec= Utils.predict(Integer.parseInt(uid),Integer.parseInt(recNum));
			} catch (NumberFormatException | IllegalAccessException | InstantiationException | InvocationTargetException
					| NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			rec = Utils.check(Integer.parseInt(uid));
		}
		StringBuffer buffer = new StringBuffer();
		if("check".equals(flag)){
			buffer.append(rec==null?"0":rec.size()).append("::");
		}
		for(Movie re:rec){
			buffer.append("<tr>")
				.append("<td>").append(re.getId()).append("</td>")
				.append("<td>").append(re.getTitle()).append("</td>")
				.append("<td>").append(re.getGenres()).append("</td>")
				.append("<td>").append(re.getRated()).append("</td>")
				.append("</tr>");
		}
		
		// 打印输出
		PrintWriter out = response.getWriter();
		if(buffer.length()<=0){
			out.write("");
		}else{
			out.write(buffer.toString());
		}
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
