package cn.deercare.controller;


import cn.deercare.controller.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JiangYuan
 * @since 2019-09-23
 */
@RestController
@RequestMapping("/user-wechat")
@Api(value = "/user-wechat", tags = {""})
public class UserWechatController extends BaseController {

}
