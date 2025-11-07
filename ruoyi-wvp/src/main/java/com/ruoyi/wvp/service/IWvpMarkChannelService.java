package com.ruoyi.wvp.service;

import java.util.List;

import com.ruoyi.wvp.service.bean.WvpMarkChannel;

/**
 * wvp国标通道标记Service接口
 *
 * @author 陈江灿
 * @date 2025-08-22
 */
public interface IWvpMarkChannelService {
    /**
     * 查询wvp国标通道标记
     *
     * @param channelId wvp国标通道标记主键
     * @return wvp国标通道标记
     */
    public WvpMarkChannel selectWvpMarkChannelByChannelId(Long channelId);

    /**
     * 查询wvp国标通道标记列表
     *
     * @param wvpMarkChannel wvp国标通道标记
     * @return wvp国标通道标记集合
     */
    public List<WvpMarkChannel> selectWvpMarkChannelList(WvpMarkChannel wvpMarkChannel);

    /**
     * 新增wvp国标通道标记
     *
     * @param wvpMarkChannel wvp国标通道标记
     * @return 结果
     */
    public int insertWvpMarkChannel(WvpMarkChannel wvpMarkChannel);

    /**
     * 修改wvp国标通道标记
     *
     * @param wvpMarkChannel wvp国标通道标记
     * @return 结果
     */
    public int updateWvpMarkChannel(WvpMarkChannel wvpMarkChannel);

    /**
     * 批量删除wvp国标通道标记
     *
     * @param channelIds 需要删除的wvp国标通道标记主键集合
     * @return 结果
     */
    public int deleteWvpMarkChannelByChannelIds(Long[] channelIds);

    /**
     * 删除wvp国标通道标记信息
     *
     * @param channelId wvp国标通道标记主键
     * @return 结果
     */
    public int deleteWvpMarkChannelByChannelId(Long channelId);
}
