package org.spring.springboot.domain;

import java.util.List;

import javax.validation.Valid;

import lombok.Data;
@Data
public class SaveVideoReqWrapper{ 
    @Valid
    private List<SaveVideoReq> req;
 
}