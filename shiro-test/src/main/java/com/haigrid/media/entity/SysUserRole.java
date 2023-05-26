package com.haigrid.media.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户-角色
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2023-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysUserRole对象", description="用户-角色")
public class SysUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户编号")
    @TableId(value = "user_id", type = IdType.UUID)
    private String userId;

    @ApiModelProperty(value = "角色编号")
    private String roleId;


}
