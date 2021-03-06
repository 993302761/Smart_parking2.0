package com.example.parkingLots.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   //生成get set equals toString等方法
@NoArgsConstructor      //生成无参构造方法
public class Parking {


    private String parking_lot_name;                 //停车场名
    private String parking_in_the_city;              //停车场所在城市
    private String parking_lot_number;                //停车场编号
    private int parking_spaces_num;                 //车位数量
    private float billing_rules;                   //计费规则 (元/小时)
    private String longitude;                      //经度
    private String latitude;                      //纬度


}
