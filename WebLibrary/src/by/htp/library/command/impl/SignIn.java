package by.htp.library.command.impl;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import by.htp.library.bean.User;
import by.htp.library.command.Command;
import by.htp.library.service.UserService;
import by.htp.library.service.exception.ServiceException;
import by.htp.library.service.factory.ServiceFactory;

public class SignIn implements Command {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Cookie[] reqCookie = request.getCookies();
		if (reqCookie != null) {
			for (Cookie cookie : reqCookie) {
				response.getWriter().println(cookie.getName() + " - " + cookie.getValue() + "<br/>");
			}
		} else {
			response.getWriter().println("No cookies");
		}

		String login;
		HttpSession session = request.getSession(true);
		login = request.getParameter("login");
		session.setAttribute("login", login);

		String password;
		User user = new User();

		login = request.getParameter("login");
		password = request.getParameter("password");

		ServiceFactory factory = ServiceFactory.getInstance();
		UserService userService = factory.getUserService();
		String page = null;

		try {
			user = userService.signIn(login, password);
			if (user != null) {
				if (user.getRole().equals("client")) {
					request.setAttribute("user", user);
					page = "libraryClient.jsp";
				} else if (user.getRole().equals("admin")) {
					request.setAttribute("user", user);
					page = "WEB-INF/jsp/libraryAdmin.jsp";
				}
			} else {
				request.setAttribute("errorSingIn", "User not registered...");
				page = "signIn.jsp";
			}
		} catch (ServiceException e1) {
			e1.printStackTrace();
			request.setAttribute("error", "wrong password or login");
			page = "signIn.jsp";
		}

		RequestDispatcher dispatcher = request.getRequestDispatcher(page);// ?????
		dispatcher.forward(request, response);

	}

}