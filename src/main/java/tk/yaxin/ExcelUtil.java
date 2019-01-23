package tk.yaxin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @Title EXCEL处理工具类
 * @Description: 该类主要包括excel的导入、导出
 * @author bin.deng ibcm@qq.com
 * @date 2014年9月25日 上午10:53:48
 *
 */
public class ExcelUtil {

	/**
	 * 
	 * @Title: export
	 * @Description: 后台数据导出excel
	 * @param clazz 传入结合包含的class
	 * @param list  结合
	 * @param os    输出流
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void export(Class<?> clazz, List<?> list, OutputStream os) throws IOException, InstantiationException, IllegalAccessException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("sheet1");
		XSSFRow header = sheet.createRow(0);
		Field[] fields = clazz.getDeclaredFields(); // Field,convert
		List<Object[]> excelField = new LinkedList<Object[]>();
		Map<String, ExcelConvert> convertMap = new HashMap<String, ExcelConvert>();
		int m = 0;
		for (int j = 0; j < fields.length; j++) {
			ExcelField annotation = fields[j].getAnnotation(ExcelField.class);
			if (annotation != null) {
				Object[] objs = new Object[2];
				header.createCell(m++).setCellValue(String.valueOf(annotation.lableName()));
				objs[0] = fields[j];
				Class<? extends ExcelConvert> convert = annotation.covertClass();
				objs[1] = convert;
				excelField.add(objs);
			}
		}
		int i = 1;
		Object[] objs;
		for (Object info : list) {
			XSSFRow data = sheet.createRow(i++);
			for (int j = 0; j < excelField.size(); j++) {
				objs = excelField.get(j);
				Object value = ProxyUtil.getFieldValue(info, (Field) objs[0]);
				if(value == null) {
					continue;
				}
				if (!((Class<?>) objs[1]).isInterface()) {// 不是接口就是实例化
					if(convertMap.containsKey(objs[1].toString())) {
						ExcelConvert convert = convertMap.get(objs[1].toString());
						data.createCell(j).setCellValue(convert.convertToExcel(value));
					}else {
						ExcelConvert convert = (ExcelConvert) ((Class<?>) objs[1]).newInstance();
						convertMap.put(objs[1].toString(), convert);
						data.createCell(j).setCellValue(convert.convertToExcel(value));
					}
				}else {
					data.createCell(j).setCellValue(ProxyUtil.toString(value, ((Field) objs[0]).getType()));
				}
				
			}
		}
		workbook.write(os);
		workbook.close();
	}

	/**
	 * 
	 * @Title: leadingIn
	 * @Description: excel导入
	 * @param clazz
	 * @param in
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	@SuppressWarnings("resource")
	public static List<?> leadingIn(Class<?> clazz, InputStream in) throws InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, ParseException {
		List<Object> list = new ArrayList<Object>();
		int i = 0;
		Map<String, Class<?>> map = new HashMap<String, Class<?>>();
		Workbook book;
		try {
			book = new HSSFWorkbook(in);
		} catch (Exception e) {
			book = new XSSFWorkbook(in);
		}
		Sheet sheet = book.getSheetAt(0);
		List<String> fieldNames = new ArrayList<String>();
		Row header = sheet.getRow(0);
		for (int cIndex = header.getFirstCellNum(); cIndex < header.getLastCellNum(); cIndex++) {
			Cell cell = header.getCell(cIndex);
			fieldNames.add(cell.toString());
		}
		map = ProxyUtil.getFields(clazz);
		for (int row = 1; row < sheet.getLastRowNum(); row++) {
			i = 0;
			Object obj = clazz.newInstance();
			Row data = sheet.getRow(row);
			for (String field : fieldNames) {
				Cell cell = data.getCell(i++);
				ProxyUtil.setter(obj, field, cell.toString(), map.get(field));
			}
			list.add(obj);
		}
		in.close();
		return list;
	}

	/**
	 * 
	 * @Title: leadingIn
	 * @Description: excel导入
	 * @param clazz
	 * @param convert
	 * @param in
	 * @return
	 * @throws IOException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 * @throws ParseException 
	 */
	@SuppressWarnings({ "rawtypes", "resource" })
	public static List<?> leadingIn(Class<?> clazz, Map<String, ExcelConvert> convert, InputStream in) throws IOException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException, IllegalArgumentException, ParseException {
		List<Object> list = new ArrayList<Object>();
		int i = 0;
		Workbook book;
		try {
			book = new HSSFWorkbook(in);
		} catch (Exception e) {
			book = new XSSFWorkbook(in);
		}
		Sheet sheet = book.getSheetAt(0);
		List<String> fieldNames = new ArrayList<String>();
		Row header = sheet.getRow(0);
		for (int cIndex = header.getFirstCellNum(); cIndex < header.getLastCellNum(); cIndex++) {
			Cell cell = header.getCell(cIndex);
			fieldNames.add(cell.toString());
		}
		Map<String, Class<?>> map = ProxyUtil.getFields(clazz);
		for (int row = 1; row < sheet.getLastRowNum(); row++) {
			i = 0;
			Object obj = clazz.newInstance();
			Row data = sheet.getRow(row);
			for (String field : fieldNames) {
				Cell cell = data.getCell(i++);
				ExcelConvert cc = convert.get(field);
				if (cc != null && !cc.getClass().isInterface())
					ProxyUtil.setFieldValue(obj, field, cc.convertToObject(cell.toString()));
				else
					ProxyUtil.setter(obj, field, cell.toString(), map.get(field));
			}
			list.add(obj);
		}
		in.close();
		return list;
	}

}
