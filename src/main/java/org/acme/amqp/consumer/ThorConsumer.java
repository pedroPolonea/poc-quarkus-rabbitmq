package org.acme.amqp.consumer;

import com.rabbitmq.client.Channel;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.acme.amqp.config.ConsumerTemplate;
import org.acme.amqp.config.QueueEnumConfig;

import java.nio.charset.StandardCharsets;

public class ThorConsumer extends ConsumerTemplate {
    private static final Logger log = LoggerFactory.getLogger(ThorConsumer.class);

    public ThorConsumer(Channel channel) {
        super(channel);
        setQueueEnumConfig(QueueEnumConfig.THOR);
    }

    @Override
    public void processMessage(byte[] body) {
        log.info("ThorConsumer: " + new String(body, StandardCharsets.UTF_8));
    }
}
