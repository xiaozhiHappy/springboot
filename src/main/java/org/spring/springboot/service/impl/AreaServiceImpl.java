package org.spring.springboot.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.spring.springboot.dao.AreaDao;
import org.spring.springboot.domain.Area;
import org.spring.springboot.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import cn.hutool.core.date.DateUtil;


/**
 * 
 * @author lzj
 *
 */
@Service("areaService")
//@Cacheable
public class AreaServiceImpl extends ServiceImpl<AreaDao, Area> implements AreaService{

    @Autowired
    private AreaDao areaDao;
    @Override
    //@Cacheable
    public Area findAreaNameByCode(String cityName) {
        return areaDao.findAreaNameByCode(cityName);
    }
    @Override
    //@Cacheable
	public List<Area> findAreas(String areaCode) {
		EntityWrapper<Area> ew = new EntityWrapper<Area>();
		ew.eq("area_code", areaCode);		
		return baseMapper.selectList(ew);
	}

	@Override
	//@Cacheable
	public Map<String,Object> getList(Map<String, Object> map) {
		EntityWrapper<Area> ew = new EntityWrapper<Area>();
		ew.eq("parent_code", String.valueOf(map.get("parentCode")));
		String currentPageStr = String.valueOf(map.get("currentPage"));
		String pageSizeStr = String.valueOf(map.get("pageSize"));
		String orderByField = String.valueOf(map.get("orderByField"));
		int currentPage = 1;
		int pageSize = 10;
		Page<Area> page = null;
		if(StringUtils.isNotBlank(currentPageStr)){
			currentPage = Integer.valueOf(currentPageStr);
		}
		if(StringUtils.isNotBlank(pageSizeStr)){
			pageSize = Integer.valueOf(pageSizeStr);
		}
		if(StringUtils.isNotBlank(orderByField)){
			page = new Page<Area>(currentPage,pageSize, orderByField);
		}else{
			page = new Page<Area>(currentPage,pageSize);
		}		
		List<Area> list = baseMapper.selectPage(page, ew);
		int totalCount = baseMapper.selectCount(ew);
		Map<String,Object> pageMap = new HashMap<String,Object>(10);
		pageMap.put("total", totalCount);
		pageMap.put("currentPage", map.get("currentPage"));
		pageMap.put("pageSize", map.get("pageSize"));
		pageMap.put("datas", list);
		
		return pageMap;
	}

}
