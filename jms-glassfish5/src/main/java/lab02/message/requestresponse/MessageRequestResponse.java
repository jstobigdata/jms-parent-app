package lab02.message.requestresponse;

import labxx.common.settings.CommonSettings;

import javax.jms.*;

public class MessageRequestResponse {
  public static void main(String[] args) throws JMSException, InterruptedException {

    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();
    Queue replyQueue = CommonSettings.getDefaultReplyQueue();

    Thread messageproducer = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext()) {
          JMSProducer producer = jmsContext.createProducer();
          //Create a temp Queue at the Source
          //Needs permission on Destination to create message
          //TemporaryQueue tempReplyQ = jmsContext.createTemporaryQueue();

          //Send the message
          Message message = jmsContext.createTextMessage("Order placed successfully");
          message.setJMSReplyTo(replyQueue);
          producer.send(queue, message);

          sleep(2000);
          JMSConsumer replyConsumer = jmsContext.createConsumer(replyQueue);
          TextMessage replyMessage = (TextMessage) replyConsumer.receive();
          System.out.println("Received reply: " + replyMessage.getText());
        } catch (JMSException | InterruptedException ex) {
          ex.printStackTrace();
        }
      }
    };

    Thread messageConsumer = new Thread() {
      public void run() {
        try (JMSContext jmsContext = connectionFactory.createContext()) {
          //Receive message
          Thread.sleep(1000);
          JMSConsumer consumer = jmsContext.createConsumer(queue);
          Message msg = consumer.receive();
          System.out.println("Received message: " + msg.getBody(String.class));

          //Reply message
          jmsContext.createProducer().send(msg.getJMSReplyTo(), "Order will be dispatched soon!");

        } catch (JMSException | InterruptedException e) {
          e.printStackTrace();
        }
      }
    };

    messageproducer.start();
    messageConsumer.start();

  }
}
