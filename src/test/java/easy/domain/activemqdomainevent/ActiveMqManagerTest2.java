package easy.domain.activemqdomainevent;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.Message;
import org.junit.Test;

import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

/**
 * Created by lixiaojing3 on 2017/1/6.
 */
public class ActiveMqManagerTest2 {
    @Test
    public void test2() throws Exception {
        ActiveMqManager activeMq = ActiveMqManagerFactory
                .createActiveMqManager(
                        "tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=0"
                );

        MessageProducer messageProducer = activeMq.createQueueProducer();

        Destination destination = new ActiveMQQueue("aa");

        TextMessage textMessage = new ActiveMQTextMessage();
        textMessage.setJMSDestination(destination);
        textMessage.setText("adfdaf");


        messageProducer.send(destination, textMessage);

    }
}

