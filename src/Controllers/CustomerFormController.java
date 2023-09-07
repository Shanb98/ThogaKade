package Controllers;

import Model.Customer;
import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class CustomerFormController {
    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtSalary;
    public Button btnSave;
    public Button btnView;
    public Button btnExit;
    public Button btnSearch;
    public Button btnUpdate;
    public Button btnDelete;

    public void initialize() {
        btnView.setOnAction(event -> {
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/ViewForm.fxml"))));
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public void btnSaveOnAction(ActionEvent actionEvent) {
        String id = txtId.getText();
        String name = txtName.getText();
        String address= txtAddress.getText();
        double salary = Double.parseDouble(txtSalary.getText());
        Customer customer = new Customer(id,name,address,salary);

        String SQL = "Insert into Customer Values(?,?,?,?)";

        try {
            Optional<ButtonType> buttonType= new Alert(Alert.AlertType.CONFIRMATION,"Do you Want to add this Customer", ButtonType.YES, ButtonType.NO).showAndWait();

            if (buttonType.get()==ButtonType.YES){
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement psTm = connection.prepareStatement(SQL);
                psTm.setObject(1,customer.getID());
                psTm.setObject(2,customer.getName());
                psTm.setObject(3,customer.getAddress());
                psTm.setObject(4,customer.getSalary());
                if (psTm.executeUpdate()>0){
                    new Alert(Alert.AlertType.INFORMATION,"Customer Added! ").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong try again").show();
                }

            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void btnExit(ActionEvent actionEvent) {
        if (txtId.getText()==null){
            Stage stage = (Stage) btnExit.getScene().getWindow();
            stage.close();
        }
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String id = txtId.getText();
        String name = txtName.getText();
        String address= txtAddress.getText();
        double salary = Double.parseDouble(txtSalary.getText());
        Customer customer = new Customer(id,name,address,salary);

        String SQL = "UPDATE Customer SET name=?, address=?,salary=? WHERE id=?";

        try {
            Optional<ButtonType> buttonType= new Alert(Alert.AlertType.CONFIRMATION,"Do you Want to Update this Customer", ButtonType.YES, ButtonType.NO).showAndWait();

            if (buttonType.get()==ButtonType.YES){
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement psTm = connection.prepareStatement(SQL);
                psTm.setObject(1,customer.getName());
                psTm.setObject(2,customer.getAddress());
                psTm.setObject(3,customer.getSalary());
                psTm.setObject(4,customer.getID());
                if (psTm.executeUpdate()>0){
                    new Alert(Alert.AlertType.INFORMATION,"Customer Updated! ").show();
                }else{
                    new Alert(Alert.AlertType.ERROR,"Something went wrong try again").show();
                }

            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void btnSearchOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String id = txtId.getText();
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        String SQL = "SELECT  * FROM Customer WHERE id ='"+id+"' ";
        ResultSet rst = stm.executeQuery(SQL);
        Customer customer = rst.next() ? new Customer(id, rst.getString("name"), rst.getString("address"), rst.getDouble("salary")) : null;
        if(customer!= null){
            txtName.setText(customer.getName());
            txtAddress.setText(customer.getAddress());
            txtSalary.setText(String.valueOf(customer.getSalary()));
        }
        else {
            new Alert(Alert.AlertType.ERROR,"Customer not Found").show();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Optional<ButtonType> buttonType= new Alert(Alert.AlertType.CONFIRMATION,"Do you Want to Update this Customer", ButtonType.YES, ButtonType.NO).showAndWait();
        if (buttonType.get()==ButtonType.YES){
            String id = txtId.getText();
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            String SQL = "DELETE FROM Customer WHERE id ='"+id+"' ";
            int rst = stm.executeUpdate(SQL);
            if (rst>0){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted! ").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong try again").show();
            }
            txtId.setText("");
            txtName.setText("");
            txtAddress.setText("");
            txtSalary.setText("");
        }

    }
}
