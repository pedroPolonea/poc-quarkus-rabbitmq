package org.acme.amqp.config;

public final class AmqpUtil {
    public static final String X_DLQ = "x-dead-letter-exchange";
    public static final String X_DLQ_ROUTER = "x-dead-letter-routing-key";
    public static final String X_TTL = "x-message-ttl";
    public static final String X_DLQ_K = "dlq-key-default";
    public static final String X_DEATH = "x-death";
    public static final String X_RETRY = "x-retries";
    public static final String X_COUNT = "x-count";
    public static final String X_QUEUE_ROUTER = "#";

}
