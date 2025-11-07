package com.ruoyi.wvp.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.wvp.mapper.WvpMarkChannelMapper;
import com.ruoyi.wvp.service.bean.WvpMarkChannel;
import com.ruoyi.wvp.service.IWvpMarkChannelService;

/**
 * wvp国标通道标记Service业务层处理
 *
 * @author 陈江灿
 * @date 2025-08-22
 */
@Service
public class WvpMarkChannelServiceImpl implements IWvpMarkChannelService {
    @Autowired
    private WvpMarkChannelMapper wvpMarkChannelMapper;

    /**
     * 查询wvp国标通道标记
     *
     * @param channelId wvp国标通道标记主键
     * @return wvp国标通道标记
     */
    @Override
    public WvpMarkChannel selectWvpMarkChannelByChannelId(Long channelId) {
        return wvpMarkChannelMapper.selectWvpMarkChannelByChannelId(channelId);
    }

    /**
     * 查询wvp国标通道标记列表
     *
     * @param wvpMarkChannel wvp国标通道标记
     * @return wvp国标通道标记
     */
    @Override
    public List<WvpMarkChannel> selectWvpMarkChannelList(WvpMarkChannel wvpMarkChannel) {
        return wvpMarkChannelMapper.selectWvpMarkChannelList(wvpMarkChannel);
    }

    /**
     * 新增wvp国标通道标记
     *
     * @param wvpMarkChannel wvp国标通道标记
     * @return 结果
     */
    @Override
    public int insertWvpMarkChannel(WvpMarkChannel wvpMarkChannel) {
        WvpMarkChannel w = wvpMarkChannelMapper.selectWvpMarkChannelByChannelId(wvpMarkChannel.getChannelId());
        wvpMarkChannel.setCreateTime(DateUtils.getNowDate());
        int row = 0;
        if(w != null){
            row = wvpMarkChannelMapper.updateWvpMarkChannel(wvpMarkChannel);
        } else {
            row = wvpMarkChannelMapper.insertWvpMarkChannel(wvpMarkChannel);
        }
        return row;
    }

    /**
     * 修改wvp国标通道标记
     *
     * @param wvpMarkChannel wvp国标通道标记
     * @return 结果
     */
    @Override
    public int updateWvpMarkChannel(WvpMarkChannel wvpMarkChannel) {
        wvpMarkChannel.setUpdateTime(DateUtils.getNowDate());
        return wvpMarkChannelMapper.updateWvpMarkChannel(wvpMarkChannel);
    }

    /**
     * 批量删除wvp国标通道标记
     *
     * @param channelIds 需要删除的wvp国标通道标记主键
     * @return 结果
     */
    @Override
    public int deleteWvpMarkChannelByChannelIds(Long[] channelIds) {
        return wvpMarkChannelMapper.deleteWvpMarkChannelByChannelIds(channelIds);
    }

    /**
     * 删除wvp国标通道标记信息
     *
     * @param channelId wvp国标通道标记主键
     * @return 结果
     */
    @Override
    public int deleteWvpMarkChannelByChannelId(Long channelId) {
        return wvpMarkChannelMapper.deleteWvpMarkChannelByChannelId(channelId);
    }

}
