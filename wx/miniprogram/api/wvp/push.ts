import { httpRequest } from "@/utils/request";

export default class pushApi {
  // 获取推流列表
  static listPush = (query: any) => httpRequest.get<any>('/api/push/list', query);
  // 播放
  static start = (query: any) => httpRequest.get<any>('/api/push/start', query);
}