import { httpRequest } from "@/utils/request";

export default class userApi {
  
  // 获取个人信息
  static getInfo = () => httpRequest.get<any>('/system/user/profile')
}