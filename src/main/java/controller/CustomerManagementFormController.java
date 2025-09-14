package controller;
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
            loadCustomerTable();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1234");

            String SQL = "DELETE FROM Customer WHERE CustID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setObject(1, id);

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                new Alert(Alert.AlertType.INFORMATION, "Customer deleted successfully!").show();
                loadCustomerTable();
            } else {
                new Alert(Alert.AlertType.WARNING, "No customer found with this ID").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error while deleting customer").show();
            e.printStackTrace();
        }
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

        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/Thogakade", "root", "1234"
            );

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
                loadCustomerTable(); // refresh table
            } else {
                new Alert(Alert.AlertType.WARNING, "No Customer found with this ID").show();
            }

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error while updating customer :" + e.getMessage()).show();
            e.printStackTrace();
        }
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
        ObservableList<CustomerInfo> customerList = FXCollections.observableArrayList();

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "1234");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Customer");

            while (rs.next()) {
                CustomerInfo customer = new CustomerInfo(
                        rs.getString("CustID"),
                        rs.getString("CustTitle"),
                        rs.getString("CustName"),
                        rs.getDate("DOB").toLocalDate().toString(), // since your colDob is String
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

        tblCustomerManagement.setItems(customerList); // set data to table
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