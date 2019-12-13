package lab01.message.model;

import labxx.common.settings.CommonSettings;

import javax.jms.*;

public class MessageDelayDelivery {
  public static void main(String[] args) throws JMSException {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();

    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      producer.setDeliveryDelay(3000);//delivered after 3 seconds
      producer.send(queue, "Message with a delay");

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      TextMessage textMsg = (TextMessage) consumer.receive(5000);//Wait for 5 seconds
      System.out.println("Received message: " + textMsg.getText());
    }
  }
}
