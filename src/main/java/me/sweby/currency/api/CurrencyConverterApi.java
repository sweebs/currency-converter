package me.sweby.currency.api;

import me.sweby.currency.model.ConversionResult;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

import static me.sweby.currency.model.ConversionConstants.AMOUNT;
import static me.sweby.currency.model.ConversionConstants.FROM;
import static me.sweby.currency.model.ConversionConstants.TO;

@Component
public class CurrencyConverterApi extends RouteBuilder {


    @Override
    public void configure() throws Exception {
        rest()
                .get("convert")
                .produces(MediaType.APPLICATION_JSON)
                .description("Converts from one currency to another")
                .param()
                .name(FROM)
                .type(RestParamType.query)
                .dataFormat("string")
                .required(true)
                .endParam()
                .param()
                .name(TO)
                .dataFormat("string")
                .required(true)
                .endParam()
                .param()
                .name(AMOUNT)
                .dataFormat("string")
                .required(true)
                .endParam()
                .responseMessage()
                .responseModel(ConversionResult.class)
                .code(200)
                .endResponseMessage()
                .route()
                .routeId("initConversion")
                .to("direct:convert")
                .endRest();

    }
}
