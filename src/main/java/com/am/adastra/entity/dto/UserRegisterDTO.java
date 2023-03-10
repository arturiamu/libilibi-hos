package com.am.adastra.entity.dto;

import com.am.adastra.entity.User;
import com.am.adastra.entity.param.ValidationRules;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @Author : ArturiaMu KMUST-Stu
 * @Date : 2022/5/31 9:50
 * @Params : @param null
 * @Exception :
 * @Return :
 * @Description ：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    @Valid
    @ApiModelProperty(value = "用户", name = "user", example = "user")
    User user;
    @NotNull(groups = ValidationRules.register.class, message = "验证码不能为空")
    @Size(groups = ValidationRules.register.class, message = "验证码格式不正确", max = 6, min = 6)
    @ApiModelProperty(value = "验证码", name = "verCode", example = "123456")
    String verCode;
}
