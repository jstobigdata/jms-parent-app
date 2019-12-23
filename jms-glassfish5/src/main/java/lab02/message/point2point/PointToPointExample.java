package lab02.message.point2point;

import labxx.common.settings.CommonSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.jms.*;
import java.util.Enumeration;

public class PointToPointExample {
  private static ConnectionFactory connectionFactory = null;
  private static Queue queue = null;
  private static Queue replyQueue = null;

  @BeforeAll
  public static void setup() {
    connectionFactory = CommonSettings.getConnectionFactory();
    queue = CommonSettings.getDefaultQueue();
    replyQueue = CommonSettings.getDefaultReplyQueue();
  }

  @Test
  public void testQueueBrowser() throws JMSException {
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      TextMessage message1 = jmsContext.createTextMessage("Start your day with a glass of Water!");
      TextMessage message2 = jmsContext.createTextMessage("Remember to do 10 mins stretching");

      producer.send(queue, message1);
      producer.send(queue, message2);

      QueueBrowser qBrowser = jmsContext.createBrowser(queue);
      Enumeration msgEnum = qBrowser.getEnumeration();
      while (msgEnum.hasMoreElements()) {
        TextMessage browsedMsg = (TextMessage) msgEnum.nextElement();
        System.out.println("Browsed message: " + browsedMsg.getText());
      }

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      for (int i = 0; i < 2; i++) {
        System.out.println("Received message: " + consumer.receiveBody(String.class));
      }
    }
  }

  @Test
  public void testTemporaryQueue() throws JMSException{
    try(JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();

      //Use temporary Queue to send and receive messages.
      TemporaryQueue temporaryQueue = jmsContext.createTemporaryQueue();
      TextMessage message = jmsContext.createTextMessage("Sender message - Hi there!");
      message.setJMSReplyTo(temporaryQueue);
      producer.send(queue, message);
      System.out.println(message.getJMSMessageID());

      //Message received
      JMSConsumer consumer = jmsContext.createConsumer(queue);
      TextMessage messageReceived = (TextMessage) consumer.receive();
      System.out.println(messageReceived.getText());

      //Reply
      JMSProducer replyProducer = jmsContext.createProducer();
      TextMessage replyMessage = jmsContext.createTextMessage("Reply message - Hi, all well here!");
      replyMessage.setJMSCorrelationID(messageReceived.getJMSMessageID());
      replyProducer.send(messageReceived.getJMSReplyTo(), replyMessage);

      //Receive and process reply
      JMSConsumer replyConsumer = jmsContext.createConsumer(temporaryQueue);
      System.out.println( replyConsumer.receiveBody(String.class));
      TextMessage replyReceived = (TextMessage) replyConsumer.receive();
      System.out.println(replyReceived.getJMSCorrelationID());
      //temporaryQueue.delete();
    }
  }

  @Test
  public void testAsyncConsumer() throws InterruptedException {
    try(JMSContext jmsContext = connectionFactory.createContext()){
      JMSConsumer consumer = jmsContext.createConsumer(queue);
      consumer.setMessageListener(message -> {
        try {
          System.out.println(message.getBody(String.class));
        } catch (JMSException e) {
          e.printStackTrace();
        }
      });

      JMSProducer producer = jmsContext.createProducer().send(queue, "Message 1");
      producer.send(queue, "Message 2");
      producer.send(queue, "Message 3");

      //Close the Consumer
      consumer.close();
    }
  }

  //Test request response using another Queue and CorrelationID
  /*@Test
  public void testRequestResponse() throws JMSException{
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();

      //Step1 - Send a Request
      TextMessage message = jmsContext.createTextMessage("Hi there, what's your name");
      message.setJMSReplyTo(replyQueue);
      producer.send(queue, message);

      //Step2 - Read the request
      JMSConsumer consumer = jmsContext.createConsumer(queue);
      TextMessage requestMsg = (TextMessage) consumer.receive();
      System.out.println("Request received: " + requestMsg.getText());

      //Step3 - Prepare and send the response
      JMSProducer replyProducer = jmsContext.createProducer();
      TextMessage msg = jmsContext.createTextMessage("Hello, I am John");
      msg.setJMSCorrelationID(requestMsg.getJMSMessageID());
      replyProducer.send(replyQueue, msg);//Remember to create a new Producer.

      //Step4 - Read the response
      JMSConsumer resConsumer = jmsContext.createConsumer(replyQueue);
      TextMessage replyMsg = (TextMessage) resConsumer.receive();
      System.out.println("Reply received: " + replyMsg.getText());
      System.out.println(replyMsg.getJMSCorrelationID());
      System.out.println(message.getJMSMessageID());
    }
  }

  @Test
  public void testReply() throws JMSException{
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSConsumer resConsumer = jmsContext.createConsumer(replyQueue);
      TextMessage replyMsg = (TextMessage) resConsumer.receive(5000);
      System.out.println("Reply received: " + replyMsg.getText());
      System.out.println(replyMsg.getJMSCorrelationID());
      //System.out.println(message.getJMSMessageID());
    }

    }*/

}
