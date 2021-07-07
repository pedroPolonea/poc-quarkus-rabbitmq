package org.acme.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;


public enum QueueEnumConfig {

    SAMPLE("sample", BuiltinExchangeType.TOPIC, false, false, false, true, true, 10000, 3);

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
}
