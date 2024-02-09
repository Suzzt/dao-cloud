package com.junmo.core.util;

import com.google.common.collect.Maps;
import com.junmo.core.model.HttpServletRequestModel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.ClassUtils;

/**
 * http 泛化调用工具类
 * @author wuzhenhong
 * @date 2024/2/9 9:10
 */
public class HttpGenericInvokeUtils {


    public static HttpServletRequestModel buildRequest(HttpServletRequest request)
        throws Exception {
        HttpServletRequestModel requestModel = new HttpServletRequestModel();
        requestModel.setHttpMethod(request.getMethod());
        requestModel.setURI(request.getRequestURI());
        requestModel.setParams(request.getParameterMap());

        InputStream inputStream = request.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] tmp = new byte[2048];
        int i = inputStream.read(tmp);
        while (i > 0) {
            baos.write(tmp, 0, i);
            i = inputStream.read(tmp);
        }
        requestModel.setBodyData(baos.toByteArray());

        Enumeration<String> enumeration = request.getHeaderNames();
        Map<String, String> header = Maps.newHashMap();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            header.put(key.toLowerCase(), request.getHeader(key));
        }

        requestModel.setHeads(header);

        return requestModel;
    }

    public static boolean isApplicationJson(FullHttpRequest request) {

        if(request.headers().contains(HttpHeaderNames.CONTENT_TYPE)) {
            String[] headerArr = splitHeaderContentType(request.headers().get(HttpHeaderNames.CONTENT_TYPE));
            return Objects.nonNull(headerArr) && headerArr.length > 0 && headerArr[0].equals(HttpHeaderValues.APPLICATION_JSON.toString());
        } else {
            return false;
        }
    }

    public static String[] splitHeaderContentType(String sb) {
        int aStart;
        int aEnd;
        int bStart;
        int bEnd;
        int cStart;
        int cEnd;
        aStart = HttpGenericInvokeUtils.findNonWhitespace(sb, 0);
        aEnd = sb.indexOf(';');
        if (aEnd == -1) {
            return new String[]{sb, "", ""};
        }
        bStart = HttpGenericInvokeUtils.findNonWhitespace(sb, aEnd + 1);
        if (sb.charAt(aEnd - 1) == ' ') {
            aEnd--;
        }
        bEnd = sb.indexOf(';', bStart);
        if (bEnd == -1) {
            bEnd = HttpGenericInvokeUtils.findEndOfString(sb);
            return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), ""};
        }
        cStart = HttpGenericInvokeUtils.findNonWhitespace(sb, bEnd + 1);
        if (sb.charAt(bEnd - 1) == ' ') {
            bEnd--;
        }
        cEnd = HttpGenericInvokeUtils.findEndOfString(sb);
        return new String[]{sb.substring(aStart, aEnd), sb.substring(bStart, bEnd), sb.substring(cStart, cEnd)};
    }

    private static int findNonWhitespace(String sb, int offset) {
        int result;
        for (result = offset; result < sb.length(); result++) {
            if (!Character.isWhitespace(sb.charAt(result))) {
                break;
            }
        }
        return result;
    }

    private static int findEndOfString(String sb) {
        int result;
        for (result = sb.length(); result > 0; result --) {
            if (!Character.isWhitespace(sb.charAt(result - 1))) {
                break;
            }
        }
        return result;
    }

    public static Object getInitPrimitiveValue(Class<?> type) {

        if(!ClassUtils.isPrimitiveOrWrapper(type) || ClassUtils.isPrimitiveWrapper(type)) {
            return null;
        }
        if (boolean.class == type) {
            return false;
        } else if (byte.class == type) {
            return (byte) 0;
        } else if (char.class == type) {
            return (char) 0;
        } else if (double.class == type) {
            return 0D;
        } else if (float.class == type) {
            return 0F;
        } else if (int.class == type) {
            return 0;
        } else if (long.class == type) {
            return 0L;
        } else if (short.class == type) {
            return (short)0;
        } else {
            return null;
        }
    }
}
