package com.restaurant.servlet;

import com.restaurant.dao.MenuItemDAO;
import com.restaurant.dao.OrderDAO;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.User;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String[] itemIds = request.getParameterValues("itemId");
        String[] quantities = request.getParameterValues("quantity");

        if (itemIds == null || quantities == null || itemIds.length != quantities.length) {
            response.sendRedirect("menu.jsp");
            return;
        }

        MenuItemDAO menuItemDAO = new MenuItemDAO();
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        for (int i = 0; i < itemIds.length; i++) {
            int itemId = Integer.parseInt(itemIds[i]);
            int quantity = Integer.parseInt(quantities[i]);

            if (quantity > 0) {
                MenuItem menuItem = menuItemDAO.getAllMenuItems().stream()
                        .filter(item -> item.getId() == itemId)
                        .findFirst()
                        .orElse(null);

                if (menuItem != null) {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setItemId(itemId);
                    orderItem.setQuantity(quantity);
                    orderItem.setPrice(menuItem.getPrice());

                    orderItems.add(orderItem);
                    totalAmount += menuItem.getPrice() * quantity;
                }
            }
        }

        if (!orderItems.isEmpty()) {
            Order order = new Order();
            order.setCustomerName(user.getUsername());
            order.setTotalAmount(totalAmount);

            OrderDAO orderDAO = new OrderDAO();
            int orderId = orderDAO.createOrder(order, orderItems);

            if (orderId > 0) {
                response.sendRedirect("order-confirmation.jsp?orderId=" + orderId);
            } else {
                response.sendRedirect("menu.jsp");
            }
        } else {
            response.sendRedirect("menu.jsp");
        }
    }
}