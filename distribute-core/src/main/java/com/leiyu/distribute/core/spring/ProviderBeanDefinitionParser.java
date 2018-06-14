package com.leiyu.distribute.core.spring;

import com.leiyu.distribute.core.provider.ProviderFactoryBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.lang.Nullable;
import org.w3c.dom.Element;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.spring
 * @Description: Provider 标签解析
 * @Author: wanghao30
 * @Creation Date: 2018-06-06
 */
public class ProviderBeanDefinitionParser extends AbstractSingleBeanDefinitionParser{

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderBeanDefinitionParser.class);

    @Nullable
    @Override
    protected Class<?> getBeanClass(Element element) {
        return ProviderFactoryBean.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        try {
            String serviceItf = element.getAttribute("interface");
            String timeOut = element.getAttribute("timeout");
            String serverPort = element.getAttribute("serverPort");
            String ref = element.getAttribute("ref");
            String weight = element.getAttribute("weight");
            String workerThreads = element.getAttribute("workerThreads");
            String appKey = element.getAttribute("appKey");
            String groupName = element.getAttribute("groupName");

            builder.addPropertyValue("serverPort", Integer.parseInt(serverPort));
            builder.addPropertyValue("timeout", Integer.parseInt(timeOut));
            builder.addPropertyValue("serviceItf", Class.forName(serviceItf));
            builder.addPropertyReference("serviceObject", ref);
            builder.addPropertyValue("appKey", appKey);

            if (NumberUtils.isCreatable(weight)) {
                builder.addPropertyValue("weight", Integer.parseInt(weight));
            }
            if (NumberUtils.isCreatable(workerThreads)) {
                builder.addPropertyValue("workerThreads", Integer.parseInt(workerThreads));
            }
            if (StringUtils.isNotBlank(groupName)) {
                builder.addPropertyValue("groupName", groupName);
            }
        }catch (Exception e){

        }
    }
}
