package cn.deercare.service.impl;

import cn.deercare.model.Order;
import cn.deercare.mapper.OrderMapper;
import cn.deercare.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-06
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
