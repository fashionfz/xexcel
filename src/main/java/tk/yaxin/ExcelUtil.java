package tk.yaxin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
 * EXCEL处理工具类
 *
 */
public class ExcelUtil {
	
	
	@SuppressWarnings("rawtypes")
	private static Map<String, ExcelConvert> getConvert(Field[] fields) throws InstantiationException, IllegalAccessException {
		Map<String, ExcelConvert> convertMap = new HashMap<String, ExcelConvert>();
		for (Field field : fields) {
			ExcelField annotation = field.getAnnotation(ExcelField.class);
			if (annotation != null && !annotation.covertClass().isInterface()) {
				ExcelConvert convert = (ExcelConvert) annotation.covertClass().newInstance();
				convertMap.put(field.getName(), convert);
			}
		}
		return convertMap;
	}
	
	
	private static List<Field> getExcelField(Field[] fields) {
		List<Field> excelField = new ArrayList<Field>();
		for (Field field : fields) {
			ExcelField annotation = field.getAnnotation(ExcelField.class);
			if (annotation != null) {
				excelField.add(field);
			}
		}
		Collections.sort(excelField, new Comparator<Field>() {
			public int compare(Field o1, Field o2) {
				ExcelField annotation1 = o1.getAnnotation(ExcelField.class);
				ExcelField annotation2 = o2.getAnnotation(ExcelField.class);
				return annotation1.sort() - annotation2.sort();
			}
		});
		return excelField;
	}
	

	/**
	 * 
	 * @param clazz 传入结合包含的class
	 * @param list  集合
	 * @param os    输出流
	 * @throws IOException IOException
	 * @throws IllegalAccessException  IllegalAccessException
	 * @throws InstantiationException  InstantiationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void export(Class<?> clazz, List<?> list, OutputStream os) throws IOException, InstantiationException, IllegalAccessException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("sheet1");
		XSSFRow header = sheet.createRow(0);
		Field[] fields = ProxyUtil.getFields(clazz); // Field,convert
		List<Field> excelField = getExcelField(fields);
		for (int m = 0; m < excelField.size(); m++) {
			ExcelField annotation = excelField.get(m).getAnnotation(ExcelField.class);
			if (annotation != null) {
				header.createCell(m).setCellValue(String.valueOf(annotation.lableName()));
			}
		}
		int row = 1;
		Map<String, ExcelConvert> convertMap = getConvert(fields);
		for (Object info : list) {
			XSSFRow data = sheet.createRow(row++);
			for (int i = 0; i < excelField.size(); i++) {
				Field field = excelField.get(i);
				ExcelConvert convert = convertMap.get(field.getName());
				Object value = ProxyUtil.getFieldValue(info, field);
				if(value == null) {
					continue;
				}
				if (convert != null) {
					data.createCell(i).setCellValue(convert.convertToExcel(value));
				}else {
					data.createCell(i).setCellValue(ProxyUtil.toString(value, field.getType()));
				}
				
			}
		}
		workbook.write(os);
		workbook.close();
	}

	/**
	 * 
	 * @param <E> 泛型类
	 * @param clazz 传入结合包含的class
	 * @param in 输入流
	 * @return 实体集合
	 * @throws IllegalAccessException IllegalAccessException
	 * @throws InstantiationException InstantiationException
	 * @throws IOException IOException
	 * @throws ParseException ParseException
	 * @throws IllegalArgumentException IllegalArgumentException
	 * @throws SecurityException SecurityException
	 * @throws NoSuchFieldException NoSuchFieldException
	 */
	@SuppressWarnings({ "resource", "rawtypes" })
	public static <E> List<E> leadingIn(Class<E> clazz, InputStream in) throws InstantiationException, IllegalAccessException, IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, ParseException {
		List<E> list = new ArrayList<E>();
		Workbook book;
		try {
			book = new XSSFWorkbook(in);
		} catch (Exception e) {
			book = new HSSFWorkbook(in);
		}
		Sheet sheet = book.getSheetAt(0);
		List<String> fieldNames = new ArrayList<String>();
		Row header = sheet.getRow(0);
		for (int cIndex = header.getFirstCellNum(); cIndex < header.getLastCellNum(); cIndex++) {
			Cell cell = header.getCell(cIndex);
			fieldNames.add(cell.toString());
		}
		Map<String, Class<?>> map = ProxyUtil.getFieldMap(clazz);
		Map<String, ExcelConvert> convert = getConvert(ProxyUtil.getFields(clazz));
		for (int row = 1; row < sheet.getLastRowNum(); row++) {
			E obj = clazz.newInstance();
			Row data = sheet.getRow(row);
			for (int i = 0; i < fieldNames.size(); i++) {
				String field = fieldNames.get(i);
				if(map.get(field) == null) {
					continue;
				}
				Cell cell = data.getCell(i);
				ExcelConvert cc = convert.get(field);
				if (cc != null && !cc.getClass().isInterface())
					ProxyUtil.setFieldValue(obj, field, cc.convertToObject(cell.toString()));
				else
					ProxyUtil.setter(obj, field, cell.toString(), map.get(field));
			}
			list.add(obj);
		}
		return list;
	}
}
