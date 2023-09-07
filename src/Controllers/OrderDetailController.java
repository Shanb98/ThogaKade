package Controllers;

import Model.OrderDetail;
import db.DBConnection;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OrderDetailController {
    public static boolean addOrderDetail(ObservableList<OrderDetail> list) throws ClassNotFoundException, SQLException{
        for (OrderDetail orderDetails : list) {
            boolean isAdded=addOrderDetail(orderDetails);
            if(!isAdded){
                return false;
            }
        }
        return true;
    }
    public static boolean addOrderDetail(OrderDetail orderDetails) throws SQLException, ClassNotFoundException, SQLException {
        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement pst = connection.prepareStatement("Insert into OrderDetail values(?,?,?,?)");
        pst.setObject(1,orderDetails.getOrderId());
        pst.setObject(2,orderDetails.getItemCode());
        pst.setObject(3,orderDetails.getQty());
        pst.setObject(4,orderDetails.getUnitPrice());
        return pst.executeUpdate()>0;
    }
}
