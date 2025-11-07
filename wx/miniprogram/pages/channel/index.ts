// pages/channel/index.ts
import Toast, { hideToast } from 'tdesign-miniprogram/toast/index';
import $api from '@/api/index';

Page({

  /**
   * 页面的初始数据
   */
  data: {
    streamInfo: {},
    deviceId: '',
    channelId: '',
    total: 0,
    channelList: [],
    parentChannelId: '0',
    queryParams: {
      pageNum: 1,
      pageSize: 10,
      searchSrt: '',
      channelType: '',
      online: '',
    },
  },

  play(event : any){
    const item = event.currentTarget.dataset.item;
    const params = {
      deviceId: this.data.deviceId,
      channelId: item.deviceId
    }
    this.setData({ channelId: item.deviceId });
    $api.queryApi.sendDevicePush(params).then((res: any) => {
      wx.navigateTo({
        url: `/pages/videoPlayer/index?url=${encodeURIComponent(res.data.hls)}`
      });
    });
  },

  getChannels(){
    if (typeof (this.data.parentChannelId) == "undefined" || this.data.parentChannelId === '0') {
      this.getDeviceChannelList();
    } else {
      this.showSubchannels();
    }
  },

  /**
   * 显示子通道
   */
  showSubchannels() {},

  /**
   * 获取设备通道列表
   */
  getDeviceChannelList() {
    Toast({
      context: this,
      selector: '#t-toast',
      message: '加载中...',
      theme: 'loading',
      direction: 'column',
      duration: -1,
    });
    $api.queryApi.listDeviceChannel({
      pageNum: this.data.queryParams.pageNum,
      pageSize: this.data.queryParams.pageSize,
      deviceId: this.data.deviceId,
      query: this.data.queryParams.searchSrt,
      online: this.data.queryParams.online,
      channelType: this.data.queryParams.channelType,
    }).then((res:any) => {
      this.setData({
        channelList: res.rows,
        total: res.total,
      })
    }).finally(() => {
      hideToast({
        context: this,
        selector: '#t-toast',
      });
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad(options) {
    if(options.deviceId){
      this.setData({deviceId: options.deviceId})
      this.getChannels();
    } else {
      Toast({
        context: this,
        selector: '#t-toast',
        message: '设备id为空',
        theme: 'error',
        direction: 'column',
      });
    }
    
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