package lab03.message.pubsub;

import labxx.common.settings.CommonSettings;

import javax.jms.*;

public class SimplePubSubExample {
  private static ConnectionFactory connectionFactory = null;
  private static Topic defaultTopic = null;

  static {
    connectionFactory = CommonSettings.getConnectionFactory();
    defaultTopic = CommonSettings.getDefautTopic();
  }

  public static void main(String[] args) {
    Thread publisher = new Thread(){
      @Override
      public void run(){
        try(JMSContext jmsContext = connectionFactory.createContext()) {
          Thread.sleep(1000);
          JMSProducer producer = jmsContext.createProducer();
          TextMessage message = jmsContext.createTextMessage("World needs to worry about the Climate changes");
          producer.send(defaultTopic, message);
        } catch (InterruptedException ex){
          ex.printStackTrace();
        }
      }
    };

    Thread subscriber1 = new Thread(){
      @Override
      public void run(){
        try(JMSContext jmsContext = connectionFactory.createContext()) {
          JMSConsumer consumer = jmsContext.createConsumer(defaultTopic);
          System.out.println("Message received: " + consumer.receive().getBody(String.class));
        } catch (JMSException e){
          e.printStackTrace();
        }
      }
    };

    Thread subscriber2 = new Thread(subscriber1);

    publisher.start();
    subscriber1.start();
    subscriber2.start();
  }
}
