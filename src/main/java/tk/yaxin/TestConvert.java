/******************************************************************
 *
 *    Java Lib For Android, Powered By Gome.
 *
 *    Copyright (c) 2001-2014 Digital Telemedia Co.,Ltd
 *    http://www.gome.com/
 *
 *    Package:     tk.yaxin
 *
 *    Filename:    TestConvert.java
 *
 *    Description: TODO(用一句话描述该文件做什么)
 *
 *    Copyright:   Copyright (c) 2001-2014
 *
 *    Company:     Digital Telemedia Co.,Ltd
 *
 *    @author:     pc
 *
 *    @version:    1.0.0
 *
 *    Create at:   2019年1月22日 下午3:06:49
 *
 *    Revision:
 *
 *    2019年1月22日 下午3:06:49
 *        - first revision
 *
 *****************************************************************/
package tk.yaxin;

import java.lang.reflect.Type;

/**
 * @ClassName TestConvert
 * @Description TODO(这里用一句话描述这个类的作用)
 * @author dengbin4@gome.com.cn
 * @Date 2019年1月22日 下午3:06:49
 * @version 1.0.0
 */
public class TestConvert implements ExcelConvert<String, String> {


	public Type getObjectFieldType() {
		return String.class;
	}


	public Type getExcelFieldType() {
		return String.class;
	}


	public String convertToObject(String obj) {
		return null;
	}


	public String convertToExcel(String obj) {
		return null;
	}


}
