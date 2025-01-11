package com.example.monitor_app.consumers;

import com.example.monitor_app.dtos.DeviceDetailsDTO;
import com.example.monitor_app.entities.EnergyMeasurement;
import com.example.monitor_app.repositories.EnergyMeasurementRepository;
import com.example.monitor_app.services.DeviceService;
//import com.example.monitor_app.services.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class MonitorConsumer {

    @Autowired
    private EnergyMeasurementRepository energyMeasurementRepository;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorConsumer.class);
    private static final String ENERGY_QUEUE_NAME = "energy_data_queue";
    private static final String DEVICE_QUEUE_NAME = "device_queue";

    private double sumMeasurements = 0;
    private int count = 0;
    private UUID lastDeviceId = null;
    private long lastTimestamp = 0;

    public MonitorConsumer() {
        startEnergyDataConsumer();
        startDeviceQueueConsumer();
    }

    private void startEnergyDataConsumer() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(ENERGY_QUEUE_NAME, true, false, false, null);
            channel.basicQos(1); // Limităm la un mesaj simultan
            LOGGER.info("Waiting for messages from energy_data_queue...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                LOGGER.info("Received message from energy_data_queue: {}", message); // Afișăm mesajul primit
                consumeEnergyMessage(message);
            };
            channel.basicConsume(ENERGY_QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Failed to start energy data consumer: {}", e.getMessage());
        }
    }

    private void consumeEnergyMessage(String message) {
        try {
            // Parsăm mesajul JSON primit din coadă
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> json = objectMapper.readValue(message, Map.class);
            long timestamp = Long.parseLong(json.get("timestamp").toString());
            UUID deviceId = UUID.fromString(json.get("device_id").toString());
            double measurementValue = Double.parseDouble(json.get("measurement_value").toString());

            // Cream entitatea EnergyMeasurement pentru fiecare citire individuală
            EnergyMeasurement energyMeasurement = new EnergyMeasurement();
            energyMeasurement.setTimestamp(timestamp);
            energyMeasurement.setDeviceId(deviceId);
            energyMeasurement.setHourlyValue(measurementValue);

            // Salvăm fiecare citire în baza de date
            energyMeasurementRepository.save(energyMeasurement);
            LOGGER.info("Saved individual EnergyMeasurement time:{}, device:{}, value:{}",timestamp ,deviceId, measurementValue);
            messagingTemplate.convertAndSend("/topic/monitor", energyMeasurement);
            LOGGER.info("Sent message to /topic/monitor: {}", energyMeasurement);

            // Acumulăm valorile citite pentru a verifica suma
            sumMeasurements += measurementValue;
            count++;
            lastDeviceId = deviceId;
            lastTimestamp = timestamp;

            // După fiecare 10 citiri, verificăm suma
            if (count == 10) {
                // Găsim dispozitivul în baza de date pentru a obține valoarea `maximum_hourly_energy_consumption`
                DeviceDetailsDTO deviceDetails = deviceService.findDeviceById(deviceId);
                double maxHourlyEnergyConsumption = deviceDetails.getmaxHourlyEnergyConsumption();

                LOGGER.info("Comparing sum of last 10 measurements ({}) with maximum hourly energy consumption ({}) for device {}", sumMeasurements, maxHourlyEnergyConsumption, deviceId);

                // Compara suma masuratorilor cu valoarea `maximum_hourly_energy_consumption`
                if (sumMeasurements > maxHourlyEnergyConsumption) {
                    // Trimite o alerta prin WebSocket
                    String alertMessage = "Alert: Device " + deviceId + " has exceeded the maximum hourly energy consumption!";
                    //webSocketService.sendAlert(alertMessage);
                    messagingTemplate.convertAndSend("/topic/alerts", alertMessage);
                    LOGGER.info("Sent alert to /topic/alerts: {}", alertMessage);
                    LOGGER.warn(alertMessage);
                }
                // Resetam contorii
                sumMeasurements = 0;
                count = 0;
            }

        } catch (Exception e) {
            LOGGER.error("Error processing energy data message: {}", e.getMessage());
        }
    }

    private void startDeviceQueueConsumer() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(DEVICE_QUEUE_NAME, true, false, false, null);
            channel.basicQos(1); // Limităm la un mesaj simultan
            LOGGER.info("Waiting for messages from device_queue...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                LOGGER.info("Received message from device_queue: {}", message); // Log pentru mesaj primit
                consumeDeviceMessage(message);
                // Delay de 2 secunde
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    LOGGER.error("Thread sleep interrupted: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            };
            channel.basicConsume(DEVICE_QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (IOException | TimeoutException e) {
            LOGGER.error("Failed to start device queue consumer: {}", e.getMessage());
        }
    }

    private void consumeDeviceMessage(String message) {
        try {
            LOGGER.info("Raw message received: {}", message);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> json = objectMapper.readValue(message, Map.class);
            String operation = (String) json.get("operation");
            DeviceDetailsDTO deviceDetails = objectMapper.convertValue(json.get("deviceDetails"), DeviceDetailsDTO.class);

            if (deviceDetails == null) {
                LOGGER.error("DeviceDetailsDTO is null, message received: {}", message);
                throw new IllegalArgumentException("DeviceDetailsDTO is null");
            }

            switch (operation) {
                case "INSERT":
                    LOGGER.info("Inserting device: {}", deviceDetails);
                    deviceService.insert(deviceDetails);
                    break;
                case "UPDATE":
                    LOGGER.info("Updating device with ID {}: {}", deviceDetails.getId(), deviceDetails);
                    deviceService.update(deviceDetails.getId(), deviceDetails);
                    break;
                case "DELETE":
                    LOGGER.info("Deleting device with ID: {}", deviceDetails.getId());
                    deviceService.delete(deviceDetails.getId());
                    break;
                default:
                    throw new IllegalArgumentException("Unknown operation: " + operation);
            }
        } catch (Exception e) {
            LOGGER.error("Error processing device message: {}", e.getMessage());
        }
    }
}
