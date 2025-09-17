package controller.orderController;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.ItemInfo;
import model.OrderInfo;

import java.sql.*;
import java.time.LocalDate;

public class OrderManagementController implements OrderManagementService {

    @Override
    public void addOrderDetails(String id, String custId, LocalDate orderDate){

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            if (!existsInTable(connection, "Customer", "CustID", custId)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Customer ID").show();
                return;
            }

            String insertSQL = "INSERT INTO Orders (OrderID, OrderDate, CustID) VALUES (?,?,?)";
            try (PreparedStatement ps = connection.prepareStatement(insertSQL)) {
                ps.setString(1, id);
                ps.setDate(2, Date.valueOf(orderDate));
                ps.setString(3, custId);
                ps.executeUpdate();

                new Alert(Alert.AlertType.INFORMATION, "Order Added Successfully!").show();

            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @Override
    public void deleteOrderDetails(String id) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1234");
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM Orders WHERE OrderID=?")) {

            ps.setString(1,id);

            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Order Deleted Successfully!").show();

            } else {
                new Alert(Alert.AlertType.WARNING, "No matching record found").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    private boolean existsInTable(Connection con, String table, String column, String value) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + "=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }


    @Override
    public void updateOrderDetails(String id,String custId,LocalDate orderDate){

        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE Orders SET OrderDate=?, CustID=? WHERE OrderID=?")) {

            ps.setDate(1, Date.valueOf(orderDate));
            ps.setString(2,custId);
            ps.setString(3, id);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Order Updated Successfully!").show();

            } else {
                new Alert(Alert.AlertType.WARNING, "No matching record found").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public ObservableList<OrderInfo> getAllOrderDetails(){

        ObservableList<OrderInfo> orderList = FXCollections.observableArrayList();

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1234");
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Orders")) {

            while (rs.next()) {
                orderList.add(new OrderInfo(
                        rs.getString("OrderID"),
                        rs.getDate("OrderDate").toLocalDate().toString(),
                        rs.getString("CustID")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

}
