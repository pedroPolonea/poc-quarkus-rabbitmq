package org.acme.amqp.consumer;

import com.rabbitmq.client.Channel;
import org.acme.amqp.config.ConsumerTemplate;
import org.acme.amqp.config.QueueEnumConfig;
import org.acme.amqp.config.ChannelInfo;

import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class ThorConsumer extends ChannelInfo implements ConsumerTemplate {
    @Override
    public QueueEnumConfig getQueue() {
        return QueueEnumConfig.THOR;
    }

    @Override
    public Channel getChannel() {
        return getChannel();
    }

    @Override
    public void processMessage(byte[] body) {
        log.info("ThorConsumer: " + new String(body, StandardCharsets.UTF_8));
    }
}
