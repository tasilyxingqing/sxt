package com.ruoyi.isup.linux64.service;

import com.ruoyi.isup.config.IsupLinuxConfig;
import com.ruoyi.isup.linux64.callBack.EHomeMsgCallBack;
import com.ruoyi.isup.linux64.interfaces.HcisupAlarm;
import com.ruoyi.isup.linux64.utils.HkUtils;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PreDestroy;

/***
 * 报警服务
 **/
@Service("linuxAlarm")
@Slf4j
public class AlarmService {

    public static HcisupAlarm hcEHomeAlarm = null;

    public static int alarmHandle = -1; //Alarm监听句柄
    @Autowired
    IsupLinuxConfig isupLinuxConfig;


    static HcisupAlarm.NET_EHOME_ALARM_LISTEN_PARAM net_ehome_alarm_listen_param = new HcisupAlarm.NET_EHOME_ALARM_LISTEN_PARAM();

    @Lazy
    @Autowired
    EHomeMsgCallBack eHomeMsgCallBack;

    /**
     * 根据不同操作系统选择不同的库文件和库路径 目前只实现了 linux64
     */
    private static boolean createInstance() {
        if (hcEHomeAlarm == null) {
            synchronized (HcisupAlarm.class) {
                try {
                    hcEHomeAlarm = (HcisupAlarm) Native.loadLibrary(HkUtils.getAmsPath(), HcisupAlarm.class);
                } catch (Exception ex) {
                    log.info("Alarm加载不同的操作库 Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Alarm 服务初始化
     */
    public void init() {
        if (hcEHomeAlarm == null) {
            if (!createInstance()) {
                log.info("Alarm 初始化失败！");
                return;
            }
        }
        // 加载 crypto
        HcisupAlarm.BYTE_ARRAY ptrByteArrayCrypto = new HcisupAlarm.BYTE_ARRAY(256);
        String strPathCrypto = HkUtils.getCryptoPath();
        System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, strPathCrypto.length());
        ptrByteArrayCrypto.write();
        hcEHomeAlarm.NET_EALARM_SetSDKInitCfg(0, ptrByteArrayCrypto.getPointer());

        //加载 ssl
        HcisupAlarm.BYTE_ARRAY ptrByteArraySsl = new HcisupAlarm.BYTE_ARRAY(256);
        String strPathSsl = HkUtils.getSslPath();
        System.arraycopy(strPathSsl.getBytes(), 0, ptrByteArraySsl.byValue, 0, strPathSsl.length());
        ptrByteArraySsl.write();
        hcEHomeAlarm.NET_EALARM_SetSDKInitCfg(1, ptrByteArraySsl.getPointer());

        //报警服务初始化
        boolean bRet = hcEHomeAlarm.NET_EALARM_Init();
        if (!bRet) {
            log.info("Alarm 报警服务初始化失败!");
        }
        //设置HCAapSDKCom组件库文件夹所在路径
        HcisupAlarm.BYTE_ARRAY ptrByteArrayCom = new HcisupAlarm.BYTE_ARRAY(256);
        String strPathCom = HkUtils.getHCAapSDKComPath();
        System.arraycopy(strPathCom.getBytes(), 0, ptrByteArrayCom.byValue, 0, strPathCom.length());
        ptrByteArrayCom.write();
        hcEHomeAlarm.NET_EALARM_SetSDKLocalCfg(5, ptrByteArrayCom.getPointer());

        // 设置日志写入路径
        hcEHomeAlarm.NET_EALARM_SetLogToFile(3, isupLinuxConfig.getAlarmLogPath(), false);
    }


    /**
     * 开启报警服务监听
     */
    public void startAlarmListen() {

        System.arraycopy(isupLinuxConfig.getAlarmServerListenIP().getBytes(), 0, net_ehome_alarm_listen_param.struAddress.szIP, 0, isupLinuxConfig.getAlarmServerListenIP().length());
        if (isupLinuxConfig.getAlarmServerType() == 2) {
            net_ehome_alarm_listen_param.struAddress.wPort = isupLinuxConfig.getAlarmServerListenTCPPort();
            net_ehome_alarm_listen_param.byProtocolType = 2; //协议类型：0- TCP，1- UDP, 2-MQTT
        } else {
            net_ehome_alarm_listen_param.struAddress.wPort = isupLinuxConfig.getAlarmServerListenUDPPort();
            net_ehome_alarm_listen_param.byProtocolType = 1; //协议类型：0- TCP，1- UDP, 2-MQTT
        }
        net_ehome_alarm_listen_param.fnMsgCb = eHomeMsgCallBack;
        net_ehome_alarm_listen_param.byUseCmsPort = 0; //是否复用CMS端口：0- 不复用，非0- 复用
        net_ehome_alarm_listen_param.write();

        //启动报警服务器监听
        alarmHandle = hcEHomeAlarm.NET_EALARM_StartListen(net_ehome_alarm_listen_param);
        if (alarmHandle < 0) {
            log.info(" 报警服务监听 失败, error:" + hcEHomeAlarm.NET_EALARM_GetLastError());
            hcEHomeAlarm.NET_EALARM_Fini();
            return;
        }

        String AlarmListenInfo = new String(net_ehome_alarm_listen_param.struAddress.szIP).trim() + "_" + net_ehome_alarm_listen_param.struAddress.wPort;
        log.info(" 报警服务器：" + AlarmListenInfo + ",Alarm 监听成功 alarmHandle:" + alarmHandle);

    }


    //报警回调
    public boolean homeMsgCallBack(int iHandle, HcisupAlarm.NET_EHOME_ALARM_MSG pAlarmMsg, Pointer pUser) {
        log.info("4. pAlarmMsg.dwXmlBufLen：" + pAlarmMsg.dwXmlBufLen);
        if (pAlarmMsg.dwXmlBufLen != 0) {
            HcisupAlarm.BYTE_ARRAY strXMLData = new HcisupAlarm.BYTE_ARRAY(pAlarmMsg.dwXmlBufLen);
            strXMLData.write();
            Pointer pPlateInfo = strXMLData.getPointer();
            pPlateInfo.write(0, pAlarmMsg.pXmlBuf.getByteArray(0, strXMLData.size()), 0, strXMLData.size());
            strXMLData.read();
            String strXML = new String(strXMLData.byValue).trim();
            // 告警接收信息输出到文件中
            if ("file".equals(isupLinuxConfig.getEventInfoPrintType())) {
                // 输出事件信息到文件中 "dwAlarmType_pXmlBuf_"+ pAlarmMsg.dwAlarmType, ".xml", strXML
                log.info("告警内容：" + strXML);
            } else {
                // 输出事件信息到控制台上
                System.out.println(strXML + "\n");
            }
        }

        //根据报警类型 不同  处理方式不同
        processAlarmData(pAlarmMsg.dwAlarmType,
                pAlarmMsg.pAlarmInfo, pAlarmMsg.dwAlarmInfoLen,
                pAlarmMsg.pXmlBuf, pAlarmMsg.dwXmlBufLen,
                pAlarmMsg.pHttpUrl, pAlarmMsg.dwHttpUrlLen);
        return true;
    }

    public void processAlarmData(int dwAlarmType, Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen, Pointer pUrl, int dwUrlLen) {
        log.info("根据报警类型来处理：");
        if (pUrl != Pointer.NULL) {
            dwAlarmType = HcisupAlarm.EHOME_ISAPI_ALARM;
        }
        switch (dwAlarmType) {
            case HcisupAlarm.EHOME_ALARM_ACS: {
                //根据事件类型 进行不同的出来  更多类型 文档 和 demo里有
                break;
            }
            default: {
                log.info("未找到相关处理的报警类型: " + dwAlarmType);
            }
        }
    }





    @PreDestroy
    public void destroy() {
        //停止监听释放
        if (alarmHandle >= 0) {
            log.info(" 停止报警Alarm服务" + alarmHandle);
            hcEHomeAlarm.NET_EALARM_StopListen(alarmHandle);
            hcEHomeAlarm.NET_EALARM_Fini();
        }
    }
}



