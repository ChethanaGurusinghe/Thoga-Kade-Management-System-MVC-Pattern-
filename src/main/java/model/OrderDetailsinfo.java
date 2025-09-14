package model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDetailsinfo {
    private String id;
    private String code;
    private Integer orderQty;
    private Integer discount;
}
