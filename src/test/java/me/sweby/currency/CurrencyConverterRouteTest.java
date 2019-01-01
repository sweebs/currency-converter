package me.sweby.currency;
import static me.sweby.currency.model.ConversionConstants.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sweby.currency.model.ConversionResult;
import me.sweby.currency.route.CurrencyConverterRoute;
import org.apache.camel.*;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

@Slf4j
public class CurrencyConverterRouteTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:datafixer")
    private MockEndpoint mockDatafixer;

    @Produce(uri = "direct:convert")
    private ProducerTemplate template;

    private static final String OUTPUT = "{\"success\":true,\"timestamp\":1546340346,\"base\":\"EUR\",\"date\":"
            + "\"2019-01-01\",\"rates\":{\"USD\":1.14877}}";




    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new RoutesBuilder() {
            @Override
            public void addRoutesToCamelContext(CamelContext context) throws Exception {
                CurrencyConverterRoute currencyConverterRoute = new CurrencyConverterRoute();
                currencyConverterRoute.setApiKey("key");
                currencyConverterRoute.setUri("http4://dummy.uri.com/doit?bridgeEndpoint=true");
                context.addRoutes(currencyConverterRoute);
            }
        };
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        context.getRouteDefinition("conversionRoute")
                .adviceWith(context, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                      weaveById("apiCall")
                              .replace()
                              .to("mock:datafixer");
                    }
                });
    }
    @Test
    public void testCurrencyConverterRoute() throws Exception {
        context.start();
        mockDatafixer.expectedMessageCount(1);
        mockDatafixer.whenAnyExchangeReceived(exchange -> {
            exchange.getIn().setBody(OUTPUT);
        });
        Exchange result = template.send("direct:convert", exchange -> {
          exchange.getIn().setHeader(FROM, EUR);
          exchange.getIn().setHeader(TO, "USD");
          exchange.getIn().setHeader(AMOUNT, "25");
        });

        String body = result.getIn().getBody(String.class);
        ObjectMapper mapper = new ObjectMapper();
        ConversionResult cr = mapper.readValue(body.getBytes(), ConversionResult.class);

        assertNotNull(cr.getToAmount());
    }

}
