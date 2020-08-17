package com.lexmark.optra;

import com.microsoft.azure.sdk.iot.deps.twin.TwinCollection;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.*;
import com.microsoft.azure.sdk.iot.device.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A bare-bones Optra skill.
 */
public class Skill {

    private static final Logger LOGGER = LoggerFactory.getLogger(Skill.class);

    private static final String PROP_INPUTS_KEY = "inputs"; // Inputs section of the device twin.
    private static final String PROP_FREQUENCY_KEY = "frequency"; // The input for our frequency.

    private static boolean terminating = false;
    private static int frequencyInSeconds = 60;
    private static double currentTemperature = 70;

    private static class DeviceTwinStatusCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode responseStatus, Object callbackContext) {
            LOGGER.info("Device twin status callback with response status: {}.", responseStatus);
        }
    }

    /**
     * Responds to twin property change events. These will be the inputs,
     * as defined when you add your skill to the portal.
     */
    private static class TwinPropertyChangeEvent implements TwinPropertyCallBack {
        @Override
        public void TwinPropertyCallBack(Property property, Object context) {
            LOGGER.info("Incoming property change {}.", property.getKey());

            if (!property.getIsReported() && property.getKey().equalsIgnoreCase(PROP_INPUTS_KEY)) {
                try {
                    TwinCollection collection = (TwinCollection) property.getValue();
                    String updatedFrequency = (String) collection.get(PROP_FREQUENCY_KEY);
                    frequencyInSeconds = Integer.parseInt(updatedFrequency);
                } catch (Throwable throwable) {
                    LOGGER.warn("Invalid twin update.", throwable);
                }
                LOGGER.info("Property value is: {}.", property.getValue());
            }
        }
    }

    /**
     * Main method, called by the container.
     * @param args program arguments.
     */
    public static void main(String[] args) {
        try {

            Thread mainThread = Thread.currentThread();

            Thread shutdownListener = new Thread() {
                public void run() {
                    terminating = true;
                    mainThread.interrupt();
                }
            };

            Runtime.getRuntime().addShutdownHook(shutdownListener);

            // Dump environment variables passed to the skill by the Optra portal.
            Map<String, String> environment = System.getenv();
            for (Map.Entry<String, String> entry : environment.entrySet()) {
                if (entry.getKey().startsWith("OPTRA")) {
                    LOGGER.info("OPTRA Environment variable set: {}: {}.", entry.getKey(), entry.getValue());
                }
            }

            IotHubClientProtocol protocol = IotHubClientProtocol.MQTT_WS;
            ModuleClient client = ModuleClient.createFromEnvironment(protocol);
            client.open();
            
            // Setup the device twin and listen for changes to the "inputs" section.
            client.startTwin(new DeviceTwinStatusCallback(), null, new TwinPropertyChangeEvent(), null);

            while (!terminating) {
                JSONObject messageJson = constructMessage();
                String messageContent = messageJson.toString(2);
                Message message = new Message(messageContent);
                client.sendEventAsync(message, null, null);
                LOGGER.info("Sending message to IoT hub: {}.", messageContent);
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(frequencyInSeconds));
                } catch (InterruptedException exception) {
                }
            }

            client.closeNow();
        } catch (Exception exception) {
            LOGGER.warn("Unexpected exception while connecting.", exception);
        }
    }

    private static JSONObject constructMessage() {
        double temperatureChange = -0.3 + Math.random() * 0.6;
        currentTemperature += temperatureChange;
        currentTemperature = (double) Math.round(currentTemperature * 10) / 10;
        return new SkillMessage()
                .addData("temperature", currentTemperature)
                .toJSON();
    }
}
