package com.ruoyi.wvp.service.impl;

import java.util.List;

import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.wvp.mapper.WvpMarkMapper;
import com.ruoyi.wvp.service.bean.WvpMark;
import com.ruoyi.wvp.service.IWvpMarkService;

/**
 * wvp通道标记Service业务层处理
 *
 * @author 陈江灿
 * @date 2025-08-22
 */
@Service
public class WvpMarkServiceImpl implements IWvpMarkService {
    @Autowired
    private WvpMarkMapper wvpMarkMapper;

    /**
     * 查询wvp通道标记
     *
     * @param id wvp通道标记主键
     * @return wvp通道标记
     */
    @Override
    public WvpMark selectWvpMarkById(Long id) {
        return wvpMarkMapper.selectWvpMarkById(id);
    }

    /**
     * 查询wvp通道标记列表
     *
     * @param wvpMark wvp通道标记
     * @return wvp通道标记
     */
    @Override
    public List<WvpMark> selectWvpMarkList(WvpMark wvpMark) {
        return wvpMarkMapper.selectWvpMarkList(wvpMark);
    }

    /**
     * 新增wvp通道标记
     *
     * @param wvpMark wvp通道标记
     * @return 结果
     */
    @Override
    public int insertWvpMark(WvpMark wvpMark) {
        wvpMark.setCreateTime(DateUtils.getNowDate());
        return wvpMarkMapper.insertWvpMark(wvpMark);
    }

    /**
     * 修改wvp通道标记
     *
     * @param wvpMark wvp通道标记
     * @return 结果
     */
    @Override
    public int updateWvpMark(WvpMark wvpMark) {
        wvpMark.setUpdateTime(DateUtils.getNowDate());
        return wvpMarkMapper.updateWvpMark(wvpMark);
    }

    /**
     * 批量删除wvp通道标记
     *
     * @param ids 需要删除的wvp通道标记主键
     * @return 结果
     */
    @Override
    public int deleteWvpMarkByIds(Long[] ids) {
        return wvpMarkMapper.deleteWvpMarkByIds(ids);
    }

    /**
     * 删除wvp通道标记信息
     *
     * @param id wvp通道标记主键
     * @return 结果
     */
    @Override
    public int deleteWvpMarkById(Long id) {
        return wvpMarkMapper.deleteWvpMarkById(id);
    }
}
