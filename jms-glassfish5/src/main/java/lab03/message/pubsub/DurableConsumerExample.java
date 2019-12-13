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
          producer.send(defaultTopic, "Update 1");
          producer.send(defaultTopic, "Update 2");
          producer.send(defaultTopic, "Update 3");
          producer.send(defaultTopic, "Update 4");
          producer.send(defaultTopic, "Update 5");
          producer.send(defaultTopic, "Update 6");
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    //Shared Consumer
    Thread sharedConsumer = new Thread() {
      @Override
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext()) {
          JMSConsumer sharedConsumer1 = jmsContext.createSharedConsumer(defaultTopic, "sharedSubscriber");
          JMSConsumer sharedConsumer2 = jmsContext.createSharedConsumer(defaultTopic, "sharedSubscriber");
          for (int i = 0; i < 3; i++) {
            System.out.println("Shared Consumer1: " + sharedConsumer1.receive().getBody(String.class));
            System.out.println("Shared Consumer2: " + sharedConsumer2.receive().getBody(String.class));
          }
          Thread.sleep(3000);
          sharedConsumer1.close();
          sharedConsumer2.close();
        } catch (JMSException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    publisher.start();
    sharedConsumer.start();
  }
}