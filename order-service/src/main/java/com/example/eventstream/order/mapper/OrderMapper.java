package com.example.eventstream.order.mapper;


import com.example.eventstream.order.dto.request.CreateOrderRequest;
import com.example.eventstream.order.dto.response.OrderResponse;
import com.example.eventstream.order.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Order toEntity(CreateOrderRequest request);
    
    OrderResponse toResponse(Order entity);
}
