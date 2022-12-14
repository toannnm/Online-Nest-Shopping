/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nestf.controller.AD.users;

import com.nestf.account.AccountDTO;
import com.nestf.dao.ADMIN.CustomerDAOAdmin;
import com.nestf.dao.ADMIN.ProductDAOAdmin;
import com.nestf.dao.ADMIN.SellerDAOAdmin;
import com.nestf.product.ProductDTO;
import com.nestf.util.MyAppConstant;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "ManagePSellerServlet", urlPatterns = {"/ManagePSellerServlet"})
public class ManagePSellerServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        ServletContext context = request.getServletContext();
        Properties siteMap = (Properties) context.getAttribute("SITEMAP");
        String url = (String) siteMap.get(MyAppConstant.AdminFeatures.MANAGE_SELLER_PAGE);
        String phone = request.getParameter("phone");
//        String choosetime = request.getParameter("choosetime"); // yyyy-mm
//        int year = 0, month = 0;
        String btAction = request.getParameter("type"); // show, block, unblock

        boolean result = false;
        if (btAction.equals("block")) {
            int numProduct = !request.getParameter("product").isEmpty() ? Integer.parseInt(request.getParameter("product")) : 0;
            if (numProduct > 0) {
                result = true;
                request.setAttribute("SELLER_ERROR", "Seller ??ang qu???n l?? s???n ph???m! Kh??ng th??? block");
            }
        }
        try {

//            TH1: input time to check revenue
//            if (!choosetime.isEmpty() && choosetime != null) {
//                String[] array = choosetime.split("-");
//                year = Integer.parseInt(array[0]);
//                month = Integer.parseInt(array[1]);
//
//                if (year != 0 && month != 0) {
//                    List<AccountDTO> manageSeller = SellerDAOAdmin.getListSellerIncome(month, year);
//                    request.setAttribute("FILTER_TIME", manageSeller);
//                }
//            }
            if (btAction.equals("show")) {
                List<ProductDTO> listProduct = ProductDAOAdmin.getListProductOfSeller(phone);
                request.setAttribute("PRODUCT_SELLER", listProduct);
                request.setAttribute("SELLER_NAME", listProduct.get(0).getSelName());
            }

            if (!phone.isEmpty() && btAction.equals("block") && !result) {
                result = CustomerDAOAdmin.blockCustomer(phone);
            }
            if (!phone.isEmpty() && btAction.equals("unblock")) {
                result = CustomerDAOAdmin.unblockCustomer(phone);
            }

            if (result) {
                HttpSession session = request.getSession();
                String choosetime = (String) session.getAttribute("MONTH");
                if (!choosetime.isEmpty()) {
                    String[] array = choosetime.split("-");
                    int year = Integer.parseInt(array[0]);
                    int month = Integer.parseInt(array[1]);
                    List<AccountDTO> manageSeller
                            = SellerDAOAdmin.getListSellerIncome(year, month);
                    session.setAttribute("MANAGE_SELLER", manageSeller);
                    List<AccountDTO> listSeller = SellerDAOAdmin.getListSeller();
                    session.setAttribute("LIST_SELLER", listSeller);
                }

            }

        } catch (SQLException e) {
            log("Error at BlockCustomerServlet_SQL: " + e.getMessage());
        } catch (NamingException e) {
            log("Error at BlockCustomerServlet_Naming: " + e.getMessage());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
