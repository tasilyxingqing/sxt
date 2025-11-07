// pages/cloudRecord/index.ts
import $api from '@/api/index';

Page({

  /**
   * 页面的初始数据
   */
  data: {
    recordList: [],
    total: 0,
    queryParams: {
      pageNum: 1,
      pageSize: 10,
      startTime: '',
      endTime: '',
      mediaServerId: '',
      app: '',
      stream: '',
      query: '',
    },
    loading: false,
    hasMore: true,
  },

  /**
   * 播放
   * @param event 
   */
  async playRecord(event: any) {
    const item = event.currentTarget.dataset.item;
    const res = await $api.recordApi.play({ recordId: item.id });
    wx.navigateTo({
      url: `/pages/videoPlayer/index?url=${encodeURIComponent(res.data.httpPath)}`
    });
  },

  /**
   * 格式化时间
   */
  formatTimeStamp(time: number): string {
    const date = new Date(time);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  },

  /**
   * 格式化秒数
   * @param time 
   */
  formatTime(time: number | string): string {
    const timeNum = typeof time === 'string' ? Number(time) : time;
    const h = Math.floor(timeNum / 3600 / 1000);
    const minute = Math.floor((timeNum - h * 3600 * 1000) / 60 / 1000);
    let second = Math.ceil((timeNum - h * 3600 * 1000 - minute * 60 * 1000) / 1000);
    if (second < 0) second = 0;
    return (h > 0 ? h + '小时' : '') +
      (minute > 0 ? minute + '分' : '') +
      (second > 0 ? second + '秒' : '');
  },

  /**
   * 获取列表
   */
  async getList() {
    const { pageNum, pageSize } = this.data.queryParams;
    if (this.data.loading) return;
    this.setData({ loading: true });
    try {
      const params = this.filterEmptyParams(this.data.queryParams);
      const res = await $api.recordApi.listProxy(params);
      const list = (res as any).rows || [];
      const total = (res as any).total || 0;
      const formattedList = list.map((item: any) => ({
        ...item,
        startTimeStr: this.formatTimeStamp(item.startTime),
        endTimeStr: this.formatTimeStamp(item.endTime),
        timeLenStr: this.formatTime(item.timeLen),
      }));
      const newRecordList = pageNum === 1 ? formattedList : [...this.data.recordList, ...formattedList];
      this.setData({
        recordList: newRecordList,
        total,
        loading: false,
        hasMore: newRecordList.length < total,
      });
    } catch (error) {
      console.error('获取录像列表失败:', error);
      this.setData({ loading: false });
    }
  },

  filterEmptyParams(obj: Record<string, any>): Record<string, any> {
    const filtered: Record<string, any> = {};

    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        const value = obj[key];
        // 只保留非 null、非 undefined、非空字符串的值
        if (value !== null && value !== undefined && value !== '') {
          filtered[key] = value;
        }
      }
    }

    return filtered;
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    this.getList();
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {

  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh() {
    wx.showNavigationBarLoading();
    wx.showLoading({
      title: '正在刷新...',
      icon: 'loading',
      mask: true
    });
    this.setData({
      'queryParams.pageNum': 1,
      recordList: [],
      hasMore: true
    });
    this.getList().finally(() => {
      wx.hideLoading();
      wx.stopPullDownRefresh();
      wx.hideNavigationBarLoading();
      wx.showToast({
        title: '刷新成功',
        icon: 'success',
        duration: 1500
      });
    });
  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {
    if (!this.data.hasMore || this.data.loading) return;
    wx.showLoading({
      title: '正在加载...',
      mask: true,
      icon: 'loading'
    });
    this.setData({
      'queryParams.pageNum': this.data.queryParams.pageNum + 1
    });
    this.getList().finally(() => {
      wx.hideLoading();
    });
  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})