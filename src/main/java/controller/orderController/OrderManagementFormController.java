package controller.orderController;

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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.OrderInfo;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class OrderManagementFormController implements Initializable {

    @FXML
    private JFXButton btnOrderAdd, btnOrderClear, btnOrderDelete, btnOrderUpdate;

    @FXML
    private TableColumn<OrderInfo, String> colOrderId, colCustomerId;

    @FXML
    private TableColumn<OrderInfo, String> colOrderDate;

    @FXML
    private TableView<OrderInfo> tblOrderManagement;

    @FXML
    private JFXTextField txtOrderId, txtCustomerId;

    @FXML
    private DatePicker datePickerOrderDate;

    private final ObservableList<OrderInfo> orderList = FXCollections.observableArrayList();

    Stage stage = new Stage();

    private final String URL = "jdbc:mysql://localhost:3306/Thogakade";
    private final String USER = "root";
    private final String PASSWORD = "1234";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("custId"));

        tblOrderManagement.setItems(orderList);

        loadOrdersFromDB();

        // Row click → fill fields
        tblOrderManagement.setOnMouseClicked(event -> {
            OrderInfo selected = tblOrderManagement.getSelectionModel().getSelectedItem();
            if (selected != null) {
                txtOrderId.setText(selected.getId());
                datePickerOrderDate.setValue(LocalDate.parse(selected.getOrderDate()));
                txtCustomerId.setText(selected.getCustId());
            }
        });
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private void loadOrdersFromDB() {
        orderList.clear();
        try (Connection con = getConnection();
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
    }

    @FXML
    void btnOrderAddOnAction(ActionEvent event) {
        String orderId = txtOrderId.getText();
        String custId = txtCustomerId.getText();
        LocalDate orderDate = datePickerOrderDate.getValue();

        if (orderId.isEmpty() || custId.isEmpty() || orderDate == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
            return;
        }

        try (Connection con = getConnection()) {
            // Validate CustID exists
            if (!existsInTable(con, "Customer", "CustID", custId)) {
                new Alert(Alert.AlertType.ERROR, "Invalid Customer ID").show();
                return;
            }

            String insertSQL = "INSERT INTO Orders (OrderID, OrderDate, CustID) VALUES (?,?,?)";
            try (PreparedStatement ps = con.prepareStatement(insertSQL)) {
                ps.setString(1, orderId);
                ps.setDate(2, Date.valueOf(orderDate));
                ps.setString(3, custId);
                ps.executeUpdate();

                new Alert(Alert.AlertType.INFORMATION, "Order Added Successfully!").show();
                loadOrdersFromDB();
                clearFields();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    @FXML
    void btnOrderUpdateOnAction(ActionEvent event) {
        if (txtOrderId.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select a row to update").show();
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE Orders SET OrderDate=?, CustID=? WHERE OrderID=?")) {

            ps.setDate(1, Date.valueOf(datePickerOrderDate.getValue()));
            ps.setString(2, txtCustomerId.getText());
            ps.setString(3, txtOrderId.getText());

            int updated = ps.executeUpdate();
            if (updated > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Order Updated Successfully!").show();
                loadOrdersFromDB();
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
    void btnOrderDeleteOnAction(ActionEvent event) {
        if (txtOrderId.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select a row to delete").show();
            return;
        }

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM Orders WHERE OrderID=?")) {

            ps.setString(1, txtOrderId.getText());

            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Order Deleted Successfully!").show();
                loadOrdersFromDB();
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
    void btnOrderClearOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        txtOrderId.clear();
        txtCustomerId.clear();
        datePickerOrderDate.setValue(null);
    }

    // Helper: Validate foreign key existence
    private boolean existsInTable(Connection con, String table, String column, String value) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + "=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, value);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    public void btnOrderManagementExitOnAction(ActionEvent actionEvent) {
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/main_form.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.show();
    }
}
