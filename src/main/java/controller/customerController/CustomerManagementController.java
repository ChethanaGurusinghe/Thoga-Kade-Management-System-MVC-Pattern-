package controller.customerController;

import javafx.scene.control.Alert;

import java.sql.*;

public class CustomerManagementController {
    public void addCustomerDetails(String id,String title,String name,String dob,Double salary,String address,String city,String province,Integer postalCode){

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1234");

            // Check if customer already exists
            String checkSQL = "SELECT COUNT(*) FROM Customer WHERE CustID = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSQL);
            checkStmt.setString(1, id);
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                new Alert(Alert.AlertType.WARNING, "Customer ID already exists!").show();
                return;
            }

            String SQL = "INSERT INTO Customer VALUES (?,?,?,?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setObject(1, id);
            preparedStatement.setObject(2, title);
            preparedStatement.setObject(3, name);
            preparedStatement.setObject(4, dob);
            preparedStatement.setObject(5, salary);
            preparedStatement.setObject(6, address);
            preparedStatement.setObject(7, city);
            preparedStatement.setObject(8, province);
            preparedStatement.setObject(9, postalCode);

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
