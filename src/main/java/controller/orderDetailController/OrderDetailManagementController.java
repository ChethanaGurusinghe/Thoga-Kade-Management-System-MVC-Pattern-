package controller.orderDetailController;

import javafx.scene.control.Alert;

import java.sql.*;

public class OrderDetailManagementController {

    public void addOrderDetails(String orderId,String itemCode,Integer orderQty,Integer discount){

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1234")) {

            // Validate OrderID exists
            if (!existsInTable(con, "Orders", "OrderID", orderId)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Order ID").show();
                return;
            }

            // Validate ItemCode exists
            if (!existsInTable(con, "Item", "ItemCode", itemCode)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Item Code").show();
                return;
            }

            String insertSQL = "INSERT INTO OrderDetail (OrderID, ItemCode, OrderQTY, Discount) VALUES (?,?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(insertSQL)) {
                ps.setString(1, orderId);
                ps.setString(2, itemCode);
                ps.setInt(3,orderQty);
                ps.setInt(4,discount);
                ps.executeUpdate();
                new Alert(Alert.AlertType.INFORMATION, "Order Detail Added Successfully!").show();

            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void updateOrderDetails(String itemCode,String orderID,Integer orderQty,Integer discount){

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1234");
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE OrderDetail SET OrderQTY=?, Discount=? WHERE OrderID=? AND ItemCode=?")) {

            ps.setInt(1,orderQty);
            ps.setInt(2,discount);
            ps.setString(3, itemCode);
            ps.setString(4,orderID);

            int updated = ps.executeUpdate();
            if (updated > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Order Detail Updated Successfully!").show();

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

}
