package com.ruoyi.dahua.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.aizuda.zlm4j.structure.MK_INI;
import com.aizuda.zlm4j.structure.MK_MEDIA;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.dahua.H264RTMPPusher;
import com.ruoyi.dahua.callback.FRealDataCallback;
import com.ruoyi.common.config.ZlmApiProperties;
import com.ruoyi.dahua.domain.DahuaDevice;
import com.ruoyi.dahua.domain.DahuaDeviceScreenshot;
import com.ruoyi.dahua.lib.NetSDKLib;
import com.ruoyi.dahua.lib.NetSDKLib.*;
import com.ruoyi.dahua.lib.NetSDKLib.LLong;
import com.ruoyi.dahua.lib.ToolKits;
import com.ruoyi.dahua.mapper.DahuaDeviceMapper;
import com.ruoyi.dahua.module.LoginModule;
import com.ruoyi.dahua.service.IDahuaDeviceScreenshotService;
import com.ruoyi.dahua.service.IDahuaDeviceService;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import static com.ruoyi.common.service.ZlmService.ZLM_API;

/**
 * 大华设备Service业务层处理
 *
 * @author fengcheng
 * @date 2025-06-06
 */
@Service
public class DahuaDeviceServiceImpl implements IDahuaDeviceService {
    @Autowired
    private DahuaDeviceMapper dahuaDeviceMapper;

    @Autowired
    private IDahuaDeviceScreenshotService dahuaDeviceScreenshotService;

    @Value("${dahua.isPublicNetwork}")
    private boolean isPublicNetwork;

    /**
     * 默认上传的地址
     */
    private static String defaultBaseDir = RuoYiConfig.getProfile();

    public static HashMap<String, LLong> loginHandleMap = new HashMap<String, LLong>();

    @Autowired
    private RedisTemplate redisTemplate;

    public static H264RTMPPusher pusher = null;


    private final Map<Long, NetSDKLib.LLong> realHandleMap = new ConcurrentHashMap<Long, NetSDKLib.LLong>();
    private final Map<Long, FRealDataCallback> callbackMap = new ConcurrentHashMap<>();
    private final Map<Long, MK_MEDIA> mkMediaMap = new ConcurrentHashMap<>();


    @Value("${media.ip}")
    private String ip;


    /**
     * 设备断线通知回调
     */
    private static DahuaDeviceServiceImpl.DisConnect disConnect = new DahuaDeviceServiceImpl.DisConnect();

    /**
     * 网络连接恢复
     */
    private static DahuaDeviceServiceImpl.HaveReConnect haveReConnect = new DahuaDeviceServiceImpl.HaveReConnect();

    public static NetSDKLib netsdk = NetSDKLib.NETSDK_INSTANCE;

    // 设备信息
    public static NetSDKLib.NET_DEVICEINFO_Ex m_stDeviceInfo = new NetSDKLib.NET_DEVICEINFO_Ex();

    public fSnapReceiveCB m_SnapReceiveCB = new fSnapReceiveCB();

    private String DAHUA_SNAPPICTURE_LOCK_KEY = "dahua_snapPicture";

    @Value("${dahua.snapPictureInterval}")
    private int snapPictureInterval;


    @Autowired
    private ZlmApiProperties zlmApiProperties;


    /**
     * 查询大华设备
     *
     * @param id 大华设备主键
     * @return 大华设备
     */
    @Override
    public DahuaDevice selectDahuaDeviceById(Long id) {
        return dahuaDeviceMapper.selectDahuaDeviceById(id);
    }

    /**
     * 查询大华设备列表
     *
     * @param dahuaDevice 大华设备
     * @return 大华设备
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<DahuaDevice> selectDahuaDeviceList(DahuaDevice dahuaDevice) {
        return dahuaDeviceMapper.selectDahuaDeviceList(dahuaDevice);
    }

    /**
     * 新增大华设备
     *
     * @param dahuaDevice 大华设备
     * @return 结果
     */
    @Override
    public int insertDahuaDevice(DahuaDevice dahuaDevice) {
        setPlayUrl(dahuaDevice);
        dahuaDevice.setCreateTime(DateUtils.getNowDate());
        dahuaDevice.setCreateBy(String.valueOf(SecurityUtils.getUserId()));
        return dahuaDeviceMapper.insertDahuaDevice(dahuaDevice);
    }

    /**
     * 设置本地播放地址
     *
     * @param dahuaDevice
     */
    private void setPlayUrl(DahuaDevice dahuaDevice) {
        if("1".equals(dahuaDevice.getPlayType())){
            String username = dahuaDevice.getUserName();
            String password = dahuaDevice.getPassword();
            String ip = dahuaDevice.getIp();
            String channel = dahuaDevice.getChannel().toString();
            String port = "554";
            String rtspUrl = String.format("rtsp://%s:%s@%s:%s/cam/realmonitor?channel=%s&subtype=0", username, password, ip, port, channel);
            dahuaDevice.setUrl(rtspUrl);
        }
    }

    /**
     * 修改大华设备
     *
     * @param dahuaDevice 大华设备
     * @return 结果
     */
    @Override
    public int updateDahuaDevice(DahuaDevice dahuaDevice) {
        setPlayUrl(dahuaDevice);
        dahuaDevice.setUpdateTime(DateUtils.getNowDate());
        dahuaDevice.setUpdateBy(String.valueOf(SecurityUtils.getUserId()));
        return dahuaDeviceMapper.updateDahuaDevice(dahuaDevice);
    }

    /**
     * 批量删除大华设备
     *
     * @param ids 需要删除的大华设备主键
     * @return 结果
     */
    @Override
    public int deleteDahuaDeviceByIds(Long[] ids) {
        return dahuaDeviceMapper.deleteDahuaDeviceByIds(ids);
    }

    /**
     * 删除大华设备信息
     *
     * @param id 大华设备主键
     * @return 结果
     */
    @Override
    public int deleteDahuaDeviceById(Long id) {
        return dahuaDeviceMapper.deleteDahuaDeviceById(id);
    }

    /**
     * 大华设备登录
     *
     * @param dahuaDevice 大华设备
     * @return
     */
    @Override
    public Vector<Integer> login(DahuaDevice dahuaDevice) {
        // 打开工程，初始化
        LoginModule.init(disConnect, haveReConnect);

        Vector<Integer> chnlist = new Vector<>();

        // 登陆句柄
        LLong l = login(loginHandleMap, dahuaDevice.getIp(), Integer.parseInt(dahuaDevice.getPort()), dahuaDevice.getUserName(), dahuaDevice.getPassword(),dahuaDevice.getDeviceId());

        if (l.longValue() == 0 ? false : true) {
            for (int i = 0; i < m_stDeviceInfo.byChanNum; i++) {
                chnlist.add(i);
            }
            System.out.println("login success" + chnlist);
        } else {
            throw new ServiceException("登录失败, 帐号已被锁定");
        }

        return chnlist;
    }

    /**
     * 实时预览
     *
     */
    @Override
    public Map<String, String> startRealPlay(Long id) {
        return startRealPlayFun(id);
    }

    /**
     * 开始实时预览
     *
     * 码流类型 ，参考  NET_RealPlayType
     *                   0 		// 实时预览
     *                   3 		// 实时预览-从码流1
     *                   7 		// 多画面预览－4画面
     *                   9 		// 多画面预览－9画面
     *                   10 	    // 多画面预览－16画面
     * 回调的数据类型,详见 EM_REAL_DATA_TYPE
     *                   0;          // 私有码流
     *                   1;          // 国标PS码流
     *                   2;          // TS码流
     *                   3;          // MP4文件
     *                   4;          // 裸H264码流
     *                   5;	        // 流式FLV`
     * @return 预览句柄
     */
    public Map<String, String> startRealPlayFun(Long id) {
        DahuaDevice dahuaDevice = getLoginHandle(id);
        LLong m_hLoginHandle = dahuaDevice.getM_hLoginHandle();
        NetSDKLib.NET_IN_REALPLAY_BY_DATA_TYPE inParam = new NetSDKLib.NET_IN_REALPLAY_BY_DATA_TYPE();
        NetSDKLib.NET_OUT_REALPLAY_BY_DATA_TYPE outParam = new NetSDKLib.NET_OUT_REALPLAY_BY_DATA_TYPE();
        //这块需要动态传入获取视频流的通道id
        inParam.nChannelID = dahuaDevice.getChannel();
        //码流类型，0为实时预览
        inParam.rType = 0;
        //回调的数据类型
        inParam.emDataType = 4;
        //开始预览
        NetSDKLib.LLong lRealHandle = netsdk.CLIENT_RealPlayByDataType(m_hLoginHandle, inParam, outParam, 3000);

        Map<String, String> urls = new HashMap<>();
        MK_INI mkIni = ZLM_API.mk_ini_create();
        ZLM_API.mk_ini_set_option(mkIni, "enable_rtsp", "1");
        ZLM_API.mk_ini_set_option(mkIni, "enable_rtmp", "1");
        ZLM_API.mk_ini_set_option_int(mkIni, "auto_close", 1);
        MK_MEDIA mkMedia = ZLM_API.mk_media_create2("__defaultVhost__", "dh", dahuaDevice.getId().toString(), 0, mkIni);
        ZLM_API.mk_ini_release(mkIni);
        ZLM_API.mk_media_init_video(mkMedia, 0, 1, 1, 25.0f, 2500);
        ZLM_API.mk_media_init_audio(mkMedia, 2, 8000, 1, 16);
        ZLM_API.mk_media_init_complete(mkMedia);
        FRealDataCallback fRealDataCallBack = new FRealDataCallback(mkMedia, 25.0);
        if (m_hLoginHandle.longValue() != 0) {
            //设置回调的接收
            //lRealHandle ：拉流成功的句柄
            //你的接收回调的类里的方法 注：这里使用的是官方demo中的RealplayEx类下的接口
            netsdk.CLIENT_SetRealDataCallBackEx(lRealHandle, fRealDataCallBack,
                    null, 31);
            realHandleMap.put(id, lRealHandle);
            callbackMap.put(id, fRealDataCallBack);
            mkMediaMap.put(id, mkMedia);
            urls.put("rtsp", "rtsp://" + ip + ":" + zlmApiProperties.getDhRtspPort() + "/dh/" + dahuaDevice.getId());
            urls.put("rtmp", "rtmp://" + ip + ":" +  zlmApiProperties.getDhRtmpPort() + "/dh/" + dahuaDevice.getId());
            urls.put("flv", "http://" + ip + ":" +  zlmApiProperties.getDhHttpPort() + "/dh/" + dahuaDevice.getId() + ".flv");
            urls.put("hls", "http://" + ip + ":" +  zlmApiProperties.getDhHttpPort() + "/dh/" + dahuaDevice.getId() + "/hls.m3u8");
        } else {
            ZLM_API.mk_media_release(mkMedia);
            throw new ServiceException("播放失败");
        }
        return urls;
    }

    /**
     * 实时预览数据回调函数--扩展(pBuffer内存由SDK内部申请释放)
     */
    public static class CbfRealDataCallBackEx implements NetSDKLib.fRealDataCallBackEx {
        private CbfRealDataCallBackEx() {
        }

        private static class CallBackHolder {
            private static CbfRealDataCallBackEx instance = new CbfRealDataCallBackEx();
        }

        public static CbfRealDataCallBackEx getInstance() {
            return CallBackHolder.instance;
        }

        @Override
        public void invoke(LLong lRealHandle, int dwDataType, Pointer pBuffer,
                           int dwBufSize, int param, Pointer dwUser) {
            byte[] bytes = pBuffer.getByteArray(0, dwBufSize);
            System.out.println("收到数据：" + bytes.length);
        }
    }

    /**
     * 停止预览
     */
    @Override
    public int stopRealPlay(Long id) {
        NetSDKLib.LLong realHandle = realHandleMap.remove(id);
        FRealDataCallback callback = callbackMap.remove(id);
        MK_MEDIA mkMedia = mkMediaMap.remove(id);

        if (realHandle != null && realHandle.longValue() != 0) {
            try {
                netsdk.CLIENT_StopRealPlayEx(realHandle);
                System.out.println("停止大华拉流-----------------------------------------");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (callback != null) {
            try {
                callback.shutdown();
                System.out.println("关闭本地回调线程并清理队列--------------------------------");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        if (mkMedia != null) {
            try {
                ZLM_API.mk_media_release(mkMedia);
                System.out.println("主动停止推流---------------------------------------------");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * 大华设备云台控制（开始）
     *
     * @param direction 方向
     * @param id        设备id
     * @param speed     速度
     */
    @Override
    public boolean ptzControlStart(String direction, Long id, int speed) {
        DahuaDevice dahuaDevice = getLoginHandle(id);

        LLong m_hLoginHandle = dahuaDevice.getM_hLoginHandle();
        if ("up".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_UP_CONTROL,
                    0, speed, 0, 0);
        } else if ("down".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_DOWN_CONTROL,
                    0, speed, 0, 0);
        } else if ("left".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_LEFT_CONTROL,
                    0, speed, 0, 0);
        } else if ("right".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_RIGHT_CONTROL,
                    0, speed, 0, 0);
        } else if ("top-left".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_LEFTTOP,
                    0, speed, 0, 0);
        } else if ("upper-right".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_RIGHTTOP,
                    0, speed, 0, 0);
        } else if ("bottom-left".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_LEFTDOWN,
                    0, speed, 0, 0);
        } else if ("lower-right".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_RIGHTDOWN,
                    0, speed, 0, 0);
        } else if ("doubling+".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_ZOOM_ADD_CONTROL,
                    0, speed, 0, 0);
        } else if ("doubling-".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_ZOOM_DEC_CONTROL,
                    0, speed, 0, 0);
        } else if ("zoom+".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_FOCUS_ADD_CONTROL,
                    0, speed, 0, 0);
        } else if ("zoom-".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_FOCUS_DEC_CONTROL,
                    0, speed, 0, 0);
        } else if ("aperture+".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_APERTURE_ADD_CONTROL,
                    0, speed, 0, 0);
        } else if ("aperture-".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_APERTURE_DEC_CONTROL,
                    0, speed, 0, 0);
        }

        return false;
    }

    /**
     * 大华设备云台控制（停止）
     *
     * @param direction 方向
     * @param id        设备id
     * @return
     */
    @Override
    public boolean ptzControlUpEnd(String direction, Long id) {
        DahuaDevice dahuaDevice = getLoginHandle(id);

        LLong m_hLoginHandle = dahuaDevice.getM_hLoginHandle();
        System.out.println("m_hLoginHandle:" + m_hLoginHandle);
        if ("up".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_UP_CONTROL,
                    0, 0, 0, 1);
        } else if ("down".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_DOWN_CONTROL,
                    0, 0, 0, 1);
        } else if ("left".equals(direction)) {

            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_LEFT_CONTROL,
                    0, 0, 0, 1);
        } else if ("right".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_RIGHT_CONTROL,
                    0, 0, 0, 1);
        } else if ("top-left".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_LEFTTOP,
                    0, 0, 0, 1);
        } else if ("upper-right".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_RIGHTTOP,
                    0, 0, 0, 1);
        } else if ("bottom-left".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_LEFTDOWN,
                    0, 0, 0, 1);
        } else if ("lower-right".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_EXTPTZ_ControlType.NET_EXTPTZ_RIGHTDOWN,
                    0, 0, 0, 1);
        } else if ("doubling+".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_ZOOM_ADD_CONTROL,
                    0, 0, 0, 1);
        } else if ("doubling-".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_ZOOM_DEC_CONTROL,
                    0, 0, 0, 1);
        } else if ("zoom+".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_FOCUS_ADD_CONTROL,
                    0, 0, 0, 1);
        } else if ("zoom-".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_FOCUS_DEC_CONTROL,
                    0, 0, 0, 1);
        } else if ("aperture+".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_APERTURE_ADD_CONTROL,
                    0, 0, 0, 1);
        } else if ("aperture-".equals(direction)) {
            return netsdk.CLIENT_DHPTZControlEx(m_hLoginHandle, dahuaDevice.getChannel(),
                    NetSDKLib.NET_PTZ_ControlType.NET_PTZ_APERTURE_DEC_CONTROL,
                    0, 0, 0, 1);
        }
        return false;
    }

    /**
     * 查询地图大华设备列表
     *
     * @param dahuaDevice
     * @return
     */
    @Override
    public List<DahuaDevice> selectDahuaDeviceListMap(DahuaDevice dahuaDevice) {
        return dahuaDeviceMapper.selectDahuaDeviceListMap(dahuaDevice);
    }

    /**
     * 大华设备抓图
     *
     * @param id 设备id
     * @return
     */
    @Override
    public boolean snapPicture(Long id) {
        return snapPictureFun(id, 0, 0);
    }

    /**
     * 大华设备定时抓图
     *
     * @param id 设备id
     * @return
     */
    @Override
    public boolean timerCapturePicture(Long id) {
        return snapPictureFun(id, 1, snapPictureInterval);
    }

    /**
     * 大华设备停止定时抓图
     *
     * @param id 设备id
     * @return
     */
    @Override
    public boolean stopCapturePicture(Long id) {
        return snapPictureFun(id, -1, 0);
    }

    /**
     * 大华设备获取时间
     *
     * @param id 设备id
     * @return
     */
    @Override
    public String getTime(Long id) {
        LLong m_hLoginHandle = getLoginHandle(id).getM_hLoginHandle();

        NET_TIME deviceTime = new NET_TIME();

        if (!netsdk.CLIENT_QueryDeviceTime(m_hLoginHandle, deviceTime, 3000)) {
            System.err.println("CLIENT_QueryDeviceTime Failed!" + ToolKits.getErrorCodePrint());
            return null;
        }

        String date = deviceTime.toStringTime();
        date = date.replace("/", "-");

        if (date == null) {
            throw new ServiceException("获取大华设备时间失败");
        }
        return date;
    }

    /**
     * 大华设备设置时间
     *
     * @param id   设备id
     * @param date 时间
     * @param type 时间类型（true=当前时间,false=设置时间）
     * @return
     */
    @Override
    public boolean setTime(Long id, String date, boolean type) {
        LLong m_hLoginHandle = getLoginHandle(id).getM_hLoginHandle();

        if (type) {
            date = null;
        }

        NET_TIME deviceTime = new NET_TIME();
        if (date == null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = dateFormat.format(new java.util.Date());
        }

        String[] dateTime = date.split(" ");
        String[] arrDate = dateTime[0].split("-");
        String[] arrTime = dateTime[1].split(":");
        deviceTime.dwYear = Integer.parseInt(arrDate[0]);
        deviceTime.dwMonth = Integer.parseInt(arrDate[1]);
        deviceTime.dwDay = Integer.parseInt(arrDate[2]);
        deviceTime.dwHour = Integer.parseInt(arrTime[0]);
        deviceTime.dwMinute = Integer.parseInt(arrTime[1]);
        deviceTime.dwSecond = Integer.parseInt(arrTime[2]);

        if (!netsdk.CLIENT_SetupDeviceTime(m_hLoginHandle, deviceTime)) {
            System.err.println("CLIENT_SetupDeviceTime Failed!" + ToolKits.getErrorCodePrint());
            return false;
        }
        return true;
    }

    /**
     * 大华设备重启
     *
     * @param id 设备id
     * @return
     */
    @Override
    public boolean reboot(Long id) {
        LLong m_hLoginHandle = getLoginHandle(id).getM_hLoginHandle();

        if (!netsdk.CLIENT_ControlDevice(m_hLoginHandle, CtrlType.CTRLTYPE_CTRL_REBOOT, null, 3000)) {
            System.err.println("CLIENT_ControlDevice Failed!" + ToolKits.getErrorCodePrint());
            return false;
        }
        return true;
    }

    /**
     * 大华设备抓图
     *
     * @param id       设备id
     * @param mode     抓图模式
     * @param interval 抓图间隔
     * @return
     */
    public boolean snapPictureFun(Long id, int mode, int interval) {
        DahuaDevice dahuaDevice = getLoginHandle(id);

        LLong m_hLoginHandle = dahuaDevice.getM_hLoginHandle();

        redisTemplate.opsForValue().set("dahua_snapPicture", id);
        netsdk.CLIENT_SetSnapRevCallBack(m_SnapReceiveCB, null);

        // send caputre picture command to device
        NetSDKLib.SNAP_PARAMS stuSnapParams = new NetSDKLib.SNAP_PARAMS();
        stuSnapParams.Channel = dahuaDevice.getChannel();            // channel
        stuSnapParams.mode = mode;                // capture picture mode
        stuSnapParams.Quality = 3;                // picture quality
        stuSnapParams.InterSnap = interval;    // timer capture picture time interval
        stuSnapParams.CmdSerial = 0;            // request serial

        IntByReference reserved = new IntByReference(0);

        synchronized (DAHUA_SNAPPICTURE_LOCK_KEY) {
            if (!netsdk.CLIENT_SnapPictureEx(m_hLoginHandle, stuSnapParams, reserved)) {
                System.err.print("CLIENT_SnapPictureEx Failed!");
                return false;
            } else {
                System.out.println("CLIENT_SnapPictureEx success");
            }
        }
        return true;
    }

    /**
     * 大华设备获取登录句柄
     *
     * @param id 设备id
     * @return
     */
    public DahuaDevice getLoginHandle(Long id) {
        DahuaDevice dahuaDevice = selectDahuaDeviceById(id);

        LLong m_hLoginHandle = loginHandleMap.get("login:handle:" + dahuaDevice.getIp());

        if (ObjUtil.isNull(m_hLoginHandle)) {
            // 登陆句柄
            m_hLoginHandle = login(loginHandleMap, dahuaDevice.getIp(), Integer.parseInt(dahuaDevice.getPort()), dahuaDevice.getUserName(), dahuaDevice.getPassword(),dahuaDevice.getDeviceId());

        }

        dahuaDevice.setM_hLoginHandle(m_hLoginHandle);
        return dahuaDevice;
    }


    /**
     * 大华设备登录
     *
     * @param loginHandleMap
     * @param m_strIp
     * @param m_nPort
     * @param m_strUser
     * @param m_strPassword
     * @param deviceId
     * @return
     */
    public LLong login(HashMap<String, LLong> loginHandleMap, String m_strIp, int m_nPort, String m_strUser, String m_strPassword,String deviceId) {
        if(isPublicNetwork){
            final int tcpSpecCap = 2;// 主动注册方式
            final IntByReference errorReference = new IntByReference(0);
            final NetSDKLib.NET_DEVICEINFO_Ex deviceinfo = new NetSDKLib.NET_DEVICEINFO_Ex();

            // 将 序列号 转化为 pointer 类型
            com.ruoyi.dahua.lib.NativeString serial = new com.ruoyi.dahua.lib.NativeString(deviceId);
            LLong m_hLoginHandle = netsdk.CLIENT_LoginEx2(m_strIp, m_nPort, m_strUser, m_strPassword, tcpSpecCap, serial.getPointer(), deviceinfo, errorReference);
            if (0 == m_hLoginHandle.longValue()) {
                System.err.printf("Login Device[%s] Port[%d]Failed", m_strIp, m_nPort);
                return null;
            }
            loginHandleMap.put("login:handle:" + m_strIp, m_hLoginHandle);

            for (int i = 0; i < deviceinfo.byChanNum; i++) {
                System.out.println("通道号=================：" + i);
            }
            m_stDeviceInfo = deviceinfo;
            return m_hLoginHandle;
        }else {
            //IntByReference nError = new IntByReference(0);
            //入参
            NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY pstInParam = new NET_IN_LOGIN_WITH_HIGHLEVEL_SECURITY();
            pstInParam.nPort = m_nPort;
            pstInParam.szIP = m_strIp.getBytes();
            pstInParam.szPassword = m_strPassword.getBytes();
            pstInParam.szUserName = m_strUser.getBytes();
            //出参
            NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY pstOutParam = new NET_OUT_LOGIN_WITH_HIGHLEVEL_SECURITY();
            pstOutParam.stuDeviceInfo = m_stDeviceInfo;
            //m_hLoginHandle = netsdk.CLIENT_LoginEx2(m_strIp, m_nPort, m_strUser, m_strPassword, 0, null, m_stDeviceInfo, nError);
            LLong m_hLoginHandle = netsdk.CLIENT_LoginWithHighLevelSecurity(pstInParam, pstOutParam);
            if (m_hLoginHandle.longValue() == 0) {
                System.err.printf("Login Device[%s] Port[%d]Failed", m_strIp, m_nPort);
            } else {
                System.out.println("Login Success [ " + m_strIp + " ]");
            }
            loginHandleMap.put("login:handle:" + m_strIp, m_hLoginHandle);
            return m_hLoginHandle;
        }
    }

    // 设备断线回调: 通过 CLIENT_Init 设置该回调函数，当设备出现断线时，SDK会调用该函数
    private static class DisConnect implements NetSDKLib.fDisConnect {
        public void invoke(LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("Device[%s] Port[%d] DisConnect!\n", pchDVRIP, nDVRPort);
            System.out.println("设备断线");
        }
    }

    // 网络连接恢复，设备重连成功回调
    // 通过 CLIENT_SetAutoReconnect 设置该回调函数，当已断线的设备重连成功时，SDK会调用该函数
    private static class HaveReConnect implements NetSDKLib.fHaveReConnect {
        public void invoke(LLong m_hLoginHandle, String pchDVRIP, int nDVRPort, Pointer dwUser) {
            System.out.printf("ReConnect Device[%s] Port[%d]\n", pchDVRIP, nDVRPort);
            System.out.println("网络连接恢复，设备重连成功");
        }
    }

    /**
     * 大华设备抓图回调
     */
    public class fSnapReceiveCB implements NetSDKLib.fSnapRev {
        BufferedImage bufferedImage = null;

        public void invoke(LLong lLoginID, Pointer pBuf, int RevLen, int EncodeType, int CmdSerial, Pointer dwUser) {
            try {
                if (pBuf != null && RevLen > 0) {
                    String strFileName = extractFilename();

                    String absPath = getAbsoluteFile(defaultBaseDir, strFileName).getAbsolutePath();

                    System.out.println("strFileName = " + absPath);

                    byte[] buf = pBuf.getByteArray(0, RevLen);
                    ByteArrayInputStream byteArrInput = new ByteArrayInputStream(buf);
                    try {
                        bufferedImage = ImageIO.read(byteArrInput);
                        if (bufferedImage == null) {
                            return;
                        }
                        ImageIO.write(bufferedImage, "jpg", new File(absPath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String pathFileName = getPathFileName(strFileName);
                    System.out.println("pathFileName = " + pathFileName);

                    synchronized (DAHUA_SNAPPICTURE_LOCK_KEY) {
                        Long id = (Long) redisTemplate.opsForValue().get("dahua_snapPicture");

                        DahuaDeviceScreenshot dahuaDeviceScreenshot = new DahuaDeviceScreenshot();
                        dahuaDeviceScreenshot.setImage(pathFileName);
                        dahuaDeviceScreenshot.setDahuaDeviceId(id);
                        dahuaDeviceScreenshotService.insertDahuaDeviceScreenshot(dahuaDeviceScreenshot);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static final String getPathFileName(String fileName) throws IOException {
        return Constants.RESOURCE_PREFIX + "/" + fileName;
    }

    /**
     * 编码文件名
     */
    public static final String extractFilename() {
        return StringUtils.format("{}/{}.jpg", DateUtils.datePath(), Seq.getId(Seq.uploadSeqType));
    }

    public static final File getAbsoluteFile(String uploadDir, String fileName) throws IOException {
        File desc = new File(uploadDir + File.separator + fileName);

        if (!desc.exists()) {
            if (!desc.getParentFile().exists()) {
                desc.getParentFile().mkdirs();
            }
        }
        return desc;
    }




}
