package test;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out.write("['server':'a']");
		out.flush();
	}

	/**
	 *{input:input,trainPercent:trainPercent,ranks:ranks,lambda:lambda,iterations:iterations},
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String input = request.getParameter("input");
		String trainPercent = request.getParameter("trainPercent");
		String ranks = request.getParameter("ranks");
		String lambda = request.getParameter("lambda");
		String iterations = request.getParameter("iterations");
		
		System.out.println(input+","+trainPercent+","+ranks+","+lambda+","+iterations);
		
		doGet(request, response);
	}

}
