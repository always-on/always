package edu.wpi.always.cm.perceptors.physical.speech;

import java.util.*;
import java.util.concurrent.*;

import javax.jms.*;
import javax.jms.Message;

import org.apache.activemq.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.perceptors.async.*;

public abstract class JMSMessagePerceptor<T extends Perception> implements MessageListener,Perceptor<T>, BufferablePerceptor<T>, AsyncPerceptor<T> {
	private ConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Topic topic;
	private MessageConsumer consumer;
	public JMSMessagePerceptor(String url, String topicName){
		try{
			connectionFactory = new ActiveMQConnectionFactory(url);
			connection = connectionFactory.createConnection();
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			topic = session.createTopic(topicName);
			consumer = session.createConsumer(topic);
			consumer.setMessageListener(this);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
	}


	private volatile T latest;
	@Override
	public T getLatest() {
		return latest;
	}

	@Override
	public void onMessage(Message message) {
		try {
			if (message instanceof TextMessage) {
				TextMessage textMessage = (TextMessage) message;
				latest = handleMessage(textMessage.getText());
				firePerceptorListeners(latest);
				bufferManager.pushPerception(latest);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	public abstract T handleMessage(String content);

	
	private final PerceptorBufferManager<T> bufferManager = new PerceptorBufferManager<T>();
	@Override
	public PerceptorBuffer<T> newBuffer() {
		return bufferManager.newBuffer();
	}

	
	private final List<AsyncPerceptorListener<T>> listeners = new CopyOnWriteArrayList<AsyncPerceptorListener<T>>();
	@Override
	public void addPerceptorListener(AsyncPerceptorListener<T> listener) {
		listeners.add(listener);
	}
	@Override
	public void removePerceptorListener(AsyncPerceptorListener<T> listener) {
		listeners.remove(listener);
	}
	protected void firePerceptorListeners(T perception){
		for(AsyncPerceptorListener<T> listener:listeners)
			listener.onPerception(perception);
	}
}
