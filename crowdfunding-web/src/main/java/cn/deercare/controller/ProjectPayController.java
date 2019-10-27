package cn.deercare.controller;

import cn.deercare.annotation.Authorization;
import cn.deercare.common.ResultCode;
import cn.deercare.controller.base.BaseController;
import cn.deercare.enums.PayType;
import cn.deercare.model.Project;
import cn.deercare.model.User;
import cn.deercare.service.ProjectPaymentService;
import cn.deercare.service.ProjectService;
import cn.deercare.utils.ProjectUtil;
import cn.deercare.utils.UserUtil;
import cn.deercare.vo.RestResult;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/project")
@Api(value = "/project-pay", tags = {"众筹项目付款相关"})
@Authorization
public class ProjectPayController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(ProjectPayController.class);

    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectPaymentService projectPaymentService;

    @GetMapping("pay/{id}/{amount}")
    @ApiOperation(value = "众筹项目支付", notes = "", response = RestResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="path", name = "id", value = "项目id", required = true, dataTypeClass = long.class),
            @ApiImplicitParam(paramType="path", name = "amount", value = "金额", required = true, dataTypeClass = long.class)
    })
    public Object pay(@PathVariable("id") Long id, @PathVariable("amount")BigDecimal amount,
        HttpServletRequest request){
        Map<String, Object> json = createJson();
        logBefore(logger, "众筹项目支付");
        try{
            // 获取项目信息
            Project project = projectService.getDetailedById(id);
            // 获取项目付款方式
            PayType payType = ProjectUtil.getProjectPayType(project.getProjectPayment().getId());
            // 该项目已众筹的金额
            BigDecimal accountAmount = project.getAccountAmount();
            // 验证是否超过目标金额(已有众筹金额+本地金额 <= 目标金额)
            if(accountAmount.add(amount).compareTo(project.getAmount()) == 1){
                this.setJson(json, ResultCode.REQ_FAIL, "失败--超过目标金额");
                return json;
            }
            // 验证付款金额
            switch (payType){
                case UNLIMITED:
                    // 付款金额小于最低金额
                    if(amount.compareTo(project.getProjectPayment().getMin()) == -1){
                        this.setJson(json, ResultCode.REQ_FAIL, "失败--付款金额小于最低金额");
                        return json;
                    }
                    break;
                case MULTIPLE:
                    // 付款金额小于最低金额或付款金额不为最小金额的倍数
                    if(amount.compareTo(project.getProjectPayment().getMin()) == -1
                        || amount.divideAndRemainder(project.getProjectPayment().getMin())[1]
                            .compareTo(BigDecimal.ZERO) == 0){
                        this.setJson(json, ResultCode.REQ_FAIL, "失败--付款金额小于最低金额或付款金额不为最小金额的倍数");
                        return json;
                    }
                    break;
            }
            // 进入支付流程
            Object object = projectPaymentService.pay(project, amount, request);
            // System.out.println(ArrayUtils.toString((String[])object));
            json.put("data", object);
            this.setJson(json, ResultCode.REQ_SUCCESS, "获取支付信息成功");
            return json;
        }catch (Exception e){
            logger.error(e.toString(), e);
            this.setJson(json, ResultCode.REQ_FAIL, "失败--众筹项目支付");
        }finally {
            logAfter(logger, json);
        }
        return json;
    }

    public static void main(String[] args) {
        BigDecimal b1 = new BigDecimal("1");
        BigDecimal b2 = new BigDecimal("1.101");
        BigDecimal b3 = new BigDecimal("2.1");
        System.out.println(b1.add(b2).compareTo(b3) == 1);
    }


}
