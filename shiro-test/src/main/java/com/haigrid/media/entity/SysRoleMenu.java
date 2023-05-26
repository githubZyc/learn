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
 * 角色-菜单
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2023-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysRoleMenu对象", description="角色-菜单")
public class SysRoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色编号")
    @TableId(value = "role_id", type = IdType.UUID)
    private String roleId;

    @ApiModelProperty(value = "菜单编号")
    private String menuId;


}
