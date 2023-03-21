package org.spring.springboot.service;

import java.util.List;
import java.util.Map;

import org.spring.springboot.domain.Area;

import com.baomidou.mybatisplus.service.IService;

/**
 * 地区接口
 * @author lzj
 *
 */
public interface AreaService extends IService<Area>{

    /**
     * 根据地区编码查询地区信息
     * @param areaCode
     * @return
     */
    Area findAreaNameByCode(String areaCode);
    /**
     * 查询地区列表
     * @param areaCode
     * @return
     */
    List<Area> findAreas(String areaCode);
    /**
     * 获取地区列表
     * @param map
     * @return
     */
    Map<String,Object> getList(Map<String, Object> map);
}
