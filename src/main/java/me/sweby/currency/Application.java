package me.sweby.currency;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class Application {

    @Value("${rest.contextPath}")
    private String contextPath;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean s = new ServletRegistrationBean(new CamelHttpTransportServlet(), "/".concat(contextPath)
                .concat("/*"));
        s.setName("CamelServlet");
        return s;
    }

}
