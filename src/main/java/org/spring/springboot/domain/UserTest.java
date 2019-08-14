package org.spring.springboot.domain;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import lombok.Data;
@Data
public class UserTest {
	@NotBlank(message="用户名不能为空")
	private String name;
    @Valid
    private List<SaveVideoReq> req;
}
