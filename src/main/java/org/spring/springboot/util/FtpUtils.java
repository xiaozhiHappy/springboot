package org.spring.springboot.util;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author lzj
 *
 */
public class FtpUtils {
	private static final Logger logger = LoggerFactory.getLogger(FtpUtils.class);
	/**
	 * 获取FTP服务器连接信息
	 * @param ftpHost    FTP服务器
	 * @param ftpPort    FTP服务器端口号
	 * @param ftpUserName   FTP用户名
	 * @param ftpPassword   FTP用户密码
	 * @return              true 连接成功 false 失败
	 * @throws Exception
	 */
	public static FTPClient getFtpConnect(String ftpHost, int ftpPort, String ftpUserName, String ftpPassword) throws Exception {
		FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpHost, ftpPort);
            ftpClient.login(ftpUserName, ftpPassword);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                closeConnect(ftpClient);
                logger.info("FTP服务器连接失败：ftp://{}:{}@{}:{}", ftpUserName,ftpPassword,ftpHost,ftpPort);
                throw new Exception("FTP服务器连接失败,服务器连接信息：ftp://" + ftpUserName + ":" + ftpPassword +"@"+ftpHost +":"+ftpPort);
            }else{
            	logger.info("FTP服务器连接成功!");
            }
        } catch (Exception e) {
            logger.info("FTP登录失败", e.getMessage());
            throw new Exception("FTP登录失败:" + e.getMessage());
        }
        return ftpClient;
	}
	/**
	 * 关闭FTP连接
	 * @param ftpClient
	 */
    public static void closeConnect(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
            } catch (IOException e) {
                logger.info("关闭FTP连接失败", e.getMessage());
            }
        }
    }
   /**
    * 查看文件是否存在
    * @param ftpClient         FTPClient
    * @param uploadFilePath    FTP文件上传路径 
    * @param fileName          文件名称
    * @return
    */
    public static Boolean isExist(FTPClient ftpClient,String uploadFilePath,String fileName ){
    	try {    		
    		ftpClient.changeWorkingDirectory(uploadFilePath);
            FTPFile[] files = ftpClient.listFiles(fileName);
            if(files!=null&&files.length>0){
                logger.info("文件存在,文件的大小为："+ files[0].getSize());
                return true;
            }else {
            	logger.info("文件【"+fileName+"】不存在,请上传文件!");
                return false;
            }
        } catch (Exception e) {
        	logger.info("FTP服务器文件目录【"+uploadFilePath+"】不存在!");
        	return false;
        }
    }

}
