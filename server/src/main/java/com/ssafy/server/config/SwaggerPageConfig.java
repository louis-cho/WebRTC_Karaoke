package com.ssafy.server.config;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class SwaggerPageConfig {
    @ApiModelProperty(value="페이지번호(0~n)")
    private Integer page;

    @ApiModelProperty(value = "페이지 크기", allowableValues = "range[0,100]")
    private Integer size;
}
