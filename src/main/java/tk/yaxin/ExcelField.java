package tk.yaxin;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解需要导出的字段
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface ExcelField {

	/**
	 * 
	 * excel字段名称
	 * @return excel字段标题
	 */
	String lableName() default "";
	/**
	 * 
	 * 转换类，需实现ExcelConvert接口
	 * @return 转换类
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends ExcelConvert> covertClass() default ExcelConvert.class;
	/**
	 * 
	 * 排序，越小越靠前
	 * @return 排序
	 */
	int sort() default 99;
}
