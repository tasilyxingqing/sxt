// pages/streamPush/index.ts
import $api from '@/api/index';

Page({

  /**
   * 页面的初始数据
   */
  data: {
    streamList: [],
    total: 0,
    image: 'https://tdesign.gtimg.com/mobile/demos/empty1.png',
    queryParams: {
      pageNum: 1,
      pageSize: 10,
      query: '',
      mediaServerId: '',
      pushing: '',
    }
  },

  async playStream(event : any) {
    const item = event.currentTarget.dataset.item;
    const res = await $api.pushApi.start({id: item.id});
    wx.navigateTo({
      url: `/pages/videoPlayer/index?url=${encodeURIComponent(res.data.hls)}`
    });
  },

  /**
   * 获取全部设备
   */
  async getList() {
    const res = await $api.pushApi.listPush(this.data.queryParams);
    this.setData({
      streamList: (res as any).rows,
      total:  (res as any).total,
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    this.getList()
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