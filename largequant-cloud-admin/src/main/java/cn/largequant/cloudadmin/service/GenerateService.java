package cn.largequant.cloudadmin.service;


import cn.largequant.cloudadmin.dto.BeanField;
import cn.largequant.cloudadmin.dto.GenerateInput;

import java.util.List;

public interface GenerateService {

	/**
	 * 获取数据库表信息
	 * 
	 * @param tableName
	 * @return
	 */
	List<BeanField> listBeanField(String tableName);

	/**
	 * 转成驼峰并大写第一个字母
	 * 
	 * @param string
	 * @return
	 */
	String upperFirstChar(String string);

	/**
	 * 生成代码
	 * 
	 * @param input
	 */
	void saveCode(GenerateInput input);
}
