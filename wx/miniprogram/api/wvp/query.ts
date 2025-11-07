import { httpRequest } from "@/utils/request";

export default class queryApi {
  // 查询全部国标设备
  static deviceList = () => httpRequest.get<any>('/api/device/query/deviceList')
  // 分页查询通道
  static listDeviceChannel = (query: any) => httpRequest.get<any>('/api/device/query/devices/channels', query)
  // 通知设备上传媒体流
  static sendDevicePush = (params: any) => httpRequest.get<any>(`/api/play/start/${params.deviceId}/${params.channelId}`)
}