package com.ruoyi.isup.utils;

import com.sun.jna.Pointer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class CommonMethod {
    public static void WriteBuffToPointer(byte[] byData, Pointer pInBuffer) {
        pInBuffer.write(0, byData, 0, byData.length);
    }

    public static String byteToString(byte[] bytes) {
        if (null == bytes || bytes.length == 0) {
            return "";
        }
        int iLengthOfBytes = 0;
        for (byte st : bytes) {
            if (st != 0) {
                iLengthOfBytes++;
            } else
                break;
        }
        String strContent = "";
        try {
            strContent = new String(bytes, 0, iLengthOfBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return strContent;
    }

    /**
     * utf8字节数组转gbk字节数组
     *
     * @param utf8Bytes
     * @return
     */
    public static byte[] UTF8toGBK(byte[] utf8Bytes) {
        String utf8Str = new String(utf8Bytes, StandardCharsets.UTF_8);
        byte[] gbkBytes = utf8Str.getBytes(Charset.forName("GBK"));
        return gbkBytes;
    }

    /**
     * utf8字节数组转gbk字符串
     *
     * @param utf8Bytes
     * @return
     */
    public static String UTF8toGBKStr(byte[] utf8Bytes) {
        return new String(UTF8toGBK(utf8Bytes), Charset.forName("GBK"));
    }
}
