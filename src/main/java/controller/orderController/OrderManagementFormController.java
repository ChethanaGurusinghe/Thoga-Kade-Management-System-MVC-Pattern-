package controller.orderController;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import controller.orderController.OrderManagementController;
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

    //---------------------ADD------------------
    @FXML
    void btnOrderAddOnAction(ActionEvent event) {
        String orderId = txtOrderId.getText();
        String custId = txtCustomerId.getText();
        LocalDate orderDate = datePickerOrderDate.getValue();

        if (orderId.isEmpty() || custId.isEmpty() || orderDate == null) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
            return;
        }

        OrderManagementController orderManagementController = new OrderManagementController();
        orderManagementController.addOrderDetails(orderId,custId,orderDate);
        loadOrdersFromDB();
        clearFields();
    }

    //------------------UPDATE------------------
    @FXML
    void btnOrderUpdateOnAction(ActionEvent event) {

        String id = txtOrderId.getText();
        String custId = txtCustomerId.getText();
        LocalDate orderDate = datePickerOrderDate.getValue();

        if (txtOrderId.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select a row to update").show();
            return;
        }

        OrderManagementController orderManagementController = new OrderManagementController();
        orderManagementController.updateOrderDetails(id,custId,orderDate);
        loadOrdersFromDB();
        clearFields();
    }

    //---------------------DELETE----------------------
    @FXML
    void btnOrderDeleteOnAction(ActionEvent event) {

        String id = txtOrderId.getText();

        if (txtOrderId.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select a row to delete").show();
            return;
        }

        OrderManagementController orderManagementController = new OrderManagementController();
        orderManagementController.orderDeleteDetails(id);
        loadOrdersFromDB();
        clearFields();
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