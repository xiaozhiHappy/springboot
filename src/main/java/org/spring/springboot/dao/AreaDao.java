package org.spring.springboot.dao;

import org.apache.ibatis.annotations.Param;
import org.spring.springboot.domain.Area;

import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * 地区Mapper
 * @author lzj
 *
 */
public interface AreaDao extends BaseMapper<Area> {

	/**
	 * 根据地区编码查询地区信息
	 * @param areaCode
	 * @return
	 */
    Area findAreaNameByCode(@Param("areaCode") String areaCode);
}
