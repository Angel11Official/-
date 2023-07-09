package org.reggieapp.dto;


import org.reggieapp.entity.Orders;
import org.reggieapp.entity.OrderDetail;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
