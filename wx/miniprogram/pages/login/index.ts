// pages/login/index.ts
import { setToken } from "@/utils/auth";
import $api from '@/api/index';
import { Toast } from 'tdesign-miniprogram';

const app = getApp();

Page({

  /**
   * 页面的初始数据
   */
  data: {
    captchaEnabled: true,
    loadingButton: false,
    codeUrl: '',
    loginForm: {
      username: "admin",
      password: "admin123",
      rememberMe: false,
      code: "",
      uuid: "",
      publicCode: ""
    }
  },

  /**
   * 校验
   * @param e 
   */
  validateForm() {
    const { username, password, code } = this.data.loginForm;
    const validations = [
      { field: username, message: '用户名不能为空' },
      { field: password, message: '密码不能为空' },
      { field: code, message: '验证码不能为空' }
    ];
    for (const { field, message } of validations) {
      if (!field) {
        Toast({
          context: this,
          selector: '#t-toast',
          message: message,
          theme: 'error',
          direction: 'column',
        });
        return false;
      }
    }
    return true;
  },

  /**
   * 登录
   */
  async handleLogin() {
    this.setData({
      loadingButton: true,
    })
    if (this.validateForm()) {
      const res = await $api.loginApi.login(this.data.loginForm);
      if (res.code === 200) {
        setToken(res.token);
        this.setData({ loadingButton: false });
        wx.showToast({
          title: '登录成功',
          icon: 'success',
          duration: 2000
        }).then(() => {
          wx.switchTab({
            url: '/pages/index/index'
          })
        })
      } else {
        wx.showToast({
          title: res.msg,
          icon: 'none',
          duration: 2000
        }).then( async () => {
          await this.getCode();
        })
      }
    }
    this.setData({ loadingButton: false });
  },

  /**
   * 双向绑定
   * @param e 
   */
  handleInputChange(e: any) {
    let value = e.detail.value;
    let fieldName = e.target.dataset.fieldName;
    this.setData({
      [`loginForm.${fieldName}`]: value
    });
  },

  /**
   * 获取验证码
   */
  async getCode() {
    const res = await $api.loginApi.getCodeImg();
    this.setData({
      codeUrl: 'data:image/gif;base64,' + (res as any).img,
      "loginForm.uuid": (res as any).uuid
    })
   },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
    this.getCode();
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