package org.acme.resteasyjackson;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.MessageProperties;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@ApplicationScoped
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    @Inject
    RabbitMQClient rabbitMQClient;



    private Channel channel;

    public void onApplicationStart(@Observes StartupEvent event) {
        // on application start prepare the queus and message listener
        setupQueues();
        setupReceiving();
    }

    private void setupQueues() {
        try {
            // create a connection
            Connection connection = rabbitMQClient.connect();
            // create a channel
            channel = connection.createChannel();
            // declare exchanges and queues
            channel.exchangeDeclare("sample", BuiltinExchangeType.TOPIC, true);

            channel.queueDeclare("sample.queue", true, false, false, Map.of("x-dead-letter-exchange", "sample", "x-dead-letter-routing-key", "dlx_key", "x-message-ttl", 5000));
            channel.queueBind("sample.queue", "sample", "#");

            channel.queueDeclare("sample.dlq", false, false, false, null);
            channel.queueBind("sample.dlq", "sample", "dlx_key");

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void setupReceiving() {
        final DefaultConsumer defaultConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                if (!envelope.getRoutingKey().equalsIgnoreCase("dlx_key")){
                    try {


                        // just print the received message.
                        log.info("Received: " + new String(body, StandardCharsets.UTF_8));
                        //channel.basicAck(envelope.getDeliveryTag(), true);]
                        //channel.basicNack(envelope.getDeliveryTag(), false, false);
                        throw new Exception("xxx");
                    } catch (Exception e){
                        final Integer xDeath = (Integer) properties.getHeaders().get("x-death");
                        final Integer xRetries = (Integer) properties.getHeaders().get("x-retries");


                        if (xDeath < xRetries) {
                            properties.getHeaders().put("x-death", xDeath+1);
                            channel.basicPublish("sample", "#", properties, body);
                        } else {
                            channel.basicPublish("sample", "dlx_key", properties, body);
                        }
                    }
                }
            }
        };
        try {
            // register a consumer for messages
            channel.basicConsume("sample.queue", true, defaultConsumer);
        } catch (IOException e) {
            //throw new UncheckedIOException(e);

        }
    }

    public void send(String message) {
        final AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .headers(Map.of("x-retries", 5, "x-death", 1))
                .build();

        try {
            // send a message to the exchange
            channel.basicPublish("sample", "#", basicProperties, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
