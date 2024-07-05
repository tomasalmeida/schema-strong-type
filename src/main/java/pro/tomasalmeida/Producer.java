package pro.tomasalmeida;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.tomasalmeida.Client;
import pro.tomasalmeida.ClientId;
import pro.tomasalmeida.common.PropertiesLoader;

import java.io.IOException;
import java.util.Properties;

public class Producer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    private final KafkaProducer<String, Client> clientKafkaProducer;
    
    public Producer() throws IOException {
        Properties properties = PropertiesLoader.load("client.properties");
        clientKafkaProducer = new KafkaProducer<>(properties);
    }

    public void createEvents() {
        try {
            produceClient("u-123ab", "a", "Dias Almeida", "ES");
            produceClient("123ab", "Tomas", "Dias Almeida", "ES");
            produceClient("u-123", "Tomas", "Dias Almeida", "ES");
            produceClient("u-abc", "Tomas", "Dias Almeida", "ES");
            clientKafkaProducer.close();
        } catch (Exception e) {
            LOGGER.error("Ops", e);
        }

    }

    private void produceClient(String id, String firstName, String lastName, String countryCode) throws InterruptedException {
        try {
            Client client = new Client(new ClientId(id), firstName, lastName, countryCode);

            LOGGER.info("Sending client {}", client);
            ProducerRecord<String, Client> userRecord = new ProducerRecord<>("client", client);
            clientKafkaProducer.send(userRecord);
        } catch (SerializationException serializationException) {
            LOGGER.error("Unable to serialize client: {}", serializationException.getCause().getMessage());
        }
        LOGGER.info("================");
        Thread.sleep(1000);

    }

    public static void main(final String[] args) throws IOException {
        Producer producerRunner = new Producer();
        producerRunner.createEvents();
    }
}
