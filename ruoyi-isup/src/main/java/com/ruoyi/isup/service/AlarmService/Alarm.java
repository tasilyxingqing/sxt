package com.ruoyi.isup.service.AlarmService;

import com.ruoyi.isup.common.osSelect;
import com.ruoyi.isup.config.IsupConfig;
import com.ruoyi.isup.service.cmsService.HCISUPCMS;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("alarmService")
public class Alarm {
    public static HCISUPAlarm hcEHomeAlarm = null;
    public static int AlarmHandle = -1; //Alarm监听句柄
    static EHomeMsgCallBack cbEHomeMsgCallBack;//报警监听回调函数实现
    static HCISUPAlarm.NET_EHOME_ALARM_LISTEN_PARAM net_ehome_alarm_listen_param = new HCISUPAlarm.NET_EHOME_ALARM_LISTEN_PARAM();

    @Autowired
    private IsupConfig isupConfig;

    /**
     * 根据不同操作系统选择不同的库文件和库路径
     *
     * @return
     */
    private static boolean CreateSDKInstance() {
        if (hcEHomeAlarm == null) {
            synchronized (HCISUPAlarm.class) {
                String strDllPath = "";
                try {
                    //System.setProperty("jna.debug_load", "true");
                    if (osSelect.isWindows())
                        //win系统加载库路径(路径不要带中文)
                    {
                        strDllPath = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\HCISUPAlarm.dll";
                    } else if (osSelect.isLinux())
                        //Linux系统加载库路径(路径不要带中文)
                    {
                        strDllPath = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libHCISUPAlarm.so";
                    }
                    hcEHomeAlarm = (HCISUPAlarm) Native.loadLibrary(strDllPath, HCISUPAlarm.class);
                } catch (Exception ex) {
                    System.out.println("loadLibrary: " + strDllPath + " Error: " + ex.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    public void eAlarm_Init() {
        if (hcEHomeAlarm == null) {
            if (!CreateSDKInstance()) {
                System.out.println("Load CMS SDK fail");
                return;
            }
        }
        if (osSelect.isWindows()) {
            HCISUPCMS.BYTE_ARRAY ptrByteArrayCrypto = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathCrypto = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\libeay32.dll"; //Linux版本是libcrypto.so库文件的路径
            System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, strPathCrypto.length());
            ptrByteArrayCrypto.write();
            hcEHomeAlarm.NET_EALARM_SetSDKInitCfg(0, ptrByteArrayCrypto.getPointer());

            //设置libssl.so所在路径
            HCISUPCMS.BYTE_ARRAY ptrByteArraySsl = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathSsl = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\ssleay32.dll";    //Linux版本是libssl.so库文件的路径
            System.arraycopy(strPathSsl.getBytes(), 0, ptrByteArraySsl.byValue, 0, strPathSsl.length());
            ptrByteArraySsl.write();
            hcEHomeAlarm.NET_EALARM_SetSDKInitCfg(1, ptrByteArraySsl.getPointer());

            //报警服务初始化
            boolean bRet = hcEHomeAlarm.NET_EALARM_Init();
            if (!bRet) {
                System.out.println("NET_EALARM_Init failed!");
            }else {
                System.out.println("报警服务初始化成功");
            }
            //设置HCAapSDKCom组件库文件夹所在路径
            HCISUPCMS.BYTE_ARRAY ptrByteArrayCom = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathCom = System.getProperty("user.dir") + "\\ruoyi-isup\\win-lib\\HCAapSDKCom";        //只支持绝对路径，建议使用英文路径
            System.arraycopy(strPathCom.getBytes(), 0, ptrByteArrayCom.byValue, 0, strPathCom.length());
            ptrByteArrayCom.write();
            hcEHomeAlarm.NET_EALARM_SetSDKLocalCfg(5, ptrByteArrayCom.getPointer());
        } else if (osSelect.isLinux()) {
            HCISUPCMS.BYTE_ARRAY ptrByteArrayCrypto = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathCrypto = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libcrypto.so"; //Linux版本是libcrypto.so库文件的路径
            System.arraycopy(strPathCrypto.getBytes(), 0, ptrByteArrayCrypto.byValue, 0, strPathCrypto.length());
            ptrByteArrayCrypto.write();
            hcEHomeAlarm.NET_EALARM_SetSDKInitCfg(0, ptrByteArrayCrypto.getPointer());

            //设置libssl.so所在路径
            HCISUPCMS.BYTE_ARRAY ptrByteArraySsl = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathSsl = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/libssl.so";    //Linux版本是libssl.so库文件的路径
            System.arraycopy(strPathSsl.getBytes(), 0, ptrByteArraySsl.byValue, 0, strPathSsl.length());
            ptrByteArraySsl.write();
            hcEHomeAlarm.NET_EALARM_SetSDKInitCfg(1, ptrByteArraySsl.getPointer());
            //报警服务初始化
            boolean bRet = hcEHomeAlarm.NET_EALARM_Init();
            if (!bRet) {
                System.out.println("NET_EALARM_Init failed!");
            }else {
                System.out.println("报警服务初始化成功");
            }
            //设置HCAapSDKCom组件库文件夹所在路径
            HCISUPCMS.BYTE_ARRAY ptrByteArrayCom = new HCISUPCMS.BYTE_ARRAY(256);
            String strPathCom = System.getProperty("user.dir") + "/ruoyi-isup/linux-lib/HCAapSDKCom/";        //只支持绝对路径，建议使用英文路径
            System.arraycopy(strPathCom.getBytes(), 0, ptrByteArrayCom.byValue, 0, strPathCom.length());
            ptrByteArrayCom.write();
            hcEHomeAlarm.NET_EALARM_SetSDKLocalCfg(5, ptrByteArrayCom.getPointer());
        }
    }

    /**
     * 开启报警服务监听
     */
    public void startAlarmListen() {
        if (cbEHomeMsgCallBack == null) {
            cbEHomeMsgCallBack = new EHomeMsgCallBack();
        }
        System.arraycopy(isupConfig.getIp().getBytes(), 0, net_ehome_alarm_listen_param.struAddress.szIP, 0, isupConfig.getIp().length());

        if (Short.parseShort(isupConfig.getAlarmServerType()) == 2) {
            net_ehome_alarm_listen_param.struAddress.wPort = Short.parseShort(String.valueOf(isupConfig.getAlarmServicePort()));
            net_ehome_alarm_listen_param.byProtocolType = 2; //协议类型：0- TCP，1- UDP, 2-MQTT
        } else {
            net_ehome_alarm_listen_param.struAddress.wPort = Short.parseShort(String.valueOf(isupConfig.getAlarmServicePort()));
            net_ehome_alarm_listen_param.byProtocolType = 1; //协议类型：0- TCP，1- UDP, 2-MQTT
        }
        net_ehome_alarm_listen_param.fnMsgCb = cbEHomeMsgCallBack;
        net_ehome_alarm_listen_param.byUseCmsPort = 0; //是否复用CMS端口：0- 不复用，非0- 复用
        net_ehome_alarm_listen_param.write();

        //启动报警服务器监听
        AlarmHandle = hcEHomeAlarm.NET_EALARM_StartListen(net_ehome_alarm_listen_param);
        if (AlarmHandle < 0) {
            System.out.println("NET_EALARM_StartListen failed, error:" + hcEHomeAlarm.NET_EALARM_GetLastError());
            hcEHomeAlarm.NET_EALARM_Fini();
            return;
        } else {
            String AlarmListenInfo = new String(net_ehome_alarm_listen_param.struAddress.szIP).trim() + "_" + net_ehome_alarm_listen_param.struAddress.wPort;
            System.out.println("报警服务器：" + AlarmListenInfo + ",NET_EALARM_StartListen succeed");
        }
    }

    /**
     * 报警回调函数实现，设备上传事件通过回调函数上传进行解析
     */
    class EHomeMsgCallBack implements HCISUPAlarm.EHomeMsgCallBack {
        @Override
        public boolean invoke(int iHandle, HCISUPAlarm.NET_EHOME_ALARM_MSG pAlarmMsg, Pointer pUser) {
            System.out.println("AlarmType: " + pAlarmMsg.dwAlarmType + ",dwAlarmInfoLen:" + pAlarmMsg.dwAlarmInfoLen + ",dwXmlBufLen:" + pAlarmMsg.dwXmlBufLen + "\n");
            if (pAlarmMsg.dwXmlBufLen != 0) {
                HCISUPAlarm.BYTE_ARRAY strXMLData = new HCISUPAlarm.BYTE_ARRAY(pAlarmMsg.dwXmlBufLen);
                strXMLData.write();
                Pointer pPlateInfo = strXMLData.getPointer();
                pPlateInfo.write(0, pAlarmMsg.pXmlBuf.getByteArray(0, strXMLData.size()), 0, strXMLData.size());
                strXMLData.read();
                String strXML = new String(strXMLData.byValue).trim();
                // 输出事件信息到控制台上
                System.out.println(strXML + "\n");
            }
            AlarmEventHandle.processAlarmData(pAlarmMsg.dwAlarmType,
                    pAlarmMsg.pAlarmInfo, pAlarmMsg.dwAlarmInfoLen,
                    pAlarmMsg.pXmlBuf, pAlarmMsg.dwXmlBufLen,
                    pAlarmMsg.pHttpUrl, pAlarmMsg.dwHttpUrlLen);
            return true;
        }
    }

}
