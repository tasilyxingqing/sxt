import {GET,POST,FILE,FILEPOST,PUT,GETNOBASE} from "../api";
const indexUrl=  {
    'leftTop':'/large/countDeviceNum',//左上
    'leftCenter':'/large/countGbNum',//左中
    "centerMap":"/large/centerMap",
    "centerBottom":"/large/installationPlan",

    'leftGbDevice':"/large/leftGbDevice", //左下
    'rightTop':"/large/alarmNum", //报警次数
    'rightBottom':'/large/rightBottom',//右下
    'rightCenter':'/large/ranking',// 报警排名
    'serverInfo':'/server/system/info',// 报警排名
}

export default indexUrl

/**左上--设备内总览 */
export const countDeviceNum=(param:any={})=>{
    return GET(indexUrl.leftTop,param)
}

/**左中--国标总览 */
export const countGbNum=(param:any={})=>{
    return GET(indexUrl.leftCenter,param)
}

/**左下--设备提醒 */
export const leftGbDevice=(param:any={})=>{
    return GET(indexUrl.leftGbDevice,param)
}

/**中上--地图 */
export const centerMap=(param:any={})=>{
    return GET(indexUrl.centerMap,param)
}

/**中下--安装计划 */
export const installationPlan=(param:any={})=>{
    return GET(indexUrl.centerBottom,param)
}

/**右上--报警次数 */
export const alarmNum=(param:any={})=>{
    return GET(indexUrl.rightTop,param)
}

/**右中--报警排名 */
export const ranking=(param:any={})=>{
    return GET(indexUrl.rightCenter,param)
}

/**右下--设备状态 */
export const rightBottom=(param:any={})=>{
    return GET(indexUrl.rightBottom,param)
}

/**获取服务信息 */
export const serverInfo=(param:any={})=>{
    return GET(indexUrl.serverInfo,param)
}
