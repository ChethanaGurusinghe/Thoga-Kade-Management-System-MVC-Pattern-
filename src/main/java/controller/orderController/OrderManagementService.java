package controller.orderController;

import java.time.LocalDate;

public interface OrderManagementService {
    void addOrderDetails(String id, String custId, LocalDate orderDate);
    void deleteOrderDetails(String id);
    void updateOrderDetails(String id,String custId,LocalDate orderDate);
}
