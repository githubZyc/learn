package com.haigrid.media.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author ZhengYanChuang
 * @since 2023-05-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="SysRole对象", description="角色表")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "编号")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "归属机构")
    private String officeId;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "英文名称")
    private String enname;

    @ApiModelProperty(value = "角色类型")
    private String roleType;

    @ApiModelProperty(value = "数据范围")
    private String dataScope;

    @ApiModelProperty(value = "是否系统数据")
    private String isSys;

    @ApiModelProperty(value = "是否可用")
    private String useable;

    @ApiModelProperty(value = "创建者")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createDate;

    @ApiModelProperty(value = "更新者")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateDate;

    @ApiModelProperty(value = "备注信息")
    private String remarks;

    @ApiModelProperty(value = "删除标记")
    private String delFlag;


}
