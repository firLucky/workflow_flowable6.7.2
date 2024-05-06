package com.fir.flow.config.mybatis;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import java.util.List;


/**
 * @author fir
 */
public class EasySqlInjector extends DefaultSqlInjector {

	@Override
	public List<AbstractMethod> getMethodList(Class<?> mapperClass) {

		List<AbstractMethod> methodList = super.getMethodList(mapperClass);
		// 添加InsertBatchSomeColumn方法
		methodList.add(new InsertBatchSomeColumn());
		return methodList;
	}
	
}
