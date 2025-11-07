package com.ruoyi.wvp.service.bean;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * wvp国标通道标记对象 wvp_mark_channel
 *
 * @author 陈江灿
 * @date 2025-08-22
 */
public class WvpMarkChannel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 国标通道id */
    private Long channelId;

    /** 标记id */
    @Excel(name = "标记id")
    private Long markId;

    /** 通道名称 */
    @Excel(name = "通道名称")
    private String gbName;

    /** 设备id */
    @Excel(name = "设备id")
    private String gbParentid;

    /** 通道id */
    @Excel(name = "通道id")
    private String gbDeviceid;

    public void setChannelId(Long channelId)
    {
        this.channelId = channelId;
    }

    public Long getChannelId()
    {
        return channelId;
    }

    public void setMarkId(Long markId)
    {
        this.markId = markId;
    }

    public Long getMarkId()
    {
        return markId;
    }

    public void setGbName(String gbName)
    {
        this.gbName = gbName;
    }

    public String getGbName()
    {
        return gbName;
    }

    public void setGbParentid(String gbParentid)
    {
        this.gbParentid = gbParentid;
    }

    public String getGbParentid()
    {
        return gbParentid;
    }

    public void setGbDeviceid(String gbDeviceid)
    {
        this.gbDeviceid = gbDeviceid;
    }

    public String getGbDeviceid()
    {
        return gbDeviceid;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("channelId", getChannelId())
            .append("markId", getMarkId())
            .append("gbName", getGbName())
            .append("gbParentid", getGbParentid())
            .append("gbDeviceid", getGbDeviceid())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
