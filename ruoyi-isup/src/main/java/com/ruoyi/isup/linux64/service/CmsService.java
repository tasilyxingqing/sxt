package com.ruoyi.isup.linux64.service;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.isup.config.IsupConfig;
import com.ruoyi.isup.config.IsupLinuxConfig;
import com.ruoyi.isup.domain.IsupDevice;
import com.ruoyi.isup.linux64.callBack.FRegisterCallBack;
import com.ruoyi.isup.linux64.interfaces.HcisupCms;
import com.ruoyi.isup.linux64.utils.HkUtils;
import com.ruoyi.isup.service.IIsupDeviceService;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 注册服务
 **/
@Service("linux64-cms")
@Slf4j
public class CmsService {
    public static HcisupCms hcisupCms = null;
    HcisupCms.NET_EHOME_CMS_LISTEN_PARAM struCMSListenPara = new HcisupCms.NET_EHOME_CMS_LISTEN_PARAM();

    public static List<Integer> lLoginIDList = new ArrayList<>();
    public static Map<Integer, String> deviceInfoMap = new HashMap<>();

    @Autowired
    FRegisterCallBack fRegisterCallBack;

    public static int cmsHandle = -1;

    @Autowired
    IsupLinuxConfig isupLinuxConfig;

    @Autowired
    private IIsupDeviceService isupDeviceService;

    /**
     * 根据不同操作系统加载不同的操作库 目前只实现了 linux64
     */
    private static boolean createInstance() {
        if (hcisupCms == null) {
            synchronized (HcisupCms.class) {
                try {
                    hcisupCms = (HcisupCms) Native.loadLibrary(HkUtils.getCmsPath(), HcisupCms.class);
                } catch (Exception ex) {
                    log.info("cms加载不同的操作库 Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * cms服务初始化
     */
    public void init() {
        if (hcisupCms == null) {
            if (!createInstance()) {
                log.info("初始化cms服务失败！");
                return;
            }
        }
        // 加载 crypto
        HcisupCms.BYTE_ARRAY ptrByteArrayCrypto = new HcisupCms.BYTE_ARRAY(256);
        String strPathCrypto = HkUtils.getCryptoPath();
        System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, strPathCrypto.length());
        ptrByteArrayCrypto.write();
        hcisupCms.NET_ECMS_SetSDKInitCfg(0, ptrByteArrayCrypto.getPointer());

        //加载 ssl
        HcisupCms.BYTE_ARRAY ptrByteArraySsl = new HcisupCms.BYTE_ARRAY(256);
        String strPathSsl = HkUtils.getSslPath();
        System.arraycopy(strPathSsl.getBytes(), 0, ptrByteArraySsl.byValue, 0, strPathSsl.length());
        ptrByteArraySsl.write();
        hcisupCms.NET_ECMS_SetSDKInitCfg(1, ptrByteArraySsl.getPointer());
        //注册服务初始化
        boolean bRet = hcisupCms.NET_ECMS_Init();
        if (!bRet) {
            log.info("cms 注册服务初始化失败!");
        }
        //设置HCAapSDKCom组件库文件夹所在路径
        HcisupCms.BYTE_ARRAY ptrByteArrayCom = new HcisupCms.BYTE_ARRAY(256);
        String strPathCom = HkUtils.getHCAapSDKComPath();
        System.arraycopy(strPathCom.getBytes(), 0, ptrByteArrayCom.byValue, 0, strPathCom.length());
        ptrByteArrayCom.write();
        hcisupCms.NET_ECMS_SetSDKLocalCfg(5, ptrByteArrayCom.getPointer());
        // 设置日志写入路径
        hcisupCms.NET_ECMS_SetLogToFile(3, isupLinuxConfig.getCmsLogPath(), false);
    }


    /**
     * cms 监听
     */
    public void startCmsListen() {
        System.arraycopy(isupLinuxConfig.getCmsServerIP().getBytes(), 0, struCMSListenPara.struAddress.szIP, 0, isupLinuxConfig.getCmsServerIP().length());
        struCMSListenPara.struAddress.wPort = isupLinuxConfig.getCmsServerPort();
        struCMSListenPara.fnCB = fRegisterCallBack;
        struCMSListenPara.write();
        //启动监听，接收设备注册信息
        cmsHandle = hcisupCms.NET_ECMS_StartListen(struCMSListenPara);
        if (cmsHandle < 0) {
            log.info(" cms 监听失败，错误代码：" + hcisupCms.NET_ECMS_GetLastError());
            hcisupCms.NET_ECMS_Fini();
            return;
        }
        String CmsListenInfo = new String(struCMSListenPara.struAddress.szIP).trim() + "_" + struCMSListenPara.struAddress.wPort;
        log.info(" 注册服务器:" + CmsListenInfo + ",CMS 监听 成功! cmsHandle:" + cmsHandle);

        //设置CMS异步回调

    }



    //协议透传 例子：
    //参数规则 配置
    public void putThermometryBasicParam(Integer lLoginID) {
        String reqUrl = "PUT /ISAPI/Thermal/channels/3/thermometry/basicParam";

        String reqContent = ""; // 预留


        HcisupCms.NET_EHOME_PTXML_PARAM m_struParam = new HcisupCms.NET_EHOME_PTXML_PARAM();
        m_struParam.read();
        String urlInBuffer = reqUrl;
        HcisupCms.BYTE_ARRAY ptrurlInBuffer = new HcisupCms.BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam.dwRequestUrlLen = urlInBuffer.length();


        byte[] byInbuffer = new byte[0];
        try {
            byInbuffer = reqContent.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            log.warn("isapiPassThrough错误, " + e.getMessage());
            throw new RuntimeException(e);
        }
        int iInBufLen = byInbuffer.length;
        HcisupCms.BYTE_ARRAY ptrInBuffer = new HcisupCms.BYTE_ARRAY(iInBufLen);
        ptrInBuffer.read();
        System.arraycopy(byInbuffer, 0, ptrInBuffer.byValue, 0, iInBufLen);
        ptrInBuffer.write();
        m_struParam.dwInSize = iInBufLen;
        m_struParam.pInBuffer = ptrInBuffer.getPointer();


        // 输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize2 = 2 * 1024 * 1024;
        HcisupCms.BYTE_ARRAY ptrOutByte2 = new HcisupCms.BYTE_ARRAY(iOutSize2);
        m_struParam.pOutBuffer = ptrOutByte2.getPointer();
        m_struParam.dwOutSize = iOutSize2;
        m_struParam.dwRecvTimeOut = 5000; // 接收超时时间，单位毫秒
        m_struParam.write();
        if (!hcisupCms.NET_ECMS_ISAPIPassThrough(lLoginID, m_struParam)) {
            log.info("测温 参数规则 配置, error：" + hcisupCms.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam.read();
            ptrOutByte2.read();
            log.info("测温 参数规则 配置succeed" + "输出报文:\n" + new String(ptrOutByte2.byValue).trim());
        }
        return;

    }



    //注册回调
    public boolean registerCallBack(int lUserID, int dwDataType, Pointer pOutBuffer, int dwOutLen, Pointer pInBuffer, int dwInLen, Pointer pUser) {

        switch (dwDataType) {
            //设备上线回调  以下三种情况 都标识设备上线
//            ENUM_DEV_ON（0）：表示设备上线。
//            ENUM_DEV_ADDRESS_CHANGED（2）：表示设备上线。实际为设备在心跳超时时间内重新上线，采用的IP和端口跟之前链路不同（IP地址或端口发生了变化）。
//            ENUM_DEV_DAS_REREGISTER（7）：表示设备上线。实际为设备在心跳超时时间内重新上线，采用的IP和端口跟之前链路相同。
            case HcisupCms.EHOME_REGISTER_TYPE.ENUM_DEV_ADDRESS_CHANGED:
            case HcisupCms.EHOME_REGISTER_TYPE.ENUM_DEV_DAS_REREGISTER:
            case HcisupCms.EHOME_REGISTER_TYPE.ENUM_DEV_ON: {
                HcisupCms.NET_EHOME_DEV_REG_INFO_V12 strDevRegInfo = new HcisupCms.NET_EHOME_DEV_REG_INFO_V12();
                strDevRegInfo.write();
                Pointer pDevRegInfo = strDevRegInfo.getPointer();
                pDevRegInfo.write(0, pOutBuffer.getByteArray(0, strDevRegInfo.size()), 0, strDevRegInfo.size());
                strDevRegInfo.read();

                if (ObjectUtil.isNotNull(isupDeviceService.selectIsupDeviceByLuserIdAndIp(lUserID, new String(strDevRegInfo.struRegInfo.struDevAdd.szIP).trim()))) {
                    isupDeviceService.updateIsupDeviceStatusByLuserId(lUserID, new String(strDevRegInfo.struRegInfo.struDevAdd.szIP).trim(), "ON");
                } else {
                    IsupDevice isupDevice = new IsupDevice();
                    isupDevice.setDeviceId(new String(strDevRegInfo.struRegInfo.byDeviceID).trim());
                    isupDevice.setDwSize(strDevRegInfo.struRegInfo.dwSize);
                    isupDevice.setDwNetUnitType(strDevRegInfo.struRegInfo.dwNetUnitType);
                    isupDevice.setFirmwareVersion(new String(strDevRegInfo.struRegInfo.byFirmwareVersion).trim());
                    isupDevice.setIpAddress(new String(strDevRegInfo.struRegInfo.struDevAdd.szIP).trim());
                    isupDevice.setPort(strDevRegInfo.struRegInfo.struDevAdd.wPort);
                    isupDevice.setDeviceRes(new String(strDevRegInfo.struRegInfo.struDevAdd.byRes).trim());
                    isupDevice.setDevType(strDevRegInfo.struRegInfo.dwDevType);
                    isupDevice.setManufacture(strDevRegInfo.struRegInfo.dwManufacture);
                    isupDevice.setPassword(new String(strDevRegInfo.struRegInfo.byPassWord).trim());
                    isupDevice.setDeviceSerial(new String(strDevRegInfo.struRegInfo.sDeviceSerial).trim());
                    isupDevice.setReliableTransmission(strDevRegInfo.struRegInfo.byReliableTransmission);
                    isupDevice.setWebsocketTransmission(strDevRegInfo.struRegInfo.byWebSocketTransmission);
                    isupDevice.setSupportRedirect(strDevRegInfo.struRegInfo.bySupportRedirect);
                    isupDevice.setDevProtocolVersion(new String(strDevRegInfo.struRegInfo.byDevProtocolVersion).trim());
                    isupDevice.setSessionKey(new String(strDevRegInfo.struRegInfo.bySessionKey).trim());
                    isupDevice.setRes(new String(strDevRegInfo.struRegInfo.byRes).trim());
                    isupDevice.setMarketType(strDevRegInfo.struRegInfo.byMarketType);
                    isupDevice.setLuserId(lUserID);
                    isupDevice.setStatus("ON");
                    isupDeviceService.insertIsupDevice(isupDevice);
                }

                HcisupCms.NET_EHOME_SERVER_INFO_V50 strEhomeServerInfo = new HcisupCms.NET_EHOME_SERVER_INFO_V50();
                strEhomeServerInfo.read();
                //strEhomeServerInfo.dwSize = strEhomeServerInfo.size();
                byte[] byCmsIP = new byte[0];
                //设置报警服务器地址、端口、类型
                byCmsIP = isupLinuxConfig.getAlarmServerIP().getBytes();
                System.arraycopy(byCmsIP, 0, strEhomeServerInfo.struUDPAlarmSever.szIP, 0, byCmsIP.length);
                System.arraycopy(byCmsIP, 0, strEhomeServerInfo.struTCPAlarmSever.szIP, 0, byCmsIP.length);

                strEhomeServerInfo.dwAlarmServerType = isupLinuxConfig.getAlarmServerType();
                strEhomeServerInfo.struTCPAlarmSever.wPort = isupLinuxConfig.getAlarmServerTCPPort();
                strEhomeServerInfo.struUDPAlarmSever.wPort = isupLinuxConfig.getAlarmServerUDPPort();
                byte[] byClouldAccessKey = "test".getBytes();
                System.arraycopy(byClouldAccessKey, 0, strEhomeServerInfo.byClouldAccessKey, 0, byClouldAccessKey.length);
                byte[] byClouldSecretKey = "12345".getBytes();
                System.arraycopy(byClouldSecretKey, 0, strEhomeServerInfo.byClouldSecretKey, 0, byClouldSecretKey.length);
                strEhomeServerInfo.dwClouldPoolId = 1;
                //设置图片存储服务器地址、端口、类型
                byte[] bySSIP = new byte[0];
                bySSIP = isupLinuxConfig.getPicServerIP().getBytes();
                System.arraycopy(bySSIP, 0, strEhomeServerInfo.struPictureSever.szIP, 0, bySSIP.length);
                strEhomeServerInfo.struPictureSever.wPort = isupLinuxConfig.getPicServerPort();
                strEhomeServerInfo.dwPicServerType = isupLinuxConfig.getPicServerType();
                strEhomeServerInfo.write();
                dwInLen = strEhomeServerInfo.size();
                pInBuffer.write(0, strEhomeServerInfo.getPointer().getByteArray(0, dwInLen), 0, dwInLen);

                String deviceID = new String(strDevRegInfo.struRegInfo.byDeviceID).trim();
                log.info("设备上线，设备ID为:" + deviceID.trim());
                // 目前不清楚 一个userID(会话id)  是否 对应多个设备id
                if (!lLoginIDList.contains(lUserID)) {
                    lLoginIDList.add(lUserID);
                }

                // 2. 一个会话对应1个设备id
                deviceInfoMap.put(lUserID, deviceID);
                log.info("目前在线设备: {}", deviceInfoMap);

                break;
            }
            //Ehome5.0设备认证回调
            case HcisupCms.EHOME_REGISTER_TYPE.ENUM_DEV_AUTH: { //ENUM_DEV_AUTH
                HcisupCms.NET_EHOME_DEV_REG_INFO_V12 strDevRegInfo = new HcisupCms.NET_EHOME_DEV_REG_INFO_V12();
                strDevRegInfo.write();
                Pointer pDevRegInfo = strDevRegInfo.getPointer();
                pDevRegInfo.write(0, pOutBuffer.getByteArray(0, strDevRegInfo.size()), 0, strDevRegInfo.size());
                strDevRegInfo.read();
                byte[] bs = new byte[0];
                String szEHomeKey = isupLinuxConfig.getISUPKey(); //ISUP5.0登录校验值
                bs = szEHomeKey.getBytes();
                pInBuffer.write(0, bs, 0, szEHomeKey.length());
                break;
            }
            //Ehome5.0设备Sessionkey回调
            case HcisupCms.EHOME_REGISTER_TYPE.ENUM_DEV_SESSIONKEY: { //HCISUPCMS.ENUM_DEV_SESSIONKEY
                HcisupCms.NET_EHOME_DEV_REG_INFO_V12 strDevRegInfo = new HcisupCms.NET_EHOME_DEV_REG_INFO_V12();
                strDevRegInfo.write();
                Pointer pDevRegInfo = strDevRegInfo.getPointer();
                pDevRegInfo.write(0, pOutBuffer.getByteArray(0, strDevRegInfo.size()), 0, strDevRegInfo.size());
                strDevRegInfo.read();
                HcisupCms.NET_EHOME_DEV_SESSIONKEY struSessionKey = new HcisupCms.NET_EHOME_DEV_SESSIONKEY();
                System.arraycopy(strDevRegInfo.struRegInfo.byDeviceID, 0, struSessionKey.sDeviceID, 0, strDevRegInfo.struRegInfo.byDeviceID.length);
                System.arraycopy(strDevRegInfo.struRegInfo.bySessionKey, 0, struSessionKey.sSessionKey, 0, strDevRegInfo.struRegInfo.bySessionKey.length);
                struSessionKey.write();
                Pointer pSessionKey = struSessionKey.getPointer();
                CmsService.hcisupCms.NET_ECMS_SetDeviceSessionKey(pSessionKey);
                //报警key
                AlarmService.hcEHomeAlarm.NET_EALARM_SetDeviceSessionKey(pSessionKey);
                break;
            }
            case HcisupCms.EHOME_REGISTER_TYPE.ENUM_DEV_DAS_REQ: {  //HCISUPCMS.ENUM_DEV_DAS_REQ
                String dasInfo = "{\n" +
                        "    \"Type\":\"DAS\",\n" +
                        "    \"DasInfo\": {\n" +
                        "        \"Address\":\"" + isupLinuxConfig.getDasServerIP() + "\",\n" +
                        "        \"Domain\":\"\",\n" +
                        "        \"ServerID\":\"\",\n" +
                        "        \"Port\":" + isupLinuxConfig.getDasServerPort() + ",\n" +
                        "        \"UdpPort\":0\n" +
                        "    }\n" +
                        "}";
                log.info("das重定向请求回调：" + dasInfo);
                byte[] bs1 = dasInfo.getBytes();
                pInBuffer.write(0, bs1, 0, dasInfo.length());
                break;
            }
            //设备下线回调
            case HcisupCms.EHOME_REGISTER_TYPE.ENUM_DEV_OFF: {
                // 设备掉线
                // 2. 一个会话对应1个设备id
                String deviceID = deviceInfoMap.get(lUserID);
                deviceInfoMap.remove(lUserID);
                log.info("设备下线了，设备id为：" + deviceID);
                break;
            }
            default:
                log.info("回调 默认 type:" + dwDataType);
                break;
        }
        return true;
    }


    @PreDestroy
    public void destroy() {
        //停止监听释放
        if (cmsHandle >= 0) {
            log.info(" 停止注册CMS服务,cmsHandle:" + cmsHandle);
            hcisupCms.NET_ECMS_StopListen(cmsHandle);
            hcisupCms.NET_ECMS_Fini();
        }
    }


}
