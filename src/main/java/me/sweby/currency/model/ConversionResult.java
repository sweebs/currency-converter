package me.sweby.currency.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.apache.camel.Exchange;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static me.sweby.currency.model.ConversionConstants.*;

@Value
public class ConversionResult implements Serializable {

    private final String fromCurrency;
    private final String toCurrency;
    private final Double fromAmount;
    private final Double toAmount;

    public static ConversionResult build(Exchange exchange) throws Exception {
        return new ConversionResult(exchange.getProperty(FROM, String.class),
                exchange.getProperty(TO, String.class),
                Double.valueOf(exchange.getProperty(AMOUNT, String.class)),
                calculateToAmount(exchange));
    }

    private static Double calculateToAmount(Exchange exchange) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.readValue(exchange.getIn().getBody(String.class), HashMap.class);
        exchange.getIn().setBody(result);
        Map<String, Object> rates = (Map) result.get(RATES);
        Double conversionRate = (Double) rates.get(exchange.getProperty(TARGET_CURRENCY, String.class));
        Double amountTo;
        if (exchange.getProperty(FROM, String.class).equalsIgnoreCase(EUR)) {
            amountTo = Double.valueOf(exchange.getProperty(AMOUNT, String.class)) * conversionRate;
        } else {
            amountTo = Double.valueOf(exchange.getProperty(AMOUNT, String.class)) / conversionRate;
        }
        BigDecimal bd = new BigDecimal(amountTo.toString());
        return bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}