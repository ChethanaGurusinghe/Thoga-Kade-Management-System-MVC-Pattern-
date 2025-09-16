package controller.customerController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.CustomerInfo;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;



public class CustomerManagementFormController implements Initializable {


    public Button btnOrderManagemenExit;
    @FXML
    private JFXButton btnCustomerAdd;
    @FXML
    private JFXButton btnCustomerClear;
    @FXML
    private JFXButton btnCustomerDelete;
    @FXML
    private JFXButton btnCustomerUpdate;
    @FXML
    private TableColumn<CustomerInfo, String> colAddress;
    @FXML
    private TableColumn<CustomerInfo, String> colCity;
    @FXML
    private TableColumn<CustomerInfo, String> colDob;
    @FXML
    private TableColumn<CustomerInfo, String> colId;
    @FXML
    private TableColumn<CustomerInfo, String> colName;
    @FXML
    private TableColumn<CustomerInfo, Integer> colPostalCode;
    @FXML
    private TableColumn<CustomerInfo, String> colProvince;
    @FXML
    private TableColumn<CustomerInfo, Double> colSalary;
    @FXML
    private TableColumn<CustomerInfo, String> colTitle;
    @FXML
    private JFXComboBox<String> comboTitle;
    @FXML
    private DatePicker datePickerDob;
    @FXML
    private TableView<CustomerInfo> tblCustomerManagement;
    @FXML
    private JFXTextField txtAddress;
    @FXML
    private JFXTextField txtCity;
    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtName;
    @FXML
    private JFXTextField txtPostalCode;
    @FXML
    private JFXTextField txtProvince;
    @FXML
    private JFXTextField txtSalary;

    ObservableList<CustomerInfo> customerInfos = FXCollections.observableArrayList(

    );


    @FXML
    void btnCustomerAddOnAction(ActionEvent event) {

        String id = txtId.getText();
        String title = String.valueOf(comboTitle.getValue());
        String name = txtName.getText();
        String dob = String.valueOf(datePickerDob.getValue());
        double salary = Double.parseDouble(txtSalary.getText());
        String address = txtAddress.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        int postalCode = Integer.parseInt(txtPostalCode.getText());

        CustomerManagementController customerManagementController = new CustomerManagementController();
        customerManagementController.addCustomerDetails(id,title,name,dob,salary,address,city,province,postalCode);
        loadCustomerTable();

    }


    @FXML
    void btnCustomerClearOnAction(ActionEvent event) {
        txtId.clear();
        comboTitle.getSelectionModel().clearSelection();
        txtName.clear();
        datePickerDob.setValue(null);
        txtSalary.clear();
        txtAddress.clear();
        txtCity.clear();
        txtProvince.clear();
        txtPostalCode.clear();
    }

    @FXML
    void btnCustomerDeleteOnAction(ActionEvent event) {
        String id = txtId.getText();

        if (id.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a customer ID to delete ").show();
            return;
        }

        CustomerManagementController customerManagementController = new CustomerManagementController();
        customerManagementController.deleteCustomerDetails(id);
        loadCustomerTable();
    }

    @FXML
    void btnCustomerUpdateOnAction(ActionEvent event) {

        String id = txtId.getText();
        String title = String.valueOf(comboTitle.getValue());
        String name = txtName.getText();
        String dob = String.valueOf(datePickerDob.getValue());
        double salary = Double.parseDouble(txtSalary.getText());
        String address = txtAddress.getText();
        String city = txtCity.getText();
        String province = txtProvince.getText();
        int postalCode = Integer.parseInt(txtPostalCode.getText());

        if (id.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a Customer ID to update.").show();
            return;
        }

        CustomerManagementController customerManagementController = new CustomerManagementController();
        customerManagementController.updateCustomerDetails(title,name,dob,salary,address,city,province,postalCode,id);
        loadCustomerTable();
    }


    Stage stage = new Stage();

    public void btnOrderManagementExitOnAction(ActionEvent actionEvent) {
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/main_form.fxml"))));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.show();
    }

    private void loadCustomerTable() {

        CustomerManagementController customerManagementController = new CustomerManagementController();
        ObservableList<CustomerInfo> allCustomerDetails = customerManagementController.getAllCustomerDetails();

        tblCustomerManagement.setItems(allCustomerDetails);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> titles = FXCollections.observableArrayList("Mr", "Ms", "Miss");
        comboTitle.setItems(titles);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

        loadCustomerTable();

        tblCustomerManagement.setRowFactory(tv -> {
            TableRow<CustomerInfo> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    CustomerInfo selectedCustomer = row.getItem();
                    fillFields(selectedCustomer);
                }
            });
            return row;
        });

    }

    private void fillFields(CustomerInfo customer) {
        txtId.setText(customer.getId());
        comboTitle.setValue(customer.getTitle());
        txtName.setText(customer.getName());
        datePickerDob.setValue(customer.getDob() != null ? LocalDate.parse(customer.getDob()) : null);
        txtSalary.setText(String.valueOf(customer.getSalary()));
        txtAddress.setText(customer.getAddress());
        txtCity.setText(customer.getCity());
        txtProvince.setText(customer.getProvince());
        txtPostalCode.setText(String.valueOf(customer.getPostalCode()));
    }

}