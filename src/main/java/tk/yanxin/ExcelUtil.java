package tk.yanxin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

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
	 * @param list 结合
	 * @param os 输出流
	 */
	public static void export(Class<?> clazz,List<?> list,OutputStream os){
		try{
			WritableWorkbook book = Workbook.createWorkbook(os);
			WritableSheet sheet = book.createSheet("page one", 0);
			int i=0;
			Field[] fields = clazz.getDeclaredFields();
			List<Object[]> excelField = new LinkedList<Object[]>();
			int m=0;
			for(int j=0;j<fields.length;j++){
				ExcelField annotation = fields[j].getAnnotation(ExcelField.class);
				if(annotation!=null){
					Object[] objs = new Object[2];
					Label label = new Label(m++, i, String.valueOf(annotation.lableName()));
					sheet.addCell(label);
					objs[0] = fields[j];
					Class<? extends ExcelConvert> convert = annotation.covertClass();
					objs[1] = convert;
					excelField.add(objs);
				}
			}
			i++;
			Object[] objs;
			for(Object info : list) {
				for(int j=0;j<excelField.size();j++){
					try{
						objs = excelField.get(j);
						Label label = null;
						if(((Field)objs[0]).getType() == Boolean.class || ((Field)objs[0]).getType() == boolean.class){
							String value = String.valueOf(ProxyUtil.isser(info, ((Field)objs[0]).getName()));
							if(!((Class<?>)objs[1]).isInterface()){//不是接口就是实例化
								ExcelConvert convert = (ExcelConvert) ((Class<?>) objs[1]).newInstance();
								value = (String) convert.convertToExcel(value);
							}
							label = new Label(j, i, value);
						}else{
							String value = String.valueOf(ProxyUtil.getter(info, ((Field)objs[0]).getName()));
							if(!((Class<?>)objs[1]).isInterface()){//不是接口就是实例化
								ExcelConvert convert = (ExcelConvert) ((Class<?>) objs[1]).newInstance();
								value = (String) convert.convertToExcel(value);
							}
							label = new Label(j, i, value);
						}
						sheet.addCell(label);
					}catch(Exception e){
						Label label = new Label(j, i, "/");
						sheet.addCell(label);
					}
				}
				i++;
			}
			 book.write();
	         book.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
    
	/**
	 * 
	 * @Title: leadingIn 
	 * @Description: excel导入 
	 * @param clazz
	 * @param in
	 * @return
	 */
    public static List<?> leadingIn(Class<?> clazz,InputStream in) {
        try {
        	List<Object> list = new ArrayList<Object>();
            Cell cell;
            int i=0;
            Map<String,Class<?>> map = new HashMap<String,Class<?>>();
            Workbook book = jxl.Workbook.getWorkbook(in);
            Sheet sheet = book.getSheet(0);
            List<String> fieldNames = new ArrayList<String>();
            Cell[] cells = sheet.getRow(0);
            for(Cell title : cells){
            	fieldNames.add(title.getContents());
            }
            map = ProxyUtil.getFields(clazz);
            for(int row=1;row<sheet.getRows();row++){
            	i=0;
                Object obj = clazz.newInstance();
                for(String field : fieldNames){
                	cell = sheet.getCell(i++, row);
                	ProxyUtil.setter(obj, field, cell.getContents(), map.get(field));
                }
                list.add(obj);
            }
            in.close();
            return list;
        } catch (Exception e) {
        	try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            e.printStackTrace();
        }
        return null;
    }
    
	/**
	 * 
	 * @Title: leadingIn 
	 * @Description: excel导入
	 * @param clazz
	 * @param convert
	 * @param in
	 * @return
	 */
    public static List<?> leadingIn(Class<?> clazz,Map<String,ExcelConvert> convert,InputStream in) {
        try {
        	List<Object> list = new ArrayList<Object>();
            jxl.Cell cell;
            int i=0;
            Map<String,Class<?>> map = new HashMap<String,Class<?>>();
            ExcelConvert cc;
            jxl.Workbook book = jxl.Workbook.getWorkbook(in);
            jxl.Sheet sheet = book.getSheet(0);
            List<String> fieldNames = new ArrayList<String>();
            cell = sheet.getCell(i++, 0);
            while(!"".equals(cell.getContents()));{
            	fieldNames.add(cell.getContents());
            	cell = sheet.getCell(i++, 0);
            }
            map = ProxyUtil.getFields(clazz);
            for(int row=1;row<sheet.getRows();row++){
            	i=0;
                Object obj = clazz.newInstance();
                for(String field : fieldNames){
                	cell = sheet.getCell(i++, row);
                	cc = convert.get(field);
                	if(cc!=null && !cc.getClass().isInterface())
                		ProxyUtil.setter(obj, field, cc.convertToObj(cell.getContents()), map.get(field));
                	else
                		ProxyUtil.setter(obj, field, cell.getContents(), map.get(field));
                }
                list.add(obj);
            }
            in.close();
            return list;
        } catch (Exception e) {
        	try {
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            e.printStackTrace();
        }
        return null;
    }

}
