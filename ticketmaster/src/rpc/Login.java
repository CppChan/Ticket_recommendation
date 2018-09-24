package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
      		 throws ServletException, IOException {
                                DBConnection conn = DBConnectionFactory.getDBConnection();
   		try {
   			JSONObject obj = new JSONObject();
   			HttpSession session = request.getSession(false);
   			if (session == null) {//means has not login
   				response.setStatus(403);
   		//403 Forbidden 是HTTP协议中的一个状态码(Status Code)。可以简单的理解为没有权限访问此站
   				
   				obj.put("status", "Session Invalid");
   			} else {//means has login
   				String userId = (String) session.getAttribute("user_id");
   				String name = conn.getFullname(userId);
   				obj.put("status", "OK");
   				obj.put("user_id", userId);
   				obj.put("name", name);
   			}
   			RpcHelper.writeJsonObject(response, obj);
   		} catch (JSONException e) {
   			e.printStackTrace();
   		}
    }

//    Session 存在服务器的一种用来存放用户数据的类HashTable结构。
//    浏览器第一次发送请求时，服务器自动生成了一HashTable和一Session ID来唯一标识这个HashTable，
//    并将其通过响应发送到浏览器。浏览器第二次发送请求会将前一次服务器响应中的Session ID放在请求中一并发送到服务器上，
//    服务器从请求中提取出Session ID，并和保存的所有Session ID进行对比，找到这个用户对应的HashTable。 
//    一般这个值会有个时间限制，超时后毁掉这个值，默认30分钟。
//    当用户在应用程序的 Web页间跳转时，存储在 Session 对象中的变量不会丢失而是在整个用户会话中一直存在下去。
//    Session的实现方式和Cookie有一定关系。建立一个连接就生成一个session id，打开几个页面就好几个了，这里就用到了Cookie，
//    把session id存在Cookie中，每次访问的时候将Session id带过去就可以识别了.

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
      		 throws ServletException, IOException {
                                DBConnection conn = DBConnectionFactory.getDBConnection();
   		try {
   			JSONObject input = RpcHelper.readJsonObject(request);
   			String userId = input.getString("user_id");
   			String pwd = input.getString("password");

   			JSONObject obj = new JSONObject();

   			if (conn.verifyLogin(userId, pwd)) {
   				//set a session, used to mark that a user has login 
   				HttpSession session = request.getSession();
   				session.setAttribute("user_id", userId);
   				// setting session to expire in 10 minutes
   				session.setMaxInactiveInterval(10 * 60);
   				// Get user name
   				String name = conn.getFullname(userId);
   				obj.put("status", "OK");
   				obj.put("user_id", userId);
   				obj.put("name", name);
   			} else {
   				response.setStatus(401);
//   		401: 您的Web服务器认为，客户端发送的 HTTP 数据流是正确的，但进入网址 (URL) 资源 ， 
//   		需要用户身份验证 ， 而相关信息 1 ）尚未被提供, 或 2 ）已提供但没有通过授权测试。

   			}
   			RpcHelper.writeJsonObject(response, obj);
   		} catch (JSONException e) {
   			e.printStackTrace();
   		}
    }
}
