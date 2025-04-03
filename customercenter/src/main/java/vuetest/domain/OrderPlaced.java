package vuetest.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import vuetest.infra.AbstractEvent;

@Data
public class OrderPlaced extends AbstractEvent {

    private Long id;
    private Integer qty;
    private String userId;
    private Date orderDate;
    private Object inventoryId;
    private Object address;
}
