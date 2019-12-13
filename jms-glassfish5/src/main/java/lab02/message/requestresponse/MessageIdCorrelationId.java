package lab02.message.requestresponse;

import labxx.common.settings.CommonSettings;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

// MessageId and CorreclationId test
public class MessageIdCorrelationId {
  public static void main(String[] args) throws JMSException {
    ConnectionFactory connectionFactory = CommonSettings.getConnectionFactory();
    Queue queue = CommonSettings.getDefaultQueue();
    Queue replyQueue = CommonSettings.getDefaultReplyQueue();

    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      Map<String, Message> messageMap = new HashMap<>();

      TextMessage message = jmsContext.createTextMessage("Order placed successfully");
      message.setJMSReplyTo(replyQueue);
      producer.send(queue, message);
      messageMap.put(message.getJMSMessageID(), message);

      message = jmsContext.createTextMessage("Another order placed");
      message.setJMSReplyTo(replyQueue);
      jmsContext.createProducer().send(queue, message);
      messageMap.put(message.getJMSMessageID(), message);


      //Receive message
      JMSConsumer consumer = jmsContext.createConsumer(queue);
      TextMessage receivedMsg = (TextMessage) consumer.receive();
      System.out.println("Received Message: "+receivedMsg.getText());

      //Reply Message
      TextMessage replyMessage = jmsContext.createTextMessage("Order Acknowledged");
      replyMessage.setJMSCorrelationID(receivedMsg.getJMSMessageID());
      jmsContext.createProducer().send(receivedMsg.getJMSReplyTo(), replyMessage);

      //
    }
  }
}
