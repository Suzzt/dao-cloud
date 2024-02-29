package com.dao.cloud.core.converter;

import com.dao.cloud.core.exception.DaoException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

/**
 * @author wuzhenhong
 * @date 2024/2/19 10:22
 */
public class StringToCharConverter implements Converter<String, Character> {

    @Override
    public Character convert(String source) {
        if(!StringUtils.hasText(source)) {
            throw new DaoException("不能将空字符串转化成指定的char类型！");
        }

        if(source.length() == 1) {
            return source.charAt(0);
        }

        if(source.length() == 3 && source.startsWith("'") && source.endsWith("'")) {
            return source.charAt(1);
        }

        throw new DaoException(String.format("不能将字符串%s转化成指定的char类型！", source));
    }
}
