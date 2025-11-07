package com.ruoyi.wvp.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.wvp.service.bean.WvpFavorites;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.wvp.mapper.WvpFavoritesMapper;
import com.ruoyi.wvp.service.IWvpFavoritesService;

/**
 * 国标通道收藏Service业务层处理
 *
 * @author 陈江灿
 * @date 2025-08-21
 */
@Service
public class WvpFavoritesServiceImpl implements IWvpFavoritesService {
    @Autowired
    private WvpFavoritesMapper wvpFavoritesMapper;

    /**
     * 查询国标通道收藏
     *
     * @param id 国标通道收藏主键
     * @return 国标通道收藏
     */
    @Override
    public WvpFavorites selectWvpFavoritesById(Long id) {
        return wvpFavoritesMapper.selectWvpFavoritesById(id);
    }

    /**
     * 查询国标通道收藏列表
     *
     * @param wvpFavorites 国标通道收藏
     * @return 国标通道收藏
     */
    @Override
    public List<WvpFavorites> selectWvpFavoritesList(WvpFavorites wvpFavorites) {
        return wvpFavoritesMapper.selectWvpFavoritesList(wvpFavorites);
    }

    /**
     * 新增国标通道收藏
     *
     * @param wvpFavorites 国标通道收藏
     * @return 结果
     */
    @Override
    public int insertWvpFavorites(WvpFavorites wvpFavorites) {
        wvpFavorites.setCreateTime(DateUtils.getNowDate());
        return wvpFavoritesMapper.insertWvpFavorites(wvpFavorites);
    }

    /**
     * 修改国标通道收藏
     *
     * @param wvpFavorites 国标通道收藏
     * @return 结果
     */
    @Override
    public int updateWvpFavorites(WvpFavorites wvpFavorites) {
        wvpFavorites.setUpdateTime(DateUtils.getNowDate());
        return wvpFavoritesMapper.updateWvpFavorites(wvpFavorites);
    }

    /**
     * 批量删除国标通道收藏
     *
     * @param ids 需要删除的国标通道收藏主键
     * @return 结果
     */
    @Override
    public int deleteWvpFavoritesByIds(Long[] ids) {
        return wvpFavoritesMapper.deleteWvpFavoritesByIds(ids);
    }

    /**
     * 删除国标通道收藏信息
     *
     * @param id 国标通道收藏主键
     * @return 结果
     */
    @Override
    public int deleteWvpFavoritesById(Long id) {
        return wvpFavoritesMapper.deleteWvpFavoritesById(id);
    }
}
