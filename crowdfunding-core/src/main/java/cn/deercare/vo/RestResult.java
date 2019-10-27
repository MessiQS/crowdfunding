package cn.deercare.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel(value = "result", description = "返回数据")
public class RestResult {
	@ApiModelProperty(value = "状态码，00：成功、01失败", required = true)
	private String resultCode;
	@ApiModelProperty(value = "消息", required = true)
	private String message;
	@ApiModelProperty(value = "数据")
	private String data;
}
