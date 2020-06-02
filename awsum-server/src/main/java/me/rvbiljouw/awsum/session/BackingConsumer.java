package me.rvbiljouw.awsum.session;

import com.rabbitmq.client.*;
import me.rvbiljouw.awsum.response.MessageWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author rvbiljouw
 */
@Component
@PropertySource(value = "classpath:server.properties", ignoreResourceNotFound = true)
public class BackingConsumer implements Runnable {
    private final ConnectionHandler connectionHandler;
    private final ConnectionFactory amqpConnectionFactory;
    private final String inboundExchangeName;

    private Connection connection;
    private Channel channel;

    public BackingConsumer(
            ConnectionHandler connectionHandler,
            ConnectionFactory amqpConnectionFactory,
            TaskExecutor sessionTaskExecutor,
            @Value("${rabbitmq.inboundExchangeName:undefined}")
                    String inboundExchangeName) {
        this.connectionHandler = connectionHandler;
        this.amqpConnectionFactory = amqpConnectionFactory;
        this.inboundExchangeName = inboundExchangeName;

        sessionTaskExecutor.execute(this);
    }

    public void run() {
        try {
            System.out.println("inbuond exchange name is " + inboundExchangeName);
            connection = amqpConnectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(inboundExchangeName, "fanout", true);
            final AMQP.Queue.DeclareOk queue = channel.queueDeclare();
            channel.queueBind(queue.getQueue(), inboundExchangeName, "inbound");
            channel.basicConsume(queue.getQueue(), true, createStatelessConsumer());
        } catch (TimeoutException | IOException e) {
            e.printStackTrace();
        }
    }

    private void onMessageReceived(MessageWrapper wrapper) {

    }

    private DefaultConsumer createStatelessConsumer() {
        return new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                final String bodyAsString = new String(body);
            }
        };
    }


}
