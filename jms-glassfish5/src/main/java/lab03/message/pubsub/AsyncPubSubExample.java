package lab03.message.pubsub;

import labxx.common.settings.CommonSettings;

import javax.jms.*;

public class AsyncPubSubExample {
  private static ConnectionFactory connectionFactory = null;
  private static Topic defaultTopic = null;

  static {
    connectionFactory = CommonSettings.getConnectionFactory();
    defaultTopic = CommonSettings.getDefautTopic();
  }

  public static void main(String[] args) throws InterruptedException {

    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      JMSConsumer consumer = jmsContext.createConsumer(defaultTopic);
      consumer.setMessageListener(msg -> {
        try {
          System.out.println(msg.getBody(String.class));
        } catch (JMSException e) {
          e.printStackTrace();
        }
      });
      for (int i = 1; i < 7; i++) {
        producer.send(defaultTopic, "Message " + i);
      }
      Thread.sleep(1000);
      consumer.close();
    }
  }
}
