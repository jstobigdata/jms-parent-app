package lab06.message.acknowledgement;

import labxx.common.settings.CommonSettings;
import labxx.common.settings.EmptyDestinations;

import javax.jms.*;

public class ClientAcknowledgeExample {
  public static void main(String[] args) throws InterruptedException {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();
    //EmptyDestinations.empty(queue);

    Thread messageproducer = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext(JMSContext.CLIENT_ACKNOWLEDGE)) {
          JMSProducer producer = jmsContext.createProducer();
          //Send the message
          Message message = jmsContext.createTextMessage("This is a CLIENT_ACKNOWLEDGE message");
          producer.send(queue, message);
          message.acknowledge(); //This is Optional
        }catch (JMSException e) {
          e.printStackTrace();
        }
      }
    };

    Thread messageConsumer1 = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext(JMSContext.CLIENT_ACKNOWLEDGE)) {
          JMSConsumer consumer = jmsContext.createConsumer(queue);
          TextMessage msg = (TextMessage) consumer.receive(3000);
          System.out.println("Received message: " + msg.getText());
        } catch (JMSException e) {
          e.printStackTrace();
        }
      }
    };
    Thread messageConsumer2 = new Thread(messageConsumer1);

    Thread messageConsumer3 = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext(JMSContext.CLIENT_ACKNOWLEDGE)) {
          JMSConsumer consumer = jmsContext.createConsumer(queue);
          TextMessage msg = (TextMessage) consumer.receive();
          System.out.println("Received message: " + msg.getText());
          Thread.sleep(500);
          msg.acknowledge(); //Important
        } catch (JMSException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    messageproducer.start();
    messageConsumer1.start();
    messageConsumer3.start();

    Thread.sleep(1000);
    messageConsumer2.start();
  }
}