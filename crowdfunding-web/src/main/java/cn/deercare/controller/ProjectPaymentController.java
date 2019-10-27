package cn.deercare.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

import cn.deercare.controller.base.BaseController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author JiangYuan
 * @since 2019-10-05
 */
@RestController
@RequestMapping("/project-payment")
@Api(value = "/project-payment", tags = {""})
public class ProjectPaymentController extends BaseController {

}
