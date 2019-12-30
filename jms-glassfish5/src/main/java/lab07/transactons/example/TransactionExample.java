package lab07.transactons.example;

import labxx.common.settings.CommonSettings;
import labxx.common.settings.EmptyDestinations;

import javax.jms.*;

public class TransactionExample {
  public static void main(String[] args) {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();
    EmptyDestinations.empty(queue); //TODO - Use only if you need.

    Thread messageproducer = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {
          JMSProducer producer = jmsContext.createProducer();

          producer.send(queue, "This is a SESSION_TRANSACTED message");
          producer.send(queue, "Sending another message");
          //TODO - Comment and see the result, message is not delivered until committed
          sleep(5000);
          jmsContext.commit(); //Important

          //Next message is never delivered as it is rollback()
          producer.send(queue, "This message will not be delivered");
          jmsContext.rollback();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    Thread messageConsumer = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext(JMSContext.SESSION_TRANSACTED)) {
          JMSConsumer consumer = jmsContext.createConsumer(queue);
          consumer.setMessageListener(msg -> {
            try {
              System.out.println(msg.getBody(String.class));
            } catch (JMSException e) {
              e.printStackTrace();
            }
          });
          jmsContext.commit();
          Thread.sleep(6000);
          consumer.close();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    messageproducer.start();
    messageConsumer.start();
  }
}