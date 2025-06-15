package com.restaurant.servlet;

import com.restaurant.dao.MenuItemDAO;
import com.restaurant.model.MenuItem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/menu")
public class MenuServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MenuItemDAO menuItemDAO = new MenuItemDAO();
        List<MenuItem> menuItems = menuItemDAO.getAllMenuItems();

        request.setAttribute("menuItems", menuItems);
        request.getRequestDispatcher("menu.jsp").forward(request, response);
    }
}