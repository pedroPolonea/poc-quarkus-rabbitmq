package org.acme.amqp.config;

import com.rabbitmq.client.BuiltinExchangeType;

import java.util.Map;

import static org.acme.amqp.config.AmqpUtil.X_DLQ;
import static org.acme.amqp.config.AmqpUtil.X_DLQ_K;
import static org.acme.amqp.config.AmqpUtil.X_DLQ_ROUTER;
import static org.acme.amqp.config.AmqpUtil.X_TTL;


public enum QueueEnumConfig {

    SAMPLE("sample", BuiltinExchangeType.TOPIC, true, false, false, true, true, 10000, 3),
    THOR("thor", BuiltinExchangeType.TOPIC, true, false, false, true, true, 10000, 3);

    private String exchange;
    private BuiltinExchangeType builtinExchange;
    private boolean durable;
    private boolean exclusive;
    private boolean autoDelete;
    private boolean retry;
    private boolean dlq;
    private int ttl;
    private int maxRetry;

    QueueEnumConfig(String exchange, BuiltinExchangeType builtinExchange, boolean durable, boolean exclusive, boolean autoDelete, boolean retry, boolean dlq, int ttl, int maxRetry) {
        this.exchange = exchange;
        this.builtinExchange = builtinExchange;
        this.durable = durable;
        this.exclusive = exclusive;
        this.autoDelete = autoDelete;
        this.retry = retry;
        this.dlq = dlq;
        this.ttl = ttl;
        this.maxRetry = maxRetry;
    }

    public String getExchange() {
        return exchange;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean isAuteDelete() {
        return autoDelete;
    }

    public boolean isRetry() {
        return retry;
    }

    public boolean isDlq() {
        return dlq;
    }

    public int getTtl() {
        return ttl;
    }

    public int getMaxRetry() {
        return maxRetry;
    }

    public BuiltinExchangeType getBuiltinExchange() {
        return builtinExchange;
    }

    public boolean isDureble() {
        return durable;
    }

    public String getNameQueue(){
        return exchange+".queue";
    }

    public String getNameRetry(){
        return exchange+".retry";
    }

    public String getNameDlq(){
        return exchange+".dlq";
    }

    public Map<String, Object> getArgs(final boolean isDql){
        if(isDql){
            return null;
        }
        return Map.of(X_DLQ, exchange,
                X_DLQ_ROUTER, X_DLQ_K,
                X_TTL, ttl);
    }
}
