package controller.orderDetailController;

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

    OrderDetailManagementController orderDetailManagementController = new OrderDetailManagementController();

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
        ObservableList<OrderDetailsinfo> allOrderDetails = orderDetailManagementController.getAllOrderDetails();
        tblOrderDetailManagement.setItems(allOrderDetails);

    }

    // --------------------Add ---------------
    @FXML
    void btnOrderDetailsAddOnAction(ActionEvent event) {
        String orderId = txtOrderId.getText();
        String itemCode = txtItemCode.getText();
        Integer orderQty = Integer.valueOf(txtOrderQty.getText());
        Integer discount = Integer.valueOf(txtDiscount.getText());

        if (orderId.isEmpty() || itemCode.isEmpty() || txtOrderQty.getText().isEmpty() || txtDiscount.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please fill all fields").show();
            return;
        }

        orderDetailManagementController.addOrderDetails(orderId,itemCode,orderQty,discount);
        loadOrderDetailsFromDB();
        clearFields();

    }

    //------------UPDATE-------------------
    @FXML
    void btnOrderDetailsUpdateOnAction(ActionEvent event) {

        String orderId = txtOrderId.getText();
        String itemCode = txtItemCode.getText();
        Integer orderQty = Integer.valueOf(txtOrderQty.getText());
        Integer discount = Integer.valueOf(txtDiscount.getText());

        if (txtOrderId.getText().isEmpty() || txtItemCode.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select a row to update").show();
            return;
        }

        orderDetailManagementController.updateOrderDetails(orderId,itemCode,orderQty,discount);
        loadOrderDetailsFromDB();
        clearFields();
    }

    //--------------DELETE----------------
    @FXML
    void btnOrderDetailsDeleteOnAction(ActionEvent event) {

        String orderId = txtOrderId.getText();
        String itemCode = txtItemCode.getText();

        if (txtOrderId.getText().isEmpty() || txtItemCode.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Select a row to delete").show();
            return;
        }
        orderDetailManagementController.deleteOrderDetails(orderId,itemCode);
        loadOrderDetailsFromDB();
        clearFields();
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


    public void btnOrderDetailExitOnAction(ActionEvent actionEvent) {
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/main_form.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.show();
    }
}
