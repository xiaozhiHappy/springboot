package org.spring.springboot.domain;

import javax.validation.constraints.NotBlank;

import lombok.Data;
@Data
public class SaveVideoReq{
 
    private static final long serialVersionUID = 1L;
 
    @NotBlank(message = "系统编号不能为空")
    private String sysNo;
 
    @NotBlank(message = "影像类型不能为空")
    private String videoType;
 
    @NotBlank(message = "影像名称不能为空")
    private String videoName;
}