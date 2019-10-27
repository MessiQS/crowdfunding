package cn.deercare.controller.base;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @ClassName: BaseController
 * @Description: TODO(控制器基类)
 * @author 姜源
 * @date 2017年5月22日 上午11:37:59
 */
@CrossOrigin(origins = "*")
public abstract class BaseController {

	protected void logBefore(Logger logger, String interfaceName) {

		logger.info(">>" + interfaceName);
		logger.info("start");
	}

	protected void logAfter(Logger logger,Map<String, Object> json) {
		logger.info(json.get("message").toString());
		logger.info("end");
	}

	protected Map<String, Object> createJson() {

		return new HashMap<String, Object>(4);
	}

	/**
	 * @method setJson(格式化输出)
	 * @return void
	 * @author ZHY
	 * @date 2016年4月28日 下午3:23:39
	 */
	protected void setJson(Map<String, Object> json, String resultCode, String message, Object object) {

		json.put("resultCode", resultCode);
		json.put("message", message);
		json.put("data", object);
	}

	/**
	 * @method setJson(格式化输出)
	 * @return void
	 * @author ZHY
	 * @date 2016年4月28日 下午3:23:39
	 */
	protected void setJson(Map<String, Object> json, String resultCode, String message) {

		json.put("resultCode", resultCode);
		json.put("message", message);
	}

	/**
	 * @method setJson(格式化输出data)
	 * @return void
	 * @author 张海阳
	 * @date 2017年6月7日 上午9:52:42
	 */
	protected void setJson(Map<String, Object> json, Object object) {

		json.put("data", object);
	}

}
