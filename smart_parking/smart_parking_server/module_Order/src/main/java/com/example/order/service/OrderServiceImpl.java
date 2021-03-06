package com.example.order.service;

import com.example.order.dao.OrderDao;
import com.example.order.entity.Order_information;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl {


    @Resource
    private OrderDao orderDao;


    @Resource
    private RestTemplate restTemplate;



    private final String vehicleURl="http://ClientVehicle/Vehicle";



    private final String parkingLotURl="http://ClientParkingLots/ParkingLots";

    /**
     * TODO：生成订单
     * @param user_name 生成订单的用户
     * @param license_plate_number 车牌号
     * @param parking_lot_number 停车场编号
     * @return 是否成功
     */
    public String generate_order(String user_name,String license_plate_number,String parking_lot_number) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String generation_time= formatter.format(date);

        //生成订单编号
        String order_number;
        order_number = user_name;
        order_number += System.currentTimeMillis();

        //判断停车场是否存在
        StringBuilder classUrl=new StringBuilder();
        classUrl.append(parkingLotURl+"/getParkingName/"+parking_lot_number);
        String parkingName=restTemplate.getForObject(classUrl.toString(),String.class);
        if (parkingName==null||parkingName.equals("")){
            return "停车场不存在";
        }
        classUrl.delete(0, classUrl.length());

        //检查车辆信息是否注册
        classUrl.append(vehicleURl+"/check_license_plate_number/"+user_name+"/"+license_plate_number);
        int check=restTemplate.getForObject(classUrl.toString(),Integer.class);
        if (check==0){
            return "未注册车辆信息";
        }

        //检查是否有未完成订单
        int incomplete_Order=orderDao.find_Incomplete_Order(user_name);
        if (incomplete_Order>0){
            return "您还有进行中或未支付的订单，请完成订单后再预约";
        }

        int i = orderDao.add_Order(order_number, generation_time, user_name,  null, null, parkingName, parking_lot_number, license_plate_number, 0, "等待进入");
        if (i<=0){
            return "订单生成失败";
        }
        else {
            return "订单开始";
        }

    }


    /**
     * TODO：获取所有订单列表
     * @return 所有订单
     */
    public List<Order_information> getAllOrders() {
        return orderDao.getAllOrders();
    }





    /**
     * TODO：获取某位用户的订单列表
     * @param user_name 所查找的用户
     * @return 用户订单
     */
    public List<Order_information> getOrderByUsername(String user_name) {
        return orderDao.getOrderByUsername(user_name);
    }




    /**
     * TODO：获取停车场订单列表
     * @param parking_lot_number 停车场编号
     * @return 停车场订单
     */
    public List<Order_information> getParkingOrders(String parking_lot_number) {
        return orderDao.getOrderByParking(parking_lot_number);
    }






    /**
     * TODO：根据订单号查找订单
     * @param order_number 订单号
     * @return 查找订单
     */
    public Order_information getOrderByNumber(String order_number) {
        return orderDao.getOrderByNumber(order_number);
    }





    /**
     * TODO：车辆进入
     * @param license_plate_number 车牌号
     * @param parking_lot_number 停车场编号
     * @return 是否成功
     */
    public String setStatus_in (String license_plate_number ,String parking_lot_number) {
        Order_information order = orderDao.getOrderByParkingAndOrder(parking_lot_number, license_plate_number);
        if (order==null){
            return "未找到此订单";
        }
        if (order.getInTime()!=null||order.getOutTime()!=null){
            return "订单错误";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String generation_time= formatter.format(date);
        orderDao.vehicle_entry(generation_time,order.getOrder_number());
        return setStatus("进行中",order.getOrder_number());
    }



    /**
     * TODO：车辆离开
     * @param license_plate_number 车牌号
     * @param parking_lot_number 停车场编号
     * @return 是否成功
     */
    public String setStatus_out (String license_plate_number ,String parking_lot_number) {
        Order_information order = orderDao.getOrderByParkingAndOrder(parking_lot_number, license_plate_number);
        if (order==null){
            return "未找到此订单";
        }
        if (order.getOutTime()!=null){
            return "订单错误";
        }
        //判断停车场是否存在
        String classUrl=parkingLotURl+"/getParkingName/"+parking_lot_number;
        String parkingName=restTemplate.getForObject(classUrl,String.class);
        if (parkingName==null||parkingName.equals("")){
            return "未找到此订单";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date outTime = new Date(System.currentTimeMillis());
        String generation_time= formatter.format(outTime);
        orderDao.vehicle_departure(generation_time,order.getOrder_number());
        setMoney(order.getOrder_number());
        return setStatus("未支付",order.getOrder_number());
    }


    /**
     * TODO：订单支付完成
     * @param order_number 订单编号
     * @return 是否成功
     */
    public void setMoney (String order_number){
        Order_information order = orderDao.getOrderByNumber(order_number);
        //获取停车场的收费标准
        String classUrl=parkingLotURl+"/getParkingBilling_rules/"+order.getParking_lot_number();
        Float billing_rules=restTemplate.getForObject(classUrl,Float.class);
        int hours = (int) ((order.getOutTime().getTime() - order.getInTime().getTime()) / (1000 * 60* 60));
        float money=hours*billing_rules;
        orderDao.set_money(money,order.getOrder_number());
    }


    /**
     * TODO：订单支付完成
     * @param user_name 用户名
     * @param parking_lot_number 订单编号
     * @return 是否成功
     */
    public Order_information userGetParkingOrder ( String user_name, String parking_lot_number){
       return orderDao.userGetOrderByNumber(user_name,parking_lot_number);
    }



    /**
     * TODO：订单支付完成
     * @param user_name 用户名
     * @param order_number 订单编号
     * @return 是否成功
     */
    public String complete_Order (String user_name,String order_number){
        Order_information order = orderDao.userGetOrderByNumber(user_name,order_number);
        if (order==null){
            return "未找到订单";
        }
        if (order.getOutTime()==null||order.getInTime()==null){
            return "请在订单结束后支付";
        }
        return setStatus("已完成",order.getOrder_number());
    }



    /**
     * TODO：app取消订单
     * @param user_name 用户名
     * @param order_number 订单编号
     * @return 是否成功
     */
    public String app_cancellation_Order (String user_name,String order_number){
        Order_information order = orderDao.userGetOrderByNumber(user_name,order_number);
        if (order==null){
            return "未找到订单";
        }
        else if (order.getOrder_status().equals("已取消")){
            return "请勿重复取消订单";
        }
        else if (order.getOrder_status().equals("已完成")){
            return "订单已完成";
        }
        else if (order.getOutTime()!=null||order.getInTime()!=null){
            return "订单进行中不可取消，若要取消可联系停车场";
        }
        return setStatus("已取消",order.getOrder_number());
    }


    /**
     * TODO：停车场取消订单
     * @param order_number 订单编号
     * @return 是否成功
     */
    public String parking_cancellation_Order (String parking_lot_number,String order_number){
        Order_information order = orderDao.getOrderByParkingAndOrder(parking_lot_number, order_number);
        if (order==null){
            return "未找到订单";
        }
        else if (order.getOrder_status().equals("已取消")){
            return "请勿重复取消订单";
        }
        else if (order.getOrder_status().equals("已完成")){
            return "订单已完成";
        }
        return setStatus("已取消",order_number);
    }




    /**
     * TODO：修改订单状态
     * @param status 状态信息
     * @param order_number 订单号
     * @return 是否成功
     * */
    public String setStatus (String status,String order_number){
        int i = orderDao.change_Order_status(status,order_number);
        if (i<=0){
            return "订单:"+order_number+" 状态修改失败";
        }else {
            return "订单:"+order_number+" 状态修改为："+status;
        }
    }




}
