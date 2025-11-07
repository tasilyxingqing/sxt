package com.ruoyi.wvp.mapper;

import java.util.List;
import com.ruoyi.wvp.service.bean.WvpMark;

/**
 * wvp通道标记Mapper接口
 *
 * @author 陈江灿
 * @date 2025-08-22
 */
public interface WvpMarkMapper
{
    /**
     * 查询wvp通道标记
     *
     * @param id wvp通道标记主键
     * @return wvp通道标记
     */
    public WvpMark selectWvpMarkById(Long id);

    /**
     * 查询wvp通道标记列表
     *
     * @param wvpMark wvp通道标记
     * @return wvp通道标记集合
     */
    public List<WvpMark> selectWvpMarkList(WvpMark wvpMark);

    /**
     * 新增wvp通道标记
     *
     * @param wvpMark wvp通道标记
     * @return 结果
     */
    public int insertWvpMark(WvpMark wvpMark);

    /**
     * 修改wvp通道标记
     *
     * @param wvpMark wvp通道标记
     * @return 结果
     */
    public int updateWvpMark(WvpMark wvpMark);

    /**
     * 删除wvp通道标记
     *
     * @param id wvp通道标记主键
     * @return 结果
     */
    public int deleteWvpMarkById(Long id);

    /**
     * 批量删除wvp通道标记
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteWvpMarkByIds(Long[] ids);
}
