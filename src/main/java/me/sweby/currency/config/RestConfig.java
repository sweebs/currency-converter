package me.sweby.currency.config;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig extends RouteBuilder {

    @Value("${rest.version}")
    private String version;
    @Value("${rest.title}")
    private String title;
    @Value("${rest.cors}")
    private String cors;
    @Value("${rest.contextPath}")
    private String contextPath;
    @Value("${rest.bindingMode}")
    private String bindingMode;

    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
                .skipBindingOnErrorCode(false)
                .apiContextPath("api-doc")
                .apiProperty("api.title", title)
                .apiProperty("api.version", version)
                .apiProperty("cors", cors)
                .apiContextRouteId("doc-api")
                .contextPath(contextPath)
                .bindingMode(RestBindingMode.valueOf(this.bindingMode));
    }
}
