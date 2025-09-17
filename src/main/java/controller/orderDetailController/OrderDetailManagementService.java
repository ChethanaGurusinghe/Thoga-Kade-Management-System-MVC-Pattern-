package controller.orderDetailController;

public interface OrderDetailManagementService {

    void addOrderDetails(String orderId,String itemCode,Integer orderQty,Integer discount);
    void updateOrderDetails(String itemCode,String orderID,Integer orderQty,Integer discount);
    void deleteOrderDetails(String orderId,String itemCode);
}
