package lab03.message.pubsub;

import labxx.common.settings.CommonSettings;

import javax.jms.*;

public class DurableConsumerExample {
  private static ConnectionFactory connectionFactory = null;
  private static Topic defaultTopic = null;

  static {
    connectionFactory = CommonSettings.getConnectionFactory();
    defaultTopic = CommonSettings.getDefautTopic();
  }

  public static void main(String[] args) {
    Thread publisher = new Thread() {
      @Override
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext()) {
          JMSProducer producer = jmsContext.createProducer();
          Thread.sleep(1000);
          for (int i = 1; i < 7; i++) {
            producer.send(defaultTopic, "Update " + i);
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    //Durable Consumer
    Thread durableConsumer = new Thread() {
      @Override
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext()) {
          jmsContext.setClientID("exampleApp");
          JMSConsumer consumer = jmsContext.createDurableConsumer(defaultTopic, "logConsumer");
          System.out.println(consumer.receive().getBody(String.class));
          Thread.sleep(2000);
          consumer.close();
          consumer = jmsContext.createDurableConsumer(defaultTopic, "logConsumer");
          for (int i = 1; i < 6; i++) {
            System.out.println(consumer.receive().getBody(String.class));
          }
        } catch (JMSException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    publisher.start();
    durableConsumer.start();
  }
}