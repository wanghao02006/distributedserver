package com.leiyu.distribute.core.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.spring
 * @Description: 服务消费者自定义标签
 * @Author: wanghao30
 * @Creation Date: 2018-06-07
 */
public class ConsumerNamespaceHandler extends NamespaceHandlerSupport {


    @Override
    public void init() {
        registerBeanDefinitionParser("consumer",new ConsumerBeanDefinitionParser());
    }
}
