package org.acme.amqp.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.acme.amqp.config.AmqpUtil.X_COUNT;
import static org.acme.amqp.config.AmqpUtil.X_DLQ_K;
import static org.acme.amqp.config.AmqpUtil.X_RETRY;

public interface ConsumerTemplate extends Consumer {
    static final Logger log = LoggerFactory.getLogger(ConsumerTemplate.class);

    QueueEnumConfig getQueue();

    void processMessage(final byte[] body) throws Exception;

    Channel getChannel();

    void setChannel(final Channel channel);

    @Override
    default void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (!envelope.getRoutingKey().equalsIgnoreCase(X_DLQ_K)){
            try {
                log.info("Received: " + new String(body, StandardCharsets.UTF_8));
                processMessage(body);
            } catch (Exception e){
                if (isDeath(properties.getHeaders())) {
                    getChannel().basicPublish(envelope.getExchange(), envelope.getRoutingKey(), incrementCount(properties), body);
                } else {
                    getChannel().basicPublish(envelope.getExchange(), X_DLQ_K, properties, body);
                }
            }
        }
    }

    private boolean isDeath(final Map<String, Object> headers){
        final Integer xCount = (Integer) headers.get(X_COUNT);
        final Integer xRetries = (Integer) headers.get(X_RETRY);

        return xCount < xRetries;
    }

    private AMQP.BasicProperties incrementCount(AMQP.BasicProperties properties){
        final Integer xCount = (Integer) properties.getHeaders().get(X_COUNT);
        properties.getHeaders().put(X_COUNT, xCount+1);

        return properties;
    }

    @Override
    default void handleConsumeOk(String consumerTag) {

    }

    @Override
    default void handleCancelOk(String consumerTag) {

    }

    @Override
    default void handleCancel(String consumerTag) throws IOException {

    }

    @Override
    default void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {

    }

    @Override
    default void handleRecoverOk(String consumerTag) {

    }
}
