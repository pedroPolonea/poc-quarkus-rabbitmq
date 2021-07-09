package org.acme.amqp.consumer;

import com.rabbitmq.client.Channel;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.acme.amqp.config.ConsumerTemplate;
import org.acme.amqp.config.QueueEnumConfig;

import java.nio.charset.StandardCharsets;

public class SampleConsumer extends ConsumerTemplate {
    private static final Logger log = LoggerFactory.getLogger(SampleConsumer.class);
    public SampleConsumer(Channel channel) {
        super(channel);
        setQueueEnumConfig(QueueEnumConfig.SAMPLE);
    }

    @Override
    public void processMessage(byte[] body) {
        log.info("SampleConsumer: " + new String(body, StandardCharsets.UTF_8));
    }
}
