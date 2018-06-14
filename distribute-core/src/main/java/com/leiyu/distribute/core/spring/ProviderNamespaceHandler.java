package com.leiyu.distribute.core.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.spring
 * @Description: 提供者自定义标签
 * @Author: wanghao30
 * @Creation Date: 2018-06-06
 */
public class ProviderNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("provider", new ProviderBeanDefinitionParser());
    }
}
