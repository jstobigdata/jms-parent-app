package lab01.message.model;

import labxx.common.settings.CommonSettings;
import javax.jms.*;

public class MessageDefaultPriorityTest {
  public static void main(String[] args) throws JMSException {

    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();

    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      String[] messages = {"Msg One", "Msg two", "Msg three", "Msg four", "Msg five"};

      producer.send(queue, messages[0]);
      producer.send(queue, messages[1]);
      producer.send(queue, messages[2]);
      producer.send(queue, messages[3]);
      producer.send(queue, messages[4]);

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      for (int i = 0; i < messages.length; i++) {
        Message message = consumer.receive();
        System.out.println("Default Priority: " + message.getJMSPriority() + " #### Message: " + message.getBody(String.class));
      }
    }
  }
}
