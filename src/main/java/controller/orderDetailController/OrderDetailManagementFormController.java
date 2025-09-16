package controller.orderDetailController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.OrderDetailsinfo;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class OrderDetailManagementFormController implements Initializable {

    @FXML
    private JFXButton btnOrderDetailsAdd;
    @FXML
    private JFXButton btnOrderDetailsClear;
    @FXML
    private JFXButton btnOrderDetailsDelete;
    @FXML
    private JFXButton btnOrderDetailsUpdate;

    @FXML
    private TableColumn<OrderDetailsinfo, String> colOrderId;
    @FXML
    private TableColumn<OrderDetailsinfo, String> colItemCode;
    @FXML
    private TableColumn<OrderDetailsinfo, Integer> colOrderQty;
    @FXML
    private TableColumn<OrderDetailsinfo, Integer> colDiscount;

    @FXML
    private TableView<OrderDetailsinfo> tblOrderDetailManagement;

    @FXML
    private JFXTextField txtOrderId;
    @FXML
    private JFXTextField txtItemCode;
    @FXML
    private JFXTextField txtOrderQty;
    @FXML
    private JFXTextField txtDiscount;

    private final ObservableList<OrderDetailsinfo> orderDetailsList = FXCollections.observableArrayList();

    Stage stage = new Stage();

    private final String URL = "jdbc:mysql://localhost:3306/Thogakade";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colOrderQty.setCellValueFactory(new PropertyValueFactory<>("orderQty"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));

        tblOrderDetailManagement.setItems(orderDetailsList);

        loadOrderDetailsFromDB();

        // Row click → fill fields
        tblOrderDetailManagement.setOnMouseClicked(event -> {
            OrderDetailsinfo selected = tblOrderDetailManagement.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtOrderId.setText(selected.getId());
                txtItemCode.setText(selected.getCode());
                txtOrderQty.setText(String.valueOf(selected.getOrderQty()));
                txtDiscount.setText(String.valueOf(selected.getDiscount()));
            }
        });
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void loadOrderDetailsFromDB() {
        orderDetailsList.clear();
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM OrderDetail")) {

            while (rs.next()) {
                orderDetailsList.add(new OrderDetailsinfo(
                        rs.getString("OrderID"),
                        rs.getString("ItemCode"),
                        rs.getInt("OrderQTY"),
                        rs.getInt("Discount")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add order detail with foreign key validation
    @FXML
    void btnOrderDetailsAddOnAction(ActionEvent event) {
        String orderId = txtOrderId.getText();
        String itemCode = txtItemCode.getText();

        if (orderId.isEmpty() || itemCode.isEmpty() || txtOrderQty.getText().isEmpty() || txtDiscount.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
            return;
        }

        try (Connection con = getConnection()) {

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
                ps.setInt(3, Integer.parseInt(txtOrderQty.getText()));
                ps.setInt(4, Integer.parseInt(txtDiscount.getText()));
                ps.executeUpdate();
                new Alert(Alert.AlertType.INFORMATION, "Order Detail Added Successfully!").show();
                loadOrderDetailsFromDB();
                clearFields();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    void btnOrderDetailsUpdateOnAction(ActionEvent event) {
        if (txtOrderId.getText().isEmpty() || txtItemCode.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select a row to update").show();
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE OrderDetail SET OrderQTY=?, Discount=? WHERE OrderID=? AND ItemCode=?")) {

            ps.setInt(1, Integer.parseInt(txtOrderQty.getText()));
            ps.setInt(2, Integer.parseInt(txtDiscount.getText()));
            ps.setString(3, txtOrderId.getText());
            ps.setString(4, txtItemCode.getText());

            int updated = ps.executeUpdate();
            if (updated > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Order Detail Updated Successfully!").show();
                loadOrderDetailsFromDB();
                clearFields();
            } else {
                new Alert(Alert.AlertType.WARNING, "No matching record found").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    void btnOrderDetailsDeleteOnAction(ActionEvent event) {
        if (txtOrderId.getText().isEmpty() || txtItemCode.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select a row to delete").show();
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM OrderDetail WHERE OrderID=? AND ItemCode=?")) {

            ps.setString(1, txtOrderId.getText());
            ps.setString(2, txtItemCode.getText());

            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Order Detail Deleted Successfully!").show();
                loadOrderDetailsFromDB();
                clearFields();
            } else {
                new Alert(Alert.AlertType.WARNING, "No matching record found").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    void btnOrderDetailsClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtOrderId.clear();
        txtItemCode.clear();
        txtOrderQty.clear();
        txtDiscount.clear();
    }

    // Helper: Check if a value exists in another table (for foreign key validation)
    private boolean existsInTable(Connection con, String table, String column, String value) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + "=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public void btnOrderDetailExitOnAction(ActionEvent actionEvent) {
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/main_form.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.show();
    }
}
