package com.yuenov.open.constant;

import com.yuenov.open.utils.UtilityAppConfig;
import com.yuenov.open.utils.UtilityException;
import com.renrui.libraries.util.UtilitySecurity;

public class ConstantInterFace {

    /**
     * 默认分页条数
     */
    public static int pageSize = 20;

    /**
     * 书城列表
     */
    public static int categoriesListPageSize = pageSize;

    private static int domainDefaultPort = 80;

    public static int getDomainDefaultPort() {
        return domainDefaultPort;
    }

    /**
     * 默认端口
     */
    private static int domainPort = domainDefaultPort;

    public static void setDomainPort(int value) {
        domainPort = value;
    }

    public static int getDomainPort() {

        try {
            // 如果没设置过端口，自动设置第一个端口
            if (domainPort == ConstantInterFace.getDomainDefaultPort()
                    && !UtilitySecurity.isEmpty(UtilityAppConfig.getInstant().ports))
                setDomainPort(UtilityAppConfig.getInstant().ports.get(0));
        } catch (Exception ex) {
            UtilityException.catchException(ex);
        }

        return domainPort;
    }

    /**
     * 接口domain
     */
    private static String INTERFACE_DOMAIN = "http://yuenov.com";

    /**
     * 获取域名全路径
     */
    public static String getInterfaceDomain() {
        return INTERFACE_DOMAIN + ":" + +getDomainPort() + "/app/";
    }

    /**
     * 获取主页地址
     */
    public static String getUrlDomain() {
        return INTERFACE_DOMAIN + ":" + +getDomainPort();
    }

    /**
     * 图片domain
     */
    private static String IMAGESTART = "http://pt.yuenov.com";

    /**
     * 获取域名全路径
     */
    public static String getImageDomain() {
        return IMAGESTART + ":" + getDomainPort();
    }
}