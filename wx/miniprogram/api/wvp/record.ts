import { httpRequest } from "@/utils/request";

export default class recordApi {
  // 获取列表
  static listProxy = (query: any) => httpRequest.get<any>('/api/cloud/record/list', query);
  // 播放
  static play = (query: any) => httpRequest.get<any>('/api/cloud/record/play/path', query);
}