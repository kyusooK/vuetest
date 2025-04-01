package vuetest.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import vuetest.InventoryApplication;

@Entity
@Table(name = "Inventory_table")
@Data
//<<< DDD / Aggregate Root
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productName;

    private Integer qty;

    @Embedded
    private Photo photo;

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = InventoryApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    //<<< Clean Arch / Port Method
    public static void decreaseStock(OrderPlaced orderPlaced) {
        //implement business logic here:

       
        ObjectMapper mapper = new ObjectMapper();
        Map<Long, Object> orderMap = mapper.convertValue(orderPlaced.getInventoryId(), Map.class);

        repository().findById(orderMap.get("id")).ifPresent(inventory->{
            
            inventory.setStock(inventory.getStock() - orderPlaced.getQty());
            repository().save(inventory);

            StockDecreased stockDecreased = new StockDecreased(inventory);
            stockDecreased.publishAfterCommit();

         });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
