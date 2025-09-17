package controller.itemController;

public interface ItemManagementService {

    void addItemDetails(String itemCode,String description,String packSize,Double unitPrice,Integer qtyOnHand);
    void deleteItemDetails(String code);
    void updateItemDetails(String description,String packSize,Double unitPrice,Integer qtyOnHand,String code);
}
