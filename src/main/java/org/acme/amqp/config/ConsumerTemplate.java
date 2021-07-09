package org.acme.amqp.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.acme.amqp.config.AmqpUtil.X_COUNT;
import static org.acme.amqp.config.AmqpUtil.X_DLQ_K;
import static org.acme.amqp.config.AmqpUtil.X_RETRY;

public class ConsumerTemplate extends DefaultConsumer {
    private static final Logger log = LoggerFactory.getLogger(ConsumerTemplate.class);

    private QueueEnumConfig queueEnumConfig;

    public ConsumerTemplate(Channel channel) {
        super(channel);
    }

    public void processMessage(final byte[] body){}

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (!envelope.getRoutingKey().equalsIgnoreCase(X_DLQ_K)){
            try {
                log.info("Received: " + new String(body, StandardCharsets.UTF_8));
                processMessage(body);
            } catch (Exception e){
                if (isDeath(properties.getHeaders())) {
                    this.getChannel().basicPublish(envelope.getExchange(), envelope.getRoutingKey(), incrementCount(properties), body);
                } else {
                    this.getChannel().basicPublish(envelope.getExchange(), X_DLQ_K, properties, body);
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

    public QueueEnumConfig getQueueEnumConfig() {
        return queueEnumConfig;
    }

    public void setQueueEnumConfig(QueueEnumConfig queueEnumConfig) {
        this.queueEnumConfig = queueEnumConfig;
    }
}
