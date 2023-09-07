package Controllers;

import Model.*;
import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.text.Font;

import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;
import java.util.List;


public class PlaceOrderController<OrderDetails> {
    public Button btnAddCus;
    public TextField lblDate;
    public ComboBox cmbAddCusIds;
    public ComboBox cmbItemCodes;
    public TextField txtCusName;
    public TextField txtDescription;
    public TextField txtUnitPrice;
    public TextField txtQtyOnHand;
    public TextField txtQty;
    public Button btnAddToCart;
    public Button btnClear;
    public TableColumn colCode;
    public TableColumn colDescription;
    public TableColumn colQty;
    public TableColumn colUnitPrice;
    public TableColumn colTotal;
    public TableView tblCart;
    public Label lblGrandTotal;
    public Button btnAddItem;
    ObservableList<Cart> shoppingList = FXCollections.observableArrayList();
    public TextField txtOrderId;


    private void loadDate(){
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("YYYY-MM-dd");
        lblDate.setText(f.format(date));
        Font font = Font.font("System", 18);
        lblDate.setFont(font);
    }

    public void initialize() throws SQLException, ClassNotFoundException {
        loadDate();
        loadCustomerIds();
        loadItemCodes();
        setOrderId();
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));


        btnAddCus.setOnAction(event -> {
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/CustomerForm.fxml"))));
                stage.show();
                stage.setResizable(false);
                stage.setTitle("Customer Form");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        cmbAddCusIds.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setCustomerData((String) newValue);
        });
        cmbItemCodes.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setItemData((String) newValue);
        });

        btnAddItem.setOnAction(event -> {
            Stage stage = new Stage();
            try {
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../View/ItemForm.fxml"))));
                stage.show();
                stage.setResizable(false);
                stage.setTitle("Item Form");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void setItemData(String newValue) {
        PlaceOrderController controller = new PlaceOrderController();
        Item item = controller.searchItem(newValue);
        txtDescription.setText(item.getDescription());
        txtUnitPrice.setText(String.valueOf(item.getUnitPrice()));
        txtQtyOnHand.setText(String.valueOf(item.getQtyOnHand()));
    }

    private Item searchItem(String code) {
        String SQL = "Select * From item where code='" + code + "'";
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(SQL);
            if (resultSet.next()){
                Item item = new Item(code,resultSet.getString("description"),resultSet.getDouble("unitPrice"), (int) resultSet.getDouble("qtyOnHand"));
                return item;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private void setCustomerData(String newValue) {
        PlaceOrderController controller = new PlaceOrderController();
        Customer customer = controller.searchCustomer(newValue);
        txtCusName.setText(customer.getName());
    }
    public Customer searchCustomer(String id) {
        String SQL = "Select * From Customer where id='" + id + "'";

        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet resultSet = stm.executeQuery(SQL);
            if (resultSet.next()) {
                Customer customer = new Customer(id, resultSet.getString("name"), resultSet.getString("address"), resultSet.getDouble("salary"));
                return customer;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        return null;
    }

    public List<String> getCustomerId() throws SQLException, ClassNotFoundException {
        List<String> ids = new ArrayList<>();
        ResultSet rst = DBConnection.getInstance().getConnection().prepareStatement("SELECT * FROM  customer").executeQuery();
        while (rst.next()){
            ids.add(
                    rst.getString(1)
            );
        }
        return ids;
    }
    private void loadCustomerIds() throws SQLException, ClassNotFoundException {
        List<String> customerIdList = new PlaceOrderController().getCustomerId();
        cmbAddCusIds.getItems().addAll(customerIdList);
    }

    public List<String> getItemCode() throws SQLException, ClassNotFoundException {
        List<String> ids = new ArrayList<>();
        ResultSet rst = DBConnection.getInstance().getConnection().prepareStatement("SELECT * FROM  item").executeQuery();
        while (rst.next()){
            ids.add(
                    rst.getString(1)
            );
        }
        return ids;
    }
    private void loadItemCodes() throws SQLException, ClassNotFoundException {
        List<String> itemCodes = new PlaceOrderController().getItemCode();
        cmbItemCodes.getItems().addAll(itemCodes);
    }
    ObservableList<Cart> cartList = FXCollections.observableArrayList();
    public void btnAddToCartOnAction(ActionEvent actionEvent) {
        String description = txtDescription.getText();
        int  qty = (int) Double.parseDouble(txtQty.getText());
        double  unitPrice = Double.parseDouble(txtUnitPrice.getText());
        int qtyOnHand = (int) Double.parseDouble(txtQtyOnHand.getText());
        double total = unitPrice*qty;
        if(qtyOnHand<qty){
            new Alert(Alert.AlertType.WARNING,"Invalid Qty, Only "+qtyOnHand+" Qts available ").show();
            return;
        }

        Cart cart = new Cart((String) cmbItemCodes.getValue(),description, qty, unitPrice, total);
        int row = isAlreadyExist(cart);
        if(row == -1){
            shoppingList.add(cart);
            System.out.println(row);
        }else{
            Cart temp = shoppingList.get(row);
            Cart newCart = new Cart(temp.getCode(),temp.getDescription(), (int) temp.getUnitPrice(), temp.getQty() + qty, temp.getTotal() + total);
            shoppingList.remove(row);
            shoppingList.add(row,newCart);
        }
        cartList.add(cart);
        tblCart.setItems(cartList);
        calculateTotal();
        txtDescription.setText("");
        txtUnitPrice.setText("");
        txtQtyOnHand.setText("");
        txtQty.setText("");
    }
    private int isAlreadyExist(Cart shoppingCart) {
        for(int i=0; i<shoppingList.size(); i++){
            if(shoppingCart.getCode().equals(shoppingList.get(i).getCode())){
                return i;
            }
        }
        return -1;
    }
    private void calculateTotal(){
        double ttl = 0;
        for (Cart tm: cartList){
            ttl+=tm.getTotal();
        }
        lblGrandTotal.setText(ttl+"0 /= ");
    }
    public static String getLastOrderId() throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();
        Statement stm = connection.createStatement();
        ResultSet rst = stm.executeQuery("SELECT id FROM Orders ORDER BY id DESC LIMIT 1");
        return rst.next() ? rst.getString("id") : null;
    }
    private void setOrderId() {
        try {
            String lastOrderId = getLastOrderId();
            if (lastOrderId != null) {
                lastOrderId = lastOrderId.split("[A-Z]")[1]; // D001==> 001
                lastOrderId = String.format("D%03d", (Integer.parseInt(lastOrderId) + 1));
                txtOrderId.setText(lastOrderId);
                Font font = Font.font("System", 18);
                txtOrderId.setFont(font);
            } else {
                txtOrderId.setText("D001");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void btnPlaceOrderActionPerformed(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        String orderId = txtOrderId.getText();
        String custId = (String) cmbAddCusIds.getValue();
        String orderDate = lblDate.getText();
        ObservableList<OrderDetail> list = FXCollections.observableArrayList();

        for(int i=0; i<shoppingList.size(); i++){
            String code = shoppingList.get(i).getCode();
            double qty = shoppingList.get(i).getQty();
            double unitPrice = shoppingList.get(i).getUnitPrice();
            OrderDetail orderDetail = new OrderDetail(orderId,code, (int) qty,unitPrice);
            list.add(orderDetail);
        }

        Order order = new Order(orderId, orderDate, custId, list);
        boolean isAdded = placeOrder(order);
        if(isAdded){
            if(isAdded){
                System.out.println("Item Added and QtyOnHand Updated");
            }else{
                System.out.println("Item added fail and qtyonhand not updated");
            }
        }else{
            System.out.println("Item added fail");
        }
    }
    private boolean placeOrder(Order order) throws SQLException, ClassNotFoundException {
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);
            PreparedStatement stm = connection.prepareStatement("Insert into Orders values(?,?,?)");
            stm.setObject(1,order.getId());
            stm.setObject(2,order.getDate());
            stm.setObject(3,order.getCustomerId());
            boolean orderIsAdded = stm.executeUpdate()>0;
            if(orderIsAdded){
                boolean orderDetailAdded = OrderDetailController.addOrderDetail(order.getList());
                if(orderDetailAdded){
                    boolean itemUpdated = ItemController.updateStock(order.getList());
                    if(itemUpdated){
                        connection.commit();
                        System.out.println("Updated Success!!");
                        return true;
                    }
                }
            }
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
