package lab06.message.acknowledgement;

import labxx.common.settings.CommonSettings;
import labxx.common.settings.EmptyDestinations;

import javax.jms.*;

public class DupsOkAcknowledgeExample {
  public static void main(String[] args) {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();
    //EmptyDestinations.empty(queue);

    Thread messageproducer = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext()) {
          JMSProducer producer = jmsContext.createProducer();
          //Send the message
          Message message = jmsContext.createTextMessage("This is an DUPS_OK_ACKNOWLEDGE message");
          producer.send(queue, message);
        }
      }
    };

    Thread messageConsumer = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext(JMSContext.DUPS_OK_ACKNOWLEDGE)) {
          JMSConsumer consumer = jmsContext.createConsumer(queue);
          TextMessage msg = (TextMessage) consumer.receive();
          System.out.println("Received message: " + msg.getText());
        } catch (JMSException e) {
          e.printStackTrace();
        }
      }
    };

    messageConsumer.start();
    messageproducer.start();
  }
}
