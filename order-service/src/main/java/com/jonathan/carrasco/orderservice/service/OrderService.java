package com.jonathan.carrasco.orderservice.service;

import com.jonathan.carrasco.orderservice.dto.InventoryResponse;
import com.jonathan.carrasco.orderservice.dto.OrderLineItemsDto;
import com.jonathan.carrasco.orderservice.dto.OrderRequest;
import com.jonathan.carrasco.orderservice.model.Order;
import com.jonathan.carrasco.orderservice.model.OrderLineItems;
import com.jonathan.carrasco.orderservice.repository.OrderRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRespository orderRespository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    public String placedOrder(OrderRequest orderRequest){
       Order order = new Order();
       order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = new ArrayList<>();
        List<OrderLineItemsDto> dtoList = orderRequest.getOrderLineItemsDtoList();
        if (dtoList != null) {
            orderLineItems = dtoList.stream()
                    .map(this::mapToDto)
                    .toList();
        }
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();


        log.info("Calling inventory service");

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())){
            //call inventory service and place order if
            //product is in stock
            InventoryResponse[] inventoryResponsesArray  = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory", uriBuilder ->
                            uriBuilder.queryParam("skuCode", skuCodes)
                                    .build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = Arrays.stream(inventoryResponsesArray)
                    .allMatch(InventoryResponse:: getIsInStock);



            if(allProductsInStock){
                orderRespository.save(order);
                return "Order placed Successfully";
            }else{
                throw new
                    IllegalArgumentException("Product is not in stock, please try again");
            }

        }finally {
            inventoryServiceLookup.end();
        }


    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }


}
