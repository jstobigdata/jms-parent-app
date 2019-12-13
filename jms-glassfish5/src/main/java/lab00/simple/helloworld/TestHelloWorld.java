package lab00.simple.helloworld;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TestHelloWorld {
  public static void main(String[] args) {

    ConnectionFactory connectionFactory = null;
    Queue queue = null;
    try {

      InitialContext initialContext = new InitialContext();
      //Step-1 Create ConnectionFactory
      connectionFactory
          = (ConnectionFactory) initialContext.lookup("jms/__defaultConnectionFactory");

      //Step-2
      queue = (Queue) initialContext.lookup("jms/PTPQueue");
    } catch (NamingException e) {
      e.printStackTrace();
    }

    //Step-3
    try (JMSContext jmsContext = connectionFactory.createContext()) {

      //Step-4a
      TextMessage textMessage = jmsContext.createTextMessage("Message using JMS 2.0");
      JMSProducer jmsProducer = jmsContext.createProducer().send(queue, textMessage);

      //Step-4b
      TextMessage message = (TextMessage) jmsContext.createConsumer(queue).receive();
      System.out.println(message.getText());
    } catch (JMSException e) {
      e.printStackTrace();
    }
  }
}
