package Controllers;

import Model.Customer;
import Model.Item;
import Model.OrderDetail;
import db.DBConnection;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class ItemController {
    public TextField txtCode;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public Button btnSave;
    public Button btnView;
    public Button btnUpdate;
    public Button btnSearch;
    public Button btnDelete;
    public Button btnExit;

    public void btnSaveOnAction(ActionEvent actionEvent) {
        String code = txtCode.getText();
        String description = txtDescription.getText();
        double unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        Item item = new Item(code,description,unitPrice,qtyOnHand);

        String SQL = "Insert into Item Values(?,?,?,?)";

        try {
            Optional<ButtonType> buttonType= new Alert(Alert.AlertType.CONFIRMATION,"Do you Want to add this Item", ButtonType.YES, ButtonType.NO).showAndWait();

            if (buttonType.get()==ButtonType.YES){
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement psTm = connection.prepareStatement(SQL);
                psTm.setObject(1,item.getCode());
                psTm.setObject(2,item.getDescription());
                psTm.setObject(3,item.getUnitPrice());
                psTm.setObject(4,item.getQtyOnHand());
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
        if (txtCode.getText()==null){
            Stage stage = (Stage) btnExit.getScene().getWindow();
            stage.close();
        }
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String code = txtCode.getText();
        String description = txtDescription.getText();
        double unitPrice= Double.parseDouble(txtUnitPrice.getText());
        int qtyOnHand = Integer.parseInt(txtQtyOnHand.getText());
        Item item = new Item(code,description,unitPrice,qtyOnHand);

        String SQL = "UPDATE Item SET description=?, unitPrice=?,qtyOnHand=? WHERE code=?";

        try {
            Optional<ButtonType> buttonType= new Alert(Alert.AlertType.CONFIRMATION,"Do you Want to Update this Customer", ButtonType.YES, ButtonType.NO).showAndWait();

            if (buttonType.get()==ButtonType.YES){
                Connection connection = DBConnection.getInstance().getConnection();
                PreparedStatement psTm = connection.prepareStatement(SQL);
                psTm.setObject(1,item.getCode());
                psTm.setObject(2,item.getDescription());
                psTm.setObject(3,item.getUnitPrice());
                psTm.setObject(4,item.getQtyOnHand());
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
        String code = txtCode.getText();
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        String SQL = "SELECT  * FROM Item WHERE code ='"+code+"' ";
        ResultSet rst = stm.executeQuery(SQL);
        Item item = rst.next() ? new Item(code, rst.getString("description"), rst.getDouble("unitPrice"), rst.getInt("qtyOnHand")) : null;
        if(item!= null){
            txtDescription.setText(item.getDescription());
            txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
            txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
        }
        else {
            new Alert(Alert.AlertType.ERROR,"Customer not Found").show();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        Optional<ButtonType> buttonType= new Alert(Alert.AlertType.CONFIRMATION,"Do you Want to Update this Customer", ButtonType.YES, ButtonType.NO).showAndWait();
        if (buttonType.get()==ButtonType.YES){
            String code = txtCode.getText();
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            String SQL = "DELETE FROM Item WHERE code ='"+code+"' ";
            int rst = stm.executeUpdate(SQL);
            if (rst>0){
                new Alert(Alert.AlertType.INFORMATION,"Customer Deleted! ").show();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong try again").show();
            }
            txtCode.setText("");
            txtDescription.setText("");
            txtUnitPrice.setText("");
            txtQtyOnHand.setText("");
        }

    }
    public static boolean updateStock(ObservableList<OrderDetail> list) throws SQLException, ClassNotFoundException {
        for(OrderDetail orderDetails : list){
            if(!updateStock(orderDetails)){
                return false;
            }
        }
        return true;
    }

    public static boolean updateStock(OrderDetail orderDetails) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pst = connection.prepareStatement("Update Item set qtyOnHand=qtyOnHand-? where code=?");
        pst.setObject(1,orderDetails.getQty());
        pst.setObject(2,orderDetails.getItemCode());
        return pst.executeUpdate()>0;
    }
}
