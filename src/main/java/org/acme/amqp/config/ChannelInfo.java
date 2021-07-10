package org.acme.amqp.config;

import com.rabbitmq.client.Channel;

import java.util.Objects;

public class ChannelInfo {
    private Channel channel;

    public void setChannel(final Channel newChannel){
        if(Objects.isNull(channel)) {
            channel = newChannel;
        }
    }

    public Channel getChannel() {
        return channel;
    }
}
