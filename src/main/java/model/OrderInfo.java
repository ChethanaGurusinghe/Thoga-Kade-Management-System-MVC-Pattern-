package model;

import lombok.*;

import java.time.LocalDate;

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
