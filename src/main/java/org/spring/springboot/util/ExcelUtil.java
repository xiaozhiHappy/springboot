package org.spring.springboot.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author lzj
 *
 */
public class ExcelUtil {
	private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

	 
	private static final String EXCEL_XLS = ".xls";  
    private static final String EXCEL_XLSX = ".xlsx";
    /**
     * 获取数据完成性
     * @param filePath
     * @throws Exception
     */
    public static Boolean vaildFileIntegrity(FTPClient ftpClient,String filePath,String fileName,String suffix ){
		FormulaEvaluator formulaEvaluator = null;
    	Boolean flag = true;
    	StringBuffer errorMsg = new StringBuffer();
    	InputStream inputStream = null;
    	try{
    		ftpClient.setControlEncoding("UTF-8");
    		FTPFile[] ftpFiles = ftpClient.listFiles(fileName);
    		for (FTPFile fPath:ftpFiles) {    			
    			inputStream = ftpClient.retrieveFileStream(fPath.getName());
    			/*
	    		       在使用public InputStream retrieveFileStream(String remote) 
	    		       需要特别注意：调用这个接口后，一定要手动close掉返回的InputStream，
	    		       然后再调用completePendingCommand方法，若不是按照这个顺序，则会导致后面对FTPClient的操作都失败
    			*/
    			Workbook workbook = getWorkbok(inputStream,suffix);
        		formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
        		int sheetCount = workbook.getNumberOfSheets(); 		
        		for (int i = 0; i < sheetCount; i++) {
        			Sheet sheet = workbook.getSheetAt(i);
        			int totalRowCount = sheet.getLastRowNum();
        			int count = 0;
        			for (Row row : sheet) {
        				if(count > 0){
        					break;
        				}
        				count++;					
        				Cell cell = row.getCell(0);
        				String firstRowData = String.valueOf(getCellValue(cell,formulaEvaluator));
        				if(StringUtils.isNotBlank(firstRowData)){
        					String[] totalRows = firstRowData.split("：");
        					if(totalRows.length == 2){
        						int totals = Integer.valueOf(totalRows[1]) + 1;
        						if(totals != totalRowCount){
        							errorMsg.append("sheet【"+i+"】页总行数有误,请补充数据;");
        							flag = false;
        						}
        					}else{
        						errorMsg.append("sheet【"+i+"】页第一行代表数据完整性的描述信息有误,请检查;");
        						flag = false;
        					}
        				}
        			}
        		}
    		}
    	}catch(Exception e){
    		flag = false;
    		errorMsg.append(e.getMessage());
    		logger.info(errorMsg.toString());
    	}finally {
    		try {
				inputStream.close();
				ftpClient.completePendingCommand();    	
			} catch (IOException e) {
				flag = false;
	    		errorMsg.append(e.getMessage());
	    		logger.info(errorMsg.toString());
			}
		}
		return flag;
	}
    
    /**
     * 检查文件各sheet的名称
     * @param ftpClient  				FTP连接信息
     * @param downLoadPath              文件移动路径             
     * @param downLoadFileName          文件移动后的名字
     * @param suffix                    文件后缀
     * @param sheetCount                sheet页的个数
     * @param sheetNames                各sheet页的名称
     * @return                          
     * @throws Exception
     */
	public static String checkSheetName(FTPClient ftpClient,String downLoadPath,String downLoadFileName, String suffix, int sheetCount, String[] sheetNameArr) throws Exception {
		StringBuffer sb = new StringBuffer();
		InputStream inputStream = null;
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.changeWorkingDirectory(downLoadPath);
		try{
			FTPFile[] ftpFiles = ftpClient.listFiles(downLoadFileName);
			for (FTPFile ftpFile : ftpFiles) {
				/*
					 在使用public InputStream retrieveFileStream(String remote) 
					需要特别注意：调用这个接口后，一定要手动close掉返回的InputStream，
					然后再调用completePendingCommand方法，若不是按照这个顺序，则会导致后面对FTPClient的操作都失败
				 */
				inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
				Workbook workbook = getWorkbok(inputStream,suffix);			
				workbook.getCreationHelper().createFormulaEvaluator();
				int sheetNumber = workbook.getNumberOfSheets();
				if(sheetNumber < sheetCount){
					sb.append("sheet页丢失,sheet页依次应为【" + StringUtils.join(sheetNameArr, ",") + "】<br/>");
					break;
				}else{
					for (int i = 0; i < sheetCount; i++) {
						Sheet sheet = workbook.getSheetAt(i);					
						String sheetName = sheet.getSheetName();
						String designSheetName = sheetNameArr[i];
						logger.info("sheet页名字------->" + sheetName);
						if(!designSheetName.equals(sheetName)){
							sb.append("第【" + (i+1) + "】sheet页文件名有误，应为【" + designSheetName + "】<br/>");
						}
					}
				}
				
			}	
		}catch(Exception e){
			logger.info(e.getMessage());
			sb.append("校验sheet名称,操作异常：" +e.getMessage() + "<br/>");
		}finally {
			inputStream.close();
			ftpClient.completePendingCommand();    
		}
		return sb.toString();
	}
	
	/**
	 * 校验sheet页列名
	 * @param ftpClient                   FTP 连接信息     
	 * @param downLoadPath                文件移动路径
	 * @param downLoadFileName            文件移动后的名字
	 * @param suffix                      文件后缀
	 * @param sheetCount                  sheet个数
	 * @param tscnArr                     触点基本信息列名数组
	 * @param tsrcnArr                    触点指标值列名数组
	 * @param tsdrcnArr                   网器指标值列名数组
	 * @param tsmcnArr                    区域负责人列名数组
	 * @return
	 * @throws Exception
	 */
	public static String checkSheetColumnName(FTPClient ftpClient, String downLoadPath,	String downLoadFileName, String suffix, int sheetCount, List<String[]> listSheetColumnNames ) throws Exception {
		StringBuffer sb = new StringBuffer();
		FormulaEvaluator formulaEvaluator = null;
		InputStream inputStream = null;
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.changeWorkingDirectory(downLoadPath);
		try{
			FTPFile[] ftpFiles = ftpClient.listFiles(downLoadFileName);
			for (FTPFile ftpFile : ftpFiles) {
				/*
					 在使用public InputStream retrieveFileStream(String remote) 
					需要特别注意：调用这个接口后，一定要手动close掉返回的InputStream，
					然后再调用completePendingCommand方法，若不是按照这个顺序，则会导致后面对FTPClient的操作都失败
				 */
				inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
				Workbook workbook = getWorkbok(inputStream,suffix);			
				formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
				for (int i = 0; i < sheetCount; i++) {
					Sheet sheet = workbook.getSheetAt(i);
					String sheetName = sheet.getSheetName();
					String[] sheetColumnNames = listSheetColumnNames.get(i);
					Row row = sheet.getRow(1);
					int columnCount = row.getPhysicalNumberOfCells();
					if(columnCount < sheetColumnNames.length){
						sb.append("sheet页【" +sheetName+"】列数丢失,列数应为【" +sheetColumnNames.length+"】列,依次应为【" + StringUtils.join(sheetColumnNames,",")+"】<br/>" );
						continue;
					}
					for (int k = 0; k < sheetColumnNames.length; k++) {
						Cell cell = row.getCell(k);
						String columnName = getCellValue(cell,formulaEvaluator);
						columnName = ExcelUtil.replaceBlank(columnName);
						String designColumnName = sheetColumnNames[k];
						if(!columnName.equals(designColumnName)){
							sb.append("sheet页【" +sheetName+"】,第【"+(k+1)+"】列,列名有误,应为【" +designColumnName+ "】<br/>");
						}
					}
				}
			}	
		}catch(Exception e){
			logger.info(e.getMessage());
			sb.append("校验sheet页列名,操作异常：" + e.getMessage() + "<br/>");
		}finally {
			inputStream.close();
			ftpClient.completePendingCommand();    
		}
		return sb.toString();
	}
    
    /**
     * 读取文件内容
     * @param ftpClient             FTP连接信息         
     * @param downLoadPath          文件移动路径
     * @param downLoadFileName      文件移动后的名字
     * @param suffix                文件后缀
     * @param sheetCount            sheet页个数
     * @param tscnArr               触点基本信息列名数组
     * @param tsrcnArr              触点指标得值列名数组
     * @param tsdrcnArr             网器指标值列名数组
     * @param tmcnArr               区域负责人列名数组
     * @return
     * @throws Exception
     */
	public static List<List<List<String>>> getFileData(FTPClient ftpClient, String downLoadPath,
			String downLoadFileName, String suffix, int sheetCount, List<String[]> listSheetColumnNames, StringBuffer sb) throws Exception {
		FormulaEvaluator formulaEvaluator = null;
		InputStream inputStream = null;
		List<List<List<String>>> result = new ArrayList<List<List<String>>>();
		ftpClient.setControlEncoding("UTF-8");
		ftpClient.changeWorkingDirectory(downLoadPath);
		try{
			FTPFile[] ftpFiles = ftpClient.listFiles(downLoadFileName);
			for (FTPFile ftpFile : ftpFiles) {
				/*
					 在使用public InputStream retrieveFileStream(String remote) 
					需要特别注意：调用这个接口后，一定要手动close掉返回的InputStream，
					然后再调用completePendingCommand方法，若不是按照这个顺序，则会导致后面对FTPClient的操作都失败
				 */
				inputStream = ftpClient.retrieveFileStream(ftpFile.getName());
				Workbook workbook = getWorkbok(inputStream,suffix);			
				formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
				for (int i = 0; i < sheetCount; i++) {
					Sheet sheet = workbook.getSheetAt(i);
					int totalRowCount = sheet.getLastRowNum();
					int rows = totalRowCount + 1;
					List<List<String>> sheetList = new ArrayList<List<String>>();
					for(int j=0;j < rows;j++){
						Row row = sheet.getRow(j);
						if(row == null || j == 0){
							continue;
						}
						//sheet个数,如果多于4个,也只处理4个sheet
						//int lastCellNum = sheet.getRow(1).getLastCellNum();
						String[] sheetColumnNames = listSheetColumnNames.get(i);
						List<String> rowList = new ArrayList<String>();
						for (int k = 0; k < sheetColumnNames.length; k++) {
							Cell cell = row.getCell(k);
							String str = getCellValue(cell,formulaEvaluator);
							rowList.add(str);
						}
						sheetList.add(rowList);
					}
					result.add(sheetList);
				}
			}	
		}catch(Exception e){
			logger.info(e.getMessage());
			sb.append("读取文件内容异常：" + e.getMessage());
		}finally {
			inputStream.close();
			ftpClient.completePendingCommand();    
		}		
		return result;
	}
    /**
     * 判断Excel的版本,获取Workbook
     * @param in
     * @param file
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbok(InputStream in,String suffix) throws IOException{  
        Workbook wb = null;  
        if(EXCEL_XLS.equals(suffix)){  
            wb = new HSSFWorkbook(in);  
        }else if(EXCEL_XLSX.equals(suffix)){ 
            wb = new XSSFWorkbook(in);  
        }  
        return wb;  
    }  
    
    /**
     * 判断Excel的版本,获取Workbook
     * @param in
     * @param file
     * @return
     * @throws IOException
     */
    public static Workbook getWorkbok(InputStream in,File file) throws IOException{  
        Workbook wb = null;  
        if(file.getName().endsWith(EXCEL_XLS)){
            wb = new HSSFWorkbook(in);  
        }else if(file.getName().endsWith(EXCEL_XLSX)){
            wb = new XSSFWorkbook(in);  
        }  
        return wb;  
    }  
  
    /**
     * 判断文件是否是excel 
     * @param file
     * @throws Exception
     */
    public static void checkExcelVaild(File file) throws Exception{  
        if(!file.exists()){  
            throw new Exception("文件不存在");           
        }  
        if(!(file.isFile() && (file.getName().endsWith(EXCEL_XLS) || file.getName().endsWith(EXCEL_XLSX)))){  
            throw new Exception("文件不是Excel");  
        }  
    }
    /**
     * 单元格数据/类型
     * @param cell
     * @return
     */
    private static String  getCellValue(Cell cell,FormulaEvaluator formulaEvaluator) {
    	String str = "";
    	try{
    		switch (cell.getCellType()) {
		        case BOOLEAN:
		        	str = String.valueOf(cell.getBooleanCellValue()); 
		            break;
		        case NUMERIC:
		        	short format = cell.getCellStyle().getDataFormat();
					if(format == 14 || format == 31 || format == 57 || format == 58 || format == 22 ){
						 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
						 double value = cell.getNumericCellValue();  
						 Date date = DateUtil.getJavaDate(value); 
						 str=sdf.format(date);
					}else{
						cell.setCellType(CellType.STRING);
						str = String.valueOf(cell.toString());
					}
		            break;
		        case STRING:
		        	str = cell.getStringCellValue(); 
		            break;
		        case FORMULA:
		        	str =  String.valueOf(formulaEvaluator.evaluate(cell).getNumberValue());
		        	break;
		        default:
		            break;
	    	}
    	}catch(Exception e){
    		e.getMessage();
    		logger.info("单元格类型转换错误---->" + cell.getCellType());
    	}
    	
    	return str.trim();
    }
    /**
     * 去除字符串  换行/空格/制表符回车
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
		String dest = "";
		if (str!=null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			java.util.regex.Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
}