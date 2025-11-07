// index.ts
import $api from '@/api/index';
import Toast, { hideToast } from 'tdesign-miniprogram/toast/index';

Page({
  data: {
    indexQuery: {
      allCount: 0,
      lineCount: 0,
      offlineCount: 0,
    }
  },

  gotoPage(event: WechatMiniprogram.BaseEvent) {
    const type = event.currentTarget.dataset.type as string;
    if (type === 'streamPush') {
      wx.navigateTo({ url: '/pages/streamPush/index' });
    } else if (type === 'streamProxy') {
      wx.navigateTo({ url: '/pages/streamProxy/index' });
    } else if (type === 'cloudRecord') {
      wx.navigateTo({ url: '/pages/cloudRecord/index' });
    }
  },

  /**
   * 获取全部设备
   */
  async getAll(){
    Toast({
      context: this,
      selector: '#t-toast',
      message: '加载中...',
      theme: 'loading',
      direction: 'column',
      duration: -1,
    });
    const {data, code} = await $api.queryApi.deviceList();
    if(code === 200){
      this.setData({
        "indexQuery.allCount": data.length,
        "indexQuery.lineCount": data.filter((item: { onLine: boolean }) => item.onLine).length,
        "indexQuery.offlineCount": data.filter((item: { onLine: boolean }) => !item.onLine).length,
      })
    }
    hideToast({
      context: this,
      selector: '#t-toast',
    });

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    this.getAll();
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

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom() {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage() {

  }
})
