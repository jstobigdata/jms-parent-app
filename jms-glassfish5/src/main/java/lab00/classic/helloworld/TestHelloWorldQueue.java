package lab00.classic.helloworld;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TestHelloWorldQueue {
  public static void main(String[] args) throws NamingException {
    InitialContext initialContext = null;

    try {
      initialContext = new InitialContext();

      //Step-1 Create ConnectionFactory
      ConnectionFactory connectionFactory
          = (ConnectionFactory) initialContext.lookup("jms/__defaultConnectionFactory");

      //Step-2 Create connection
      Connection connection = connectionFactory.createConnection();

      //Step-3 Create Session
      //Any number of sessions can be created from a connection
      Session session = connection.createSession();

      //Step-4 Get the Queue
      Queue queue = (Queue) initialContext.lookup("jms/PTPQueue");

      //Step-5a Create the message Producer
      MessageProducer messageProducer = session.createProducer(queue);
      //Step-6a Create the Text Message
      TextMessage textMessage = session.createTextMessage("Test message - Hello");
      //Step-7a Send the message
      messageProducer.send(textMessage);

      //Step-5b Create the message consumer
      MessageConsumer messageConsumer = session.createConsumer(queue);
      //Step-6b Start the connection
      connection.start(); //to start delivery of the message
      //step-7b Receive the message
      TextMessage message = (TextMessage) messageConsumer.receive();
      System.out.println(message.getText());
    } catch (NamingException | JMSException ex) {
      ex.printStackTrace();
    } finally {
      if (initialContext != null) {
        initialContext.close();
      }
    }
  }
}
