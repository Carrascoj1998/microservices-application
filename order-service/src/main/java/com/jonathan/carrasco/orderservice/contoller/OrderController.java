package com.jonathan.carrasco.orderservice.contoller;

import com.jonathan.carrasco.orderservice.dto.OrderRequest;
import com.jonathan.carrasco.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest){
        orderService.placedOrder(orderRequest);
        return "Order placed Successfully";
    }
}
