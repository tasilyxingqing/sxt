// pages/desk/index.ts
import $api from '@/api/index';

Page({

  /**
   * 页面的初始数据
   */
  data: {
    deviceList: [],
  },

  viewChannels(event : any){
    const item = event.currentTarget.dataset.item;
    wx.navigateTo({
      url: `/pages/channel/index?deviceId=${item.deviceId}`
    });
  },

   /**
   * 获取全部设备
   */
  async getAll(){
    const {data} = await $api.queryApi.deviceList();
    this.setData({
      deviceList: data
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    this.getAll()
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