package com.ruoyi.onvif.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;

import java.util.List;

/**
 * xml转json
 *
 * @author 陈江灿
 */
public class XmlToJsonUtils {

    public static JSONObject xmlToJson(String xml) {
        if (StringUtils.isEmpty(xml)) {
            return null;
        }
        xml = xml.trim();
        Document doc;
        try {
            doc = DocumentHelper.parseText(xml);
            JSONObject json = new JSONObject();
            dom4j2Json(doc.getRootElement(), json);
            if (xml.contains("Body")) {
                return json.getJSONObject("Body");
            } else if (xml.contains("body")) {
                return json.getJSONObject("body");
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * xml转json
     */
    public static void dom4j2Json(Element element, JSONObject json) {
        for (Object o : element.attributes()) {
            Attribute attr = (Attribute)o;
            if (!isEmpty(attr.getValue())) {
                json.put("@" + attr.getName(), attr.getValue());
            }
        }
        List<Element> chdEl = element.elements();
        if (chdEl.isEmpty() && !isEmpty(element.getText())) {
            json.put(element.getName(), element.getText());
        }

        for (Element e : chdEl) {
            if (!e.elements().isEmpty()) {
                JSONObject chdjson = new JSONObject();
                dom4j2Json(e, chdjson);
                Object o = json.get(e.getName());
                if (o != null) {
                    JSONArray jsona = null;
                    if (o instanceof JSONObject) {
                        JSONObject jsono = (JSONObject)o;
                        json.remove(e.getName());
                        jsona = new JSONArray();
                        jsona.add(jsono);
                        jsona.add(chdjson);
                    }
                    if (o instanceof JSONArray) {
                        jsona = (JSONArray)o;
                        jsona.add(chdjson);
                    }
                    json.put(e.getName(), jsona);
                } else {
                    if (!chdjson.isEmpty()) {
                        json.put(e.getName(), chdjson);
                    }
                }

            } else {// 子元素没有子元素
                for (Object o : element.attributes()) {
                    Attribute attr = (Attribute)o;
                    if (!isEmpty(attr.getValue())) {
                        json.put("@" + attr.getName(), attr.getValue());
                    }
                }
                if (!e.getText().isEmpty()) {
                    json.put(e.getName(), e.getText());
                }
            }
        }
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().isEmpty() || "null".equals(str)) {
            return true;
        }
        return false;
    }
}
