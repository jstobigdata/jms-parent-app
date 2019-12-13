package lab01.message.model;

import labxx.common.settings.CommonSettings;

import javax.jms.*;

public class MessageExpirationTest {
  public static void main(String[] args) throws InterruptedException, JMSException {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();

    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      producer.setTimeToLive(2000);
      producer.send(queue, "This message will expire soon");

      Thread.sleep(5000);

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      TextMessage message = (TextMessage) consumer.receive(10000);
      System.out.println(message.getText());
    }
  }
}
