import { getToken, removeToken } from "@/utils/auth";

const app = getApp();

export enum HttpMethod {
	GET = 'GET',
	POST = 'POST',
	OPTIONS = 'OPTIONS',
	PUT = 'PUT',
	DELETE = 'DELETE'
}

interface RequestConfig {
	url?: string
	method?: HttpMethod
	data?: any
	needToken?: boolean
	header?: object
	dataType?: string
	noShowMsg?: boolean
}

export interface MyAwesomeData<T> {
	code: number
	msg: string
  data: T
  token: string
}

class HttpRequest {
	private static instance: HttpRequest
  private constructor() { }
  
  public static getInstance(): HttpRequest {
		if (!this.instance) {
			this.instance = new HttpRequest()
		}
		return this.instance
  }
  
  private handerErrorStatus(statusCode: number, requestConfig: RequestConfig) {
		let msg = '服务找不到'
		if (statusCode === 502 || statusCode === 503) {
			msg = '服务器开小差了~'
		}
		!requestConfig.noShowMsg && wx.showToast({
			title: `${msg}，错误码：${statusCode}`,
			icon: 'none'
		})
		return msg
  }
  
  private handerError(err: { errMsg: string }, requestConfig: RequestConfig) {
		let msg = `请求异常`
		if (/timeout/.test(err.errMsg)) {
			msg = '请求超时'
		}
		!requestConfig.noShowMsg && wx.showToast({
			title: msg,
			icon: 'none'
		});
		return msg
  }
  
  public request<T>(requestConfig: RequestConfig): Promise<MyAwesomeData<T>> {
		let _this = this
		return new Promise((resolve, reject) => {
			const contentType = requestConfig.method === 'GET' ? 'application/x-www-form-urlencoded' : 'application/json'
			const header = {
        'content-type': contentType,
        'Authorization': 'Bearer ' + getToken()
			}
			wx.request({
				method: requestConfig.method,
				url: app.globalData.baseUrl + `${requestConfig.url}`,
				data: requestConfig.data,
				header: Object.assign(header, requestConfig?.header),
				dataType: !requestConfig.dataType ? 'json' : '其他',
				success: function (res) {
					const statusCode = res.statusCode || -404
					const data = res.data
					if (statusCode == 200) {
            const code = (data as any).code || 200;
            if(code === 200){
              resolve(data as any)
            } else if((data as any).code === 401) {
              wx.showModal({
                title: '系统提示',
                content: '登录状态已过期，您可以继续留在该页面，或者重新登录',
                confirmText: '重新登录',
                cancelText: '取消',
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    removeToken();
                    wx.reLaunch({
                      url: '/pages/login/index'
                    });
                  }
                }
              });
            }
						resolve(data as any)
					} else {
						const errMsg = _this.handerErrorStatus(statusCode, requestConfig)
						reject({ statusCode, msg: errMsg, data })
					}
				},
				fail: err => {
					let msg = _this.handerError(err, requestConfig)
					reject({ msg })
				}
			})
		})
  }
  
  public get<T>(url: string, data?: Object, OtherConfig?: RequestConfig) {
		return this.request<T>({ method: HttpMethod.GET, url, data, ...OtherConfig })
  }
  
  public post<T>(url: string, data: Object, OtherConfig?: RequestConfig) {
		return this.request<T>({ method: HttpMethod.POST, url, data, ...OtherConfig })
  }
  
  public delete<T>(url: string, data: Object, OtherConfig?: RequestConfig) {
		return this.request<T>({ method: HttpMethod.DELETE, url, data, ...OtherConfig })
  }
  
  public put<T>(url: string, data?: Object, OtherConfig?: RequestConfig) {
		return this.request<T>({ method: HttpMethod.PUT, url, data, ...OtherConfig })
	}

}

export const httpRequest = HttpRequest.getInstance();