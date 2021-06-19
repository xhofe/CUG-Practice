package com.hh.mapper;

import com.hh.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrderMapper {
    /**
     * 获取一个用户的所有订单
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> getOrdersByUserId(int userId);

    /**
     * 添加订单
     * @param order 订单
     * @return 添加的记录条数
     */
    int addOrder(Order order);

    /**
     * 删除一个订单
     * @param orderId 订单
     * @return 被操作的记录条数
     */
    int deleteOrder(int orderId);

    List<Order> getOrdersByUserIdAndGoodsId(@Param("userId") int userId, @Param("goodsId") int goodsId);

    Order getOrderById(int id);

    int updateOrder(Order order);

    List<Order> getAllOrders();
}
