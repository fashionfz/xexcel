package tk.yanxin;

/**
 * 
 * @ClassName: ExcelConvert 
 * @Description: excel字段转换接口 
 * @author bin.deng  ibcm@qq.com
 * @date 2014年9月17日 下午1:18:36 
 *
 */
public interface ExcelConvert {

	/**
	 * 
	 * @Title: convert 
	 * @Description: 从excel转换成对象的实现方法
	 * @param obj
	 * @return
	 */
	public Object convertToObj(Object obj);
	/**
	 * 
	 * @Title: convert 
	 * @Description: 从对象转换excel的实现方法
	 * @param obj
	 * @return
	 */
	public Object convertToExcel(Object obj);
}
