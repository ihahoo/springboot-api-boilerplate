package cn.hahoo.boilerplate.utils;

import cn.hahoo.boilerplate.exception.ApiException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    public static void required(Object v, String name) {
        if (v == null) {
            throw new ApiException(400001, name + "不能为空", 422);
        }
        if (v instanceof String && v.equals("")) {
            throw new ApiException(400001, name + "不能为空", 422);
        }
        if (v instanceof Integer && (Integer)v == 0) {
            throw new ApiException(400001, name + "不能为空", 422);
        }
    }

    public static void stringType(Object v, String name) {
        if (v != null) {
            if (!(v instanceof String)) {
                throw new ApiException(400002, name + "必须是字符串类型", 422);
            }
        }
    }

    public static void numberType(Object v, String name) {
        if (v != null) {
            if (!(v instanceof Number)) {
                throw new ApiException(400002, name + "必须是数值类型", 422);
            }
        }
    }

    public static void email(Object v, String name, Boolean isRequired) {
        if (isRequired) {
            required(v, name);
        }
        if (v != null) {
            stringType(v, name);
            Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
            Matcher m = VALID_EMAIL_ADDRESS_REGEX.matcher((String)v);
            if (!m.matches()) {
                throw new ApiException(400002, name + "格式不正确", 422);
            }
        }
    }

    public static void email(Object v, Boolean isRequired) {
        email(v, "电子邮件", isRequired);
    }

    public static void mobile(Object v, String name, Boolean isRequired) {
        if (isRequired) {
            required(v, name);
        }
        if (v != null) {
            stringType(v, name);
            Pattern p = Pattern.compile("^(\\+?0?86\\-?)?1[345789]\\d{9}$");
            Matcher m = p.matcher((String)v);
            if (!m.matches()) {
                throw new ApiException(400002, name + "错误", 422);
            }
        }
    }

    public static void mobile(Object v, Boolean isRequired) {
        mobile(v, "手机号码", isRequired);
    }

    public static void uuid(Object v, String name, Boolean isRequired) {
        if (isRequired) {
            required(v, name);
        }
        if (v != null) {
            stringType(v, name);
            Pattern p = Pattern.compile("^[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}$", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher((String)v);
            if (!m.matches()) {
                throw new ApiException(400002, name + "格式错误", 422);
            }
        }
    }
}
