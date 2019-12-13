package lab01.message.model;

import labxx.common.settings.CommonSettings;

import javax.jms.*;
import java.util.UUID;

public class MessageHeaderExample {
  public static void main(String[] args) throws JMSException {

    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();


    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      Queue replyQueue = jmsContext.createTemporaryQueue();

      producer.send(queue, "Test message Headers");
      producer.setJMSCorrelationID(UUID.randomUUID().toString());

      JMSConsumer consumer = jmsContext.createConsumer(queue);
       Message message = consumer.receive();

        System.out.println("Priority: " + message.getJMSPriority() + " #### Message: " + message.getBody(String.class));

    }
  }
}
