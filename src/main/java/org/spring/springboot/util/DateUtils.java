package org.spring.springboot.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DateUtils {
	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
	/**
	 * 校验日期格式
	 * @param date
	 * @param formate
	 * @return
	 */
	public static Boolean validateDateFormate(String date,String formate){
		boolean flag = true;
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		try {
			sdf.parse(date);
		} catch (ParseException e) {
			flag = false;
			logger.info(e.getMessage());
		}
		return flag;
	}
	/**
	 * 日期字符按指定格式转Date
	 * @param date
	 * @param formate
	 * @return
	 */
	public static Date getDate(String date,String formate){
		SimpleDateFormat sdf = new SimpleDateFormat(formate);
		Date d = null;
		try {
			d = sdf.parse(date);
		} catch (ParseException e) {
			logger.info(e.getMessage());
		}
		return d;
	}
	/**
	 * 获取日期字符串
	 * 
	 * @param params  日期字符串,如果不传或格式错误则默认为当前时间
	 * @return
	 */
    public static String getDateStr(String params){
    	String dateStr = "";
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	Date today = new Date();
    	if(StringUtils.isNotBlank(params)){
    		params = params.replace("-", "").replace("/", "");
			try {
				sdf.parse(params);
				dateStr = params;
			} catch (ParseException e) {
				dateStr = sdf.format(today);
			}
    	}else{
    		dateStr = sdf.format(today);
    	}
    	return dateStr;
    }
    /**
	 * 当前时间与邮件发送时间临界点比较，大于等于true 小于false
	 * @param sendEmailTime
	 * @return
	 */
	public static Boolean compareSendEmailTime(String sendEmailTime){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		Date currentDate = new Date();
		String currentTimeStr = sdf.format(currentDate);
		try {
			Date todayDate = sdf.parse(currentTimeStr);//将字符串转换为date类型 
			Date sendEmailDate = sdf.parse(sendEmailTime);
			if(todayDate.getTime() >= sendEmailDate.getTime()){
				return true;
			}else{
				return false;
			}
		} catch (ParseException e) {
			logger.info("与发送邮件时间的临界值【"+sendEmailTime+"】进行比较,转换时间失败:" +e.getMessage());
			return false;
		}
	}
	public static void main(String[] args) {
		Date d = new Date(1554938100012L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		System.out.println(sdf.format(d));
	}
}
