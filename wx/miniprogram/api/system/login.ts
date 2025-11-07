import { httpRequest } from "@/utils/request";

export default class loginApi {

  // 获取验证码
  static getCodeImg = () => httpRequest.get<any>('/captchaImage')
  
  // 获取用户详细信息
  static getInfo = () => httpRequest.get<any>('/getInfo')

  // 登录方法
  static login = (data:any) => httpRequest.post<any>('/login', data)

}