package tk.yaxin;

/**
 * excel字段转换接口 
 *
 */
public interface ExcelConvert<E> {
	
	
	/**
	 * 从excel转换成对象的实现方法
	 * @param obj excel值
	 * @return 实体属性值
	 */
	public E convertToObject(String obj);
	/**
	 * 从对象转换excel的实现方法
	 * @param obj 实体属性值
	 * @return excel值
	 */
	public String convertToExcel(E obj);
}
