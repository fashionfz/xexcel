package tk.yaxin;

import java.lang.reflect.Type;

/**
 * 
 * @ClassName: ExcelConvert 
 * @Description: excel字段转换接口 
 * @author bin.deng  ibcm@qq.com
 * @date 2014年9月17日 下午1:18:36 
 *
 */
public interface ExcelConvert<O, E> {
	
	
	public Type getObjectFieldType();

	
	public Type getExcelFieldType();
	/**
	 * 
	 * @Title: convert 
	 * @Description: 从excel转换成对象的实现方法
	 * @param obj
	 * @return
	 */
	public O convertToObject(E obj);
	/**
	 * 
	 * @Title: convert 
	 * @Description: 从对象转换excel的实现方法
	 * @param obj
	 * @return
	 */
	public E convertToExcel(O obj);
}
