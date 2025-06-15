package com.restaurant.servlet;

import com.restaurant.dao.UserDAO;
import com.restaurant.model.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            // Check if user role matches the selected role
            if (user.getRole().equals(role)) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);

                // Set session timeout to 30 minutes
                session.setMaxInactiveInterval(30 * 60);

                // Redirect based on role
                if ("admin".equals(role)) {
                    response.sendRedirect("admin");
                } else {
                    response.sendRedirect("menu");
                }
            } else {
                // Role mismatch
                request.setAttribute("error",
                        "You don't have permission to access as " + role + ". Please login with correct role.");
                request.getRequestDispatcher("login.jsp?role=" + role).forward(request, response);
            }
        } else {
            // Invalid credentials
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("login.jsp?role=" + role).forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role = request.getParameter("role");
        if (role == null) role = "customer";

        // Check if user is already logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");

            // Redirect to appropriate page if already logged in
            if ("admin".equals(user.getRole())) {
                response.sendRedirect("admin");
            } else {
                response.sendRedirect("menu");
            }
            return;
        }

        // Forward to login page with role parameter
        request.setAttribute("role", role);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}