package vuetest.infra;
import vuetest.domain.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/orders")
@Transactional
public class OrderController {
    @Autowired
    OrderRepository orderRepository;

    @RequestMapping(value = "/orders/{id}/modifyorderinfo",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8")
    public Order modifyOrderinfo(@PathVariable(value = "id")  id, @RequestBody ModifyOrderinfoCommand modifyOrderinfoCommand, HttpServletRequest request, HttpServletResponse response) throws Exception {
            System.out.println("##### /order/modifyOrderinfo  called #####");
            Optional<Order> optionalOrder = orderRepository.findById(id);
            
            optionalOrder.orElseThrow(()-> new Exception("No Entity Found"));
            Order order = optionalOrder.get();
            order.modifyOrderinfo(modifyOrderinfoCommand);
            
            orderRepository.save(order);
            return order;
            
    }
}
//>>> Clean Arch / Inbound Adaptor
