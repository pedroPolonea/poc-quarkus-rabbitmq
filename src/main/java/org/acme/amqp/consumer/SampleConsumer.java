package org.acme.amqp.consumer;

import org.acme.amqp.config.ConsumerTemplate;
import org.acme.amqp.config.QueueEnumConfig;
import org.acme.amqp.config.ChannelInfo;

import javax.enterprise.context.ApplicationScoped;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
public class SampleConsumer extends ChannelInfo implements ConsumerTemplate {


    @Override
    public QueueEnumConfig getQueue() {
        return QueueEnumConfig.SAMPLE;
    }

    @Override
    public void processMessage(byte[] body) throws Exception {
        final String s = new String(body, StandardCharsets.UTF_8);
        log.info("SampleConsumer: " + s);

        if(s.equalsIgnoreCase("s")){
            throw new Exception("ssss");
        }
    }

}
