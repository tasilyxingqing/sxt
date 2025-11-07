package com.ruoyi.wvp.mapper;

import com.ruoyi.wvp.service.bean.WvpFavoritesChannel;

import java.util.List;

/**
 * 收藏夹国标通道Mapper接口
 *
 * @author 陈江灿
 * @date 2025-08-21
 */
public interface WvpFavoritesChannelMapper
{
    /**
     * 查询收藏夹国标通道
     *
     * @param favoritesId 收藏夹国标通道主键
     * @return 收藏夹国标通道
     */
    public WvpFavoritesChannel selectWvpFavoritesChannelByFavoritesId(Long favoritesId);

    /**
     * 查询收藏夹国标通道列表
     *
     * @param wvpFavoritesChannel 收藏夹国标通道
     * @return 收藏夹国标通道集合
     */
    public List<WvpFavoritesChannel> selectWvpFavoritesChannelList(WvpFavoritesChannel wvpFavoritesChannel);

    /**
     * 新增收藏夹国标通道
     *
     * @param wvpFavoritesChannel 收藏夹国标通道
     * @return 结果
     */
    public int insertWvpFavoritesChannel(WvpFavoritesChannel wvpFavoritesChannel);

    /**
     * 修改收藏夹国标通道
     *
     * @param wvpFavoritesChannel 收藏夹国标通道
     * @return 结果
     */
    public int updateWvpFavoritesChannel(WvpFavoritesChannel wvpFavoritesChannel);

    /**
     * 删除收藏夹国标通道
     *
     * @param favoritesId 收藏夹国标通道主键
     * @return 结果
     */
    public int deleteWvpFavoritesChannelByFavoritesId(Long favoritesId);

    /**
     * 批量删除收藏夹国标通道
     *
     * @param favoritesIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWvpFavoritesChannelByFavoritesIds(Long[] favoritesIds);

    /**
     * 根据通道id查询
     * @param channelId
     * @return
     */
    WvpFavoritesChannel selectWvpFavoritesChannelBychannelId(Long channelId);
}
