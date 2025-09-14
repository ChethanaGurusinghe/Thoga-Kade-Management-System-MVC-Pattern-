package controller;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.ItemInfo;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ItemManagementFormController implements Initializable {

    @FXML
    private JFXButton btnItemAdd;

    @FXML
    private JFXButton btnItemClear;

    @FXML
    private JFXButton btnItemDelete;

    @FXML
    private JFXButton btnItemUpdate;

    @FXML
    private TableColumn<ItemInfo, String> colDescription;

    @FXML
    private TableColumn<ItemInfo, String> colItemCode;

    @FXML
    private TableColumn<ItemInfo, String> colPakcSize;

    @FXML
    private TableColumn<ItemInfo, Integer> colQtyOnHand;

    @FXML
    private TableColumn<ItemInfo, Double> colUnitPrice;

    @FXML
    private TableView<ItemInfo> tblItemManagement;

    @FXML
    private TextArea txtDescription;

    @FXML
    private JFXTextField txtItemCode;

    @FXML
    private JFXTextField txtPackSize;

    @FXML
    private JFXTextField txtQttyOnHand;

    @FXML
    private JFXTextField txtUnitPrice;

    private final ObservableList<ItemInfo> itemInfos = FXCollections.observableArrayList();

    // ---------------- ADD ITEM ----------------
    @FXML
    void btnItemAddOnAction(ActionEvent event) {
        String itemCode = txtItemCode.getText();
        String description = txtDescription.getText();
        String packSize = txtPackSize.getText();
        Double unitPrice = Double.valueOf(txtUnitPrice.getText());
        Integer qtyOnHand = Integer.valueOf(txtQttyOnHand.getText());

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
                loadItemTable(); // refresh table
                btnItemClearOnAction(null); // clear fields
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    // ---------------- CLEAR ----------------
    @FXML
    void btnItemClearOnAction(ActionEvent event) {
        txtItemCode.clear();
        txtDescription.clear();
        txtPackSize.clear();
        txtUnitPrice.clear();
        txtQttyOnHand.clear();
    }

    // ---------------- DELETE ----------------
    @FXML
    void btnItemDeleteOnAction(ActionEvent event) {
        String code = txtItemCode.getText();
        if (code.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter an Item Code to delete").show();
            return;
        }

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/Thogakade", "root", "1234")) {

            String SQL = "DELETE FROM Item WHERE ItemCode=?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setObject(1, code);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Item Deleted Successfully!").show();
                loadItemTable();
                btnItemClearOnAction(null);
            } else {
                new Alert(Alert.AlertType.WARNING, "No Item found with this Code").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    // ---------------- UPDATE ----------------
    @FXML
    void btnItemUpdateOnAction(ActionEvent event) {
        String code = txtItemCode.getText();
        String description = txtDescription.getText();
        String packSize = txtPackSize.getText();
        Double unitPrice = Double.valueOf(txtUnitPrice.getText());
        Integer qtyOnHand = Integer.valueOf(txtQttyOnHand.getText());

        if (code.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter an Item Code to update").show();
            return;
        }

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
                loadItemTable();
            } else {
                new Alert(Alert.AlertType.WARNING, "No Item found with this Code").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            e.printStackTrace();
        }
    }

    // ---------------- EXIT ----------------
    Stage stage = new Stage();

    public void btnItemManagementExitOnAction(ActionEvent actionEvent) {
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/main_form.fxml"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.show();
    }

    // ---------------- LOAD TABLE ----------------
    private void loadItemTable() {
        itemInfos.clear();

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

            tblItemManagement.setItems(itemInfos);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillFields(ItemInfo item) {
        txtItemCode.setText(item.getItemCode());
        txtDescription.setText(item.getDescription());
        txtPackSize.setText(item.getPackSize());
        txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        txtQttyOnHand.setText(String.valueOf(item.getQtyOnHand()));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPakcSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));

        loadItemTable();

        tblItemManagement.setRowFactory(tv -> {
            javafx.scene.control.TableRow<ItemInfo> row = new javafx.scene.control.TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    ItemInfo selectedItem = row.getItem();
                    fillFields(selectedItem);
                }
            });
            return row;
        });

    }
}
