package model;

import lombok.*;



@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderInfo {
    private String id;
    private String orderDate;
    private String custId;
}
