package com.ruoyi.wvp.service;

import com.ruoyi.wvp.service.bean.WvpFavoritesChannel;

import java.util.List;

/**
 * 收藏夹国标通道Service接口
 *
 * @author 陈江灿
 * @date 2025-08-21
 */
public interface IWvpFavoritesChannelService
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
     * 批量删除收藏夹国标通道
     *
     * @param favoritesIds 需要删除的收藏夹国标通道主键集合
     * @return 结果
     */
    public int deleteWvpFavoritesChannelByFavoritesIds(Long[] favoritesIds);

    /**
     * 删除收藏夹国标通道信息
     *
     * @param favoritesId 收藏夹国标通道主键
     * @return 结果
     */
    public int deleteWvpFavoritesChannelByFavoritesId(Long favoritesId);
}
