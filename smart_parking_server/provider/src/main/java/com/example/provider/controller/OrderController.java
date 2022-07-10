package com.example.provider.controller;

import com.example.provider.entiry.Order_information;
import com.example.provider.service.OrderServiceImpl;
import com.example.provider.service.ParkingLotServiceImpl;
import com.example.provider.service.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Component
@RestController
@Api(tags = "订单模块")
@RequestMapping("/Order")
public class OrderController {


    @Autowired(required = false)
    private OrderServiceImpl orderService;



    @ApiOperation(value = "订单生成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user_name", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "parking_lot_number", value = "停车场编号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "license_plate_number", value = "车牌号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "UUID", value = "通用唯一识别码", required = true, dataType = "String")

    })
    @PostMapping(value = "/generate_order", produces = "text/plain;charset=utf-8")
    public String  generate_order (String user_name,String license_plate_number,String parking_lot_number,String UUID){
        return orderService.generate_order(user_name,license_plate_number,parking_lot_number);
    }

    @ApiOperation(value = "app获取用户订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user_name", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "UUID", value = "通用唯一识别码", required = true, dataType = "String")
    })
    @GetMapping(value = "/getUserOrder",produces = "application/json; charset=utf-8")
    public List<Order_information> getUserOrder (String user_name, String UUID){
        return orderService.getUserOrders(user_name);
    }


    @ApiOperation(value = "停车场管理员获取用户订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "parking_lot_number", value = "停车场编号", required = true, dataType = "String")
    })
    @GetMapping(value = "/getParkingOrder", produces = "application/json; charset=utf-8")
    public List<Order_information> getParkingOrder (String parking_lot_number){
        return orderService.getParkingOrders(parking_lot_number);
    }


    @ApiOperation(value = "搜索订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user_name", value = "用户名", required = true, dataType = "String"),
            @ApiImplicitParam(name = "order_number", value = "订单编号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "UUID", value = "通用唯一识别码", required = true, dataType = "String")

    })
    @GetMapping(value = "/getOrder", produces = "application/json; charset=utf-8")
    public Order_information getOrder (String user_name,String order_number,String UUID){
        return orderService.getOrder(user_name,order_number);
    }


}
