package org.spring.springboot.domain;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

/**
 * 地区实体类
 * @author lzj
 *
 */
@Data
@TableName("tb_area")
public class Area implements Serializable{
	private static final long serialVersionUID = 8819952571498278612L;
	//地区编码
	private String areaCode;
	//地区名称
	private String areaName;
	//地区等级 
	private int level;
	//父级地区编码
	@TableField
	private String parentCode;
	//经度
	private String lng;
	//维度
	private String lat;

    
}
