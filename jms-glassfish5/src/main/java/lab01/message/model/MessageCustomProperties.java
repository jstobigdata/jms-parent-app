package lab01.message.model;

import labxx.common.settings.CommonSettings;
import org.junit.jupiter.api.Test;

import javax.jms.*;
import java.util.Enumeration;
import java.util.UUID;

public class MessageCustomProperties {

  @Test
  public void test()
      throws JMSException {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();

    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      TextMessage message = jmsContext.createTextMessage("Message with a custom property");
      message.setBooleanProperty("priorityUser", true);
      message.setStringProperty("authToken", UUID.randomUUID().toString());
      producer.send(queue, message);

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      //Delay for testing purpose Only
      TextMessage textMsg = (TextMessage) consumer.receive(1000);
      Enumeration customProperties = textMsg.getPropertyNames();
      while (customProperties.hasMoreElements()) {
        System.out.println(customProperties.nextElement());
      }
      System.out.println("priorityUser: " + textMsg.getBooleanProperty("priorityUser"));
      System.out.println("authToken: " + textMsg.getStringProperty("authToken"));
      System.out.println("Received message: " + textMsg.getText());
    }
  }
}