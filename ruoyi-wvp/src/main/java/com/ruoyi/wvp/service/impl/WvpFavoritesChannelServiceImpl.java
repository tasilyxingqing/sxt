package com.ruoyi.wvp.service.impl;

import java.util.List;

import com.ruoyi.wvp.service.bean.WvpFavoritesChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.wvp.mapper.WvpFavoritesChannelMapper;
import com.ruoyi.wvp.service.IWvpFavoritesChannelService;

/**
 * 收藏夹国标通道Service业务层处理
 *
 * @author 陈江灿
 * @date 2025-08-21
 */
@Service
public class WvpFavoritesChannelServiceImpl implements IWvpFavoritesChannelService {
    @Autowired
    private WvpFavoritesChannelMapper wvpFavoritesChannelMapper;

    /**
     * 查询收藏夹国标通道
     *
     * @param favoritesId 收藏夹国标通道主键
     * @return 收藏夹国标通道
     */
    @Override
    public WvpFavoritesChannel selectWvpFavoritesChannelByFavoritesId(Long favoritesId) {
        return wvpFavoritesChannelMapper.selectWvpFavoritesChannelByFavoritesId(favoritesId);
    }

    /**
     * 查询收藏夹国标通道列表
     *
     * @param wvpFavoritesChannel 收藏夹国标通道
     * @return 收藏夹国标通道
     */
    @Override
    public List<WvpFavoritesChannel> selectWvpFavoritesChannelList(WvpFavoritesChannel wvpFavoritesChannel) {
        return wvpFavoritesChannelMapper.selectWvpFavoritesChannelList(wvpFavoritesChannel);
    }

    /**
     * 新增收藏夹国标通道
     *
     * @param wvpFavoritesChannel 收藏夹国标通道
     * @return 结果
     */
    @Override
    public int insertWvpFavoritesChannel(WvpFavoritesChannel wvpFavoritesChannel) {
        WvpFavoritesChannel w = wvpFavoritesChannelMapper.selectWvpFavoritesChannelBychannelId(wvpFavoritesChannel.getChannelId());
        int row = 0;
        if(w != null){
            row = wvpFavoritesChannelMapper.updateWvpFavoritesChannel(wvpFavoritesChannel);
        } else {
            row = wvpFavoritesChannelMapper.insertWvpFavoritesChannel(wvpFavoritesChannel);
        }
        return row;
    }

    /**
     * 修改收藏夹国标通道
     *
     * @param wvpFavoritesChannel 收藏夹国标通道
     * @return 结果
     */
    @Override
    public int updateWvpFavoritesChannel(WvpFavoritesChannel wvpFavoritesChannel) {
        return wvpFavoritesChannelMapper.updateWvpFavoritesChannel(wvpFavoritesChannel);
    }

    /**
     * 批量删除收藏夹国标通道
     *
     * @param favoritesIds 需要删除的收藏夹国标通道主键
     * @return 结果
     */
    @Override
    public int deleteWvpFavoritesChannelByFavoritesIds(Long[] favoritesIds) {
        return wvpFavoritesChannelMapper.deleteWvpFavoritesChannelByFavoritesIds(favoritesIds);
    }

    /**
     * 删除收藏夹国标通道信息
     *
     * @param favoritesId 收藏夹国标通道主键
     * @return 结果
     */
    @Override
    public int deleteWvpFavoritesChannelByFavoritesId(Long favoritesId) {
        return wvpFavoritesChannelMapper.deleteWvpFavoritesChannelByFavoritesId(favoritesId);
    }
}
