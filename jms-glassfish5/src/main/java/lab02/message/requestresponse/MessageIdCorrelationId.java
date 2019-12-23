package lab02.message.requestresponse;

import labxx.common.settings.CommonSettings;
import org.junit.jupiter.api.Test;

import javax.jms.*;

// MessageId and CorrelationId test
public class MessageIdCorrelationId {
  public static void main(String[] args) throws JMSException, InterruptedException {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();
    Queue replyQueue = CommonSettings.getDefaultReplyQueue();

    try (JMSContext jmsContext = connectionFactory.createContext()) {
      //Message listener on replyQueue
      JMSConsumer replyConsumer = jmsContext.createConsumer(replyQueue);
      replyConsumer.setMessageListener(msg -> {
        try {
          System.out.println("Reply message: " + msg.getBody(String.class));
          System.out.println("Reply MessageID: " + msg.getJMSMessageID());
          System.out.println("Reply CorrelationID: " + msg.getJMSCorrelationID());
        } catch (JMSException e) {
          e.printStackTrace();
        }
      });

      //Message1
      JMSProducer producer = jmsContext.createProducer();
      TextMessage message = jmsContext.createTextMessage("Order placed successfully");
      message.setJMSReplyTo(replyQueue);
      producer.send(queue, message);
      System.out.println("Message1 " + message.getJMSMessageID());

      //Receive Message
      JMSConsumer consumer = jmsContext.createConsumer(queue);
      TextMessage receivedMsg = (TextMessage) consumer.receive();
      System.out.println("Message received: " + receivedMsg.getText());

      //Reply Message
      TextMessage replyMessage = jmsContext.createTextMessage("Order Acknowledged");
      replyMessage.setJMSCorrelationID(receivedMsg.getJMSMessageID());
      jmsContext.createProducer().send(receivedMsg.getJMSReplyTo(), replyMessage);

      //Remember to close Otherwise will throw Exception
      replyConsumer.close();
    }
  }

  /*@Test
  public void test() {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();
    Queue replyQueue = CommonSettings.getDefaultReplyQueue();

    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSConsumer consumer = jmsContext.createConsumer(replyQueue);
      for (int i = 0; i < 100; i++) {
        System.out.println(consumer.receiveBody(String.class));
      }
    }
  }*/
}
