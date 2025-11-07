package com.ruoyi.onvif.domain.bo;

public enum OnvifTypeBo {
    CUSTOM(""),
    GET_SERVICES("http://www.onvif.org/ver10/device/wsdl"),
    GET_DEVICE_INFORMATION("http://www.onvif.org/ver10/device/wsdl"),
    GET_MEDIA_PROFILES("http://www.onvif.org/ver10/media/wsdl"),
    GET_STREAM_URI("http://www.onvif.org/ver10/media/wsdl"),
    GET_RECORD_URI("http://www.onvif.org/ver10/recording/wsdl"),
    GET_REPLAY_URI("http://www.onvif.org/ver10/replay/wsdl"),
    GET_IMAGING_URI("http://www.onvif.org/ver10/imaging/wsdl"),
    GET_PTZ_URI("http://www.onvif.org/ver10/ptz/wsdl"),
    GET_EVENTS_URI("http://www.onvif.org/ver10/events/wsdl"),
    GET_EVENTS_PULL_MESSAGE_URI("http://www.onvif.org/ver10/events/wsdl");

    public final String namespace;

    private OnvifTypeBo(String namespace) {
        this.namespace = namespace;
    }

}
