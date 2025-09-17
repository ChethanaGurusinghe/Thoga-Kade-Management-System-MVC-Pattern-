package controller.customerController;

import model.CustomerInfo;

public interface CustomerManagementService {
    void addCustomerDetails(String id,String title,String name,String dob,Double salary,String address, String city,String province,Integer postalCode);
    void deleteCustomerDetails(String id);
    void updateCustomerDetails(String title,String name ,String dob,Double salary,String address,String city,String province,Integer postalCode,String id);
}
