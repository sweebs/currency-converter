package me.sweby.currency.route;

import me.sweby.currency.model.ConversionResult;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static me.sweby.currency.model.ConversionConstants.AMOUNT;
import static me.sweby.currency.model.ConversionConstants.EUR;
import static me.sweby.currency.model.ConversionConstants.FROM;
import static me.sweby.currency.model.ConversionConstants.TARGET_CURRENCY;
import static me.sweby.currency.model.ConversionConstants.TO;

@Component
public class CurrencyConverterRoute extends RouteBuilder {

    @Value("${target.uri}")
    private String uri;

    @Value("${target.apiKey}")
    private String apiKey;

    @Override
    public void configure() throws Exception {
        from("direct:convert")
                .routeId("conversionRoute")
                .log(LoggingLevel.INFO, "Start ConversionRoute")
                .setProperty(FROM, header(FROM))
                .setProperty(TO, header(TO))
                .setProperty(AMOUNT, header(AMOUNT))
                .removeHeaders("*")
                .choice()
                .when(exchangeProperty(FROM).isEqualToIgnoreCase(EUR))
                .setProperty(TARGET_CURRENCY, simple("${property.to}"))
                .otherwise()
                .setProperty(TARGET_CURRENCY, simple("${property.from}"))
                .end()
                .setHeader(Exchange.HTTP_QUERY, simple("access_key=".concat(apiKey).concat("&symbols=${property.targetCurrency}")))
                .to(uri).id("apiCall").description("Calling the API")
                .bean(ConversionResult.class, "build")
                .marshal()
                .json(JsonLibrary.Jackson)
                .log(LoggingLevel.INFO, "End ConversionRoute")
                .end();

    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
