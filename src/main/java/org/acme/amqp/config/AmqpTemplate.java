package org.acme.amqp.config;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import io.quarkiverse.rabbitmqclient.RabbitMQClient;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.acme.amqp.consumer.SampleConsumer;
import org.acme.amqp.consumer.ThorConsumer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static org.acme.amqp.config.AmqpUtil.X_DLQ_K;
import static org.acme.amqp.config.AmqpUtil.X_QUEUE_ROUTER;

@ApplicationScoped
public class AmqpTemplate {

    private static final Logger log = LoggerFactory.getLogger(AmqpTemplate.class);

    @Inject
    RabbitMQClient rabbitMQClient;

    private Channel channel;

    public void onApplicationStart(@Observes StartupEvent event) throws IOException {
        // on application start prepare the queus and message listener
        setupQueues();
        //setupReceiving();
    }

    private void setupReceiving() throws IOException {
        Arrays.asList(new SampleConsumer(channel), new ThorConsumer(channel)).forEach(consumer -> {
            try {
                channel.basicConsume(consumer.getQueueEnumConfig().getNameQueue(), true, consumer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupQueues() {
        try {
            // create a connection
            Connection connection = rabbitMQClient.connect();
            // create a channel
            channel = connection.createChannel();
            // declare exchanges and queues
            createQueueEnum();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void createQueueEnum(){
        Stream.of(QueueEnumConfig.values()).forEach(queue -> {
            try {
                // Exchange
                channel.exchangeDeclare(queue.getExchange(), queue.getBuiltinExchange(), queue.isDureble());

                // Queue
                channel.queueDeclare(queue.getNameQueue(), queue.isDureble(), queue.isExclusive(), queue.isAuteDelete(), queue.getArgs(false));
                channel.queueBind(queue.getNameQueue(), queue.getExchange(), X_QUEUE_ROUTER);

                // DLQ
                if (queue.isDlq()) {
                    channel.queueDeclare(queue.getNameDlq(), true, queue.isExclusive(), queue.isAuteDelete(), queue.getArgs(queue.isDlq()));
                    channel.queueBind(queue.getNameDlq(), queue.getExchange(), X_DLQ_K);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void send(final String message, final QueueEnumConfig enumConfig) {
        final AMQP.BasicProperties basicProperties = new AMQP.BasicProperties.Builder()
                .headers(Map.of(
                        "x-retries", 2,
                        "x-count", 1,
                        "x-retry", 2,
                        "x-retry-limit",2,
                        "x-delay", 5000L
                ))
                //.expiration("4000")
                .build();

        try {
            // send a message to the exchange
            channel.basicPublish(enumConfig.getExchange(), X_QUEUE_ROUTER, basicProperties, message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


}
