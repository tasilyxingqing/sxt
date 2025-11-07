const TokenKey = 'Admin-Token';

// 获取 Token
export const getToken = () => {
  try {
    return wx.getStorageSync(TokenKey) || null; // 如果缓存中没有值，返回 null
  } catch (error) {
    console.error('获取 Token 失败:', error);
    return null;
  }
};

// 设置 Token
export const setToken = (access_token:string) => {
  try {
    wx.setStorageSync(TokenKey, access_token); // 将 Token 存入缓存
  } catch (error) {
    console.error('设置 Token 失败:', error);
  }
};

// 移除 Token
export const removeToken = () => {
  try {
    wx.removeStorageSync(TokenKey); // 从缓存中移除 Token
  } catch (error) {
    console.error('移除 Token 失败:', error);
  }
};