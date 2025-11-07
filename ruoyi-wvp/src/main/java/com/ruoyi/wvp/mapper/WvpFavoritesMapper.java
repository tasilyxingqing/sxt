package com.ruoyi.wvp.mapper;

import com.ruoyi.wvp.service.bean.WvpFavorites;

import java.util.List;

/**
 * 国标通道收藏Mapper接口
 *
 * @author 陈江灿
 * @date 2025-08-21
 */
public interface WvpFavoritesMapper
{
    /**
     * 查询国标通道收藏
     *
     * @param id 国标通道收藏主键
     * @return 国标通道收藏
     */
    public WvpFavorites selectWvpFavoritesById(Long id);

    /**
     * 查询国标通道收藏列表
     *
     * @param wvpFavorites 国标通道收藏
     * @return 国标通道收藏集合
     */
    public List<WvpFavorites> selectWvpFavoritesList(WvpFavorites wvpFavorites);

    /**
     * 新增国标通道收藏
     *
     * @param wvpFavorites 国标通道收藏
     * @return 结果
     */
    public int insertWvpFavorites(WvpFavorites wvpFavorites);

    /**
     * 修改国标通道收藏
     *
     * @param wvpFavorites 国标通道收藏
     * @return 结果
     */
    public int updateWvpFavorites(WvpFavorites wvpFavorites);

    /**
     * 删除国标通道收藏
     *
     * @param id 国标通道收藏主键
     * @return 结果
     */
    public int deleteWvpFavoritesById(Long id);

    /**
     * 批量删除国标通道收藏
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWvpFavoritesByIds(Long[] ids);
}
