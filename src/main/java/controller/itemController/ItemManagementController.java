package controller.itemController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.ItemInfo;

import java.sql.*;

public class ItemManagementController {

    public void addItemDetails(String itemCode,String description,String packSize,Double unitPrice,Integer qtyOnHand){

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Thogakade", "root", "1234")) {

            String SQL = "INSERT INTO Item (ItemCode, Description, PackSize, UnitPrice, QtyOnHand) VALUES (?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setObject(1, itemCode);
            preparedStatement.setObject(2, description);
            preparedStatement.setObject(3, packSize);
            preparedStatement.setObject(4, unitPrice);
            preparedStatement.setObject(5, qtyOnHand);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Item Added Successfully!").show();

            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void deleteItemDetails(String code){

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Thogakade", "root", "1234")) {

            String SQL = "DELETE FROM Item WHERE ItemCode=?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setObject(1, code);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Item Deleted Successfully!").show();

            } else {
                new Alert(Alert.AlertType.WARNING, "No Item found with this Code").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void updateItemDetails(String description,String packSize,Double unitPrice,Integer qtyOnHand,String code){

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Thogakade", "root", "1234")) {

            String SQL = "UPDATE Item SET Description=?, PackSize=?, UnitPrice=?, QtyOnHand=? WHERE ItemCode=?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);

            preparedStatement.setObject(1, description);
            preparedStatement.setObject(2, packSize);
            preparedStatement.setObject(3, unitPrice);
            preparedStatement.setObject(4, qtyOnHand);
            preparedStatement.setObject(5, code);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Item Updated Successfully!").show();

            } else {
                new Alert(Alert.AlertType.WARNING, "No Item found with this Code").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }

    }

    public ObservableList<ItemInfo> getAllitemDetails(){

        ObservableList<ItemInfo> itemInfos = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Thogakade", "root", "1234")) {

            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM Item");

            while (rs.next()) {
                itemInfos.add(new ItemInfo(
                        rs.getString("ItemCode"),
                        rs.getString("Description"),
                        rs.getString("PackSize"),
                        rs.getDouble("UnitPrice"),
                        rs.getInt("QtyOnHand")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemInfos;
    }
}
