package controller.customerController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.CustomerInfo;
import db.DBConnection;

import java.sql.*;

public class CustomerManagementController implements CustomerManagementService {

    @Override
    public void addCustomerDetails(String id,String title,String name,String dob,Double salary,String address,String city,String province,Integer postalCode){

        try {
            Connection connection = DBConnection.getInstance().getConnection();

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

    @Override
    public void deleteCustomerDetails(String id){

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String SQL = "DELETE FROM Customer WHERE CustID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setObject(1, id);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer deleted successfully!").show();

            } else {
                new Alert(Alert.AlertType.WARNING, "No customer found with this ID").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error while deleting customer").show();
            e.printStackTrace();
        }
    }

    @Override
    public void updateCustomerDetails(String title,String name ,String dob,Double salary,String address,String city,String province,Integer postalCode,String id){

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String SQL = "UPDATE Customer SET CustTitle= ?, CustName= ?, DOB= ?, salary= ?, CustAddress= ?, City= ?, Province= ?, PostalCode=? WHERE CustID= ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setObject(1, title);
            preparedStatement.setObject(2, name);
            preparedStatement.setObject(3, dob);
            preparedStatement.setObject(4, salary);
            preparedStatement.setObject(5, address);
            preparedStatement.setObject(6, city);
            preparedStatement.setObject(7, province);
            preparedStatement.setObject(8, postalCode);
            preparedStatement.setObject(9, id);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer Updated Successfully!").show();

            } else {
                new Alert(Alert.AlertType.WARNING, "No Customer found with this ID").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error while updating customer :" + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public ObservableList<CustomerInfo> getAllCustomerDetails(){

        ObservableList<CustomerInfo> customerList = FXCollections.observableArrayList();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");

            while (rs.next()) {
                CustomerInfo customer = new CustomerInfo(
                        rs.getString("CustID"),
                        rs.getString("CustTitle"),
                        rs.getString("CustName"),
                        rs.getDate("DOB").toLocalDate().toString(),
                        rs.getDouble("salary"),
                        rs.getString("CustAddress"),
                        rs.getString("City"),
                        rs.getString("Province"),
                        rs.getInt("PostalCode")
                );
                customerList.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customerList;
    }
}
