package vuetest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import vuetest.OrderApplication;
import vuetest.domain.OrderPlaced;

@Entity
@Table(name = "Order_table")
@Data
//<<< DDD / Aggregate Root
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer qty;

    private String userId;

    private Date orderDate;

    @Embedded
    private InventoryId inventoryId;

    @Embedded
    private Address address;

    @PostPersist
    public void onPostPersist() {
        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }

    //<<< Clean Arch / Port Method
    public void modifyOrderinfo(ModifyOrderinfoCommand modifyOrderinfoCommand) {
        repository().findById(this.getId()).ifPresent(order ->{
            this.setQty(modifyOrderinfoCommand.getQty());
        });

        OrderInfoModified orderInfoModified = new OrderInfoModified(this);
        orderInfoModified.publishAfterCommit();
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
