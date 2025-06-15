package com.restaurant.servlet;

import com.restaurant.dao.MenuItemDAO;
import com.restaurant.model.MenuItem;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MenuItemDAO menuItemDAO = new MenuItemDAO();
        request.setAttribute("menuItems", menuItemDAO.getAllMenuItems());
        request.getRequestDispatcher("admin-menu.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        MenuItemDAO menuItemDAO = new MenuItemDAO();

        if ("add".equals(action)) {
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            String category = request.getParameter("category");

            MenuItem item = new MenuItem();
            item.setName(name);
            item.setDescription(description);
            item.setPrice(price);
            item.setCategory(category);

            menuItemDAO.addMenuItem(item);
        } else if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            menuItemDAO.deleteMenuItem(id);
        }

        response.sendRedirect("admin");
    }
}