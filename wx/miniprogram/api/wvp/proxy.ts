import { httpRequest } from "@/utils/request";

export default class proxyApi {
  // 获取推流列表
  static listProxy = (query: any) => httpRequest.get<any>('/api/proxy/list', query);
  // 播放
  static start = (query: any) => httpRequest.get<any>('/api/proxy/start', query);
}