package lab03.message.pubsub;

import labxx.common.settings.CommonSettings;

import javax.jms.*;

public class NormalConsumerProblem {
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

    //Normal Consumer
    Thread consumer = new Thread() {
      @Override
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext()) {
          JMSConsumer consumer = jmsContext.createConsumer(defaultTopic);
          System.out.println(consumer.receive().getBody(String.class));
          Thread.sleep(2000);
          consumer.close();
          consumer = jmsContext.createConsumer(defaultTopic);
          for (int i = 1; i < 6; i++) {
            System.out.println(consumer.receive().getBody(String.class));
          }
        } catch (JMSException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    publisher.start();
    consumer.start();
  }
}
