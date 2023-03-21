package org.spring.springboot.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.spring.springboot.domain.Area;
import org.spring.springboot.domain.UserTest;
import org.spring.springboot.domain.ValidList;
import org.spring.springboot.service.AreaService;
import org.spring.springboot.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.schema.Collections;

/**
 * 
 * @author lzj
 *
 */
@RestController
@RequestMapping("/api")
@Api(tags = "AreaController",value="AreaController")
public class AreaController {
	@Resource
	private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private AreaService areaService;

    @RequestMapping(value = "/findAreaName", method = RequestMethod.GET)
    @ApiOperation(value = "查询地区", notes = "根据参数,查询地区信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R findAreaNameByCode(@RequestParam(value = "areaCode", required = true) String areaCode) {
    	//Area area = areaService.findAreaNameByCode(areaCode);
    	redisTemplate.opsForValue().set("happy", "你们晚上辛苦了");
    	String obj = String.valueOf(redisTemplate.opsForValue().get("happy"));
    	Map<String,String> mm = new HashMap<String,String>();
    	mm.put("name", "xiaoli");
    	mm.put("age", "10");
    	
    	redisTemplate.opsForHash().put("happy7777","name","xiaozhang");  
    	redisTemplate.opsForHash().put("happy7777","age","18");  
    	redisTemplate.opsForHash().put("happy7777","sex","男");  
    	
    	Map<Object,Object> map = redisTemplate.opsForHash().entries("happy7777");    	
    	
    	redisTemplate.opsForValue().set("happy666",JSON.toJSONString(mm),30000,TimeUnit.SECONDS);
    	String mm2 = String.valueOf(redisTemplate.opsForValue().get("happy666"));
    	System.out.println(mm2);
    	System.out.println("获取redis中存入的值================>" + obj);
    	
    	
    	
        return R.ok();
    }
    @RequestMapping(value = "/findAreas", method = RequestMethod.GET)
    @ApiOperation(value = "查询地区列表", notes = "根据参数,查询地区列表信息", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R findAreas(@RequestParam(value = "areaCode", required = true) String areaCode) {
    	List<Area> list = areaService.findAreas(areaCode);
        return R.ok().put("result", list);
    }
    @RequestMapping(value = "/getList", method = RequestMethod.POST)
    @ApiOperation(value = "分页查询地区列表", notes = "根据参数,查询地区列表", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public R getList(@RequestBody Map<String,Object> map) {
    	Map<String,Object> result = areaService.getList(map);
        return R.ok().put("result", result);
    }
    /**
     * 数据校验
     * @param req
     * @param bindingResult
     * @return
     */
    @RequestMapping(value = "/saveVideo", method = RequestMethod.POST)
    @ResponseBody
    public R validTest(@Validated @RequestBody ValidList<UserTest> req, BindingResult bindingResult){
    	List list = bindingResult.getAllErrors();
    	StringBuffer sb = new StringBuffer();
    	if(list != null && list.size() >0){
    		for (Object object : list) {
    			HashMap<String,Object> map = (HashMap<String, Object>) list.get(0);
    			sb.append(String.valueOf(map.get("defaultMessage")));				
			}
    		return R.error().put("msg",sb.toString());
    	}    	
    	return R.ok();
    }

}
