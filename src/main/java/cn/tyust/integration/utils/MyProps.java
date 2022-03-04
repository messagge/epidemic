package cn.tyust.integration.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix="system")
public class MyProps {
    public String url;
    public String getUrl() {
        return url;
    }
    public MyProps setUrl(String url) {
        this.url = url;
        return this;
    }
}
