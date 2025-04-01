package vuetest.domain;

import java.util.Date;

import lombok.Data;
import lombok.ToString;
import vuetest.infra.AbstractEvent;

@Data
@ToString
public class OrderPlaced extends AbstractEvent {

    private Long id;
    private Integer qty;
    private String userId;
    private Date orderDate;
    private Object inventoryId;
    private Object address;
}
