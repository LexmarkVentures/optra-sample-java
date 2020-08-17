package com.lexmark.optra;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * An convenience class to build messages that will be sent to the hub.
 * It injects necessary metadata that is used in the optra portal, and
 * abstracts away the 'shape' of the JSON that the portal expects.
 *
 */
public class SkillMessage {

    private final Map<String, Object> message;
    private final Map<String, Object> messageData;
    private static final String KEY_CREATED_AT = "createdAt";
    private static final String KEY_DATA = "data";

    public SkillMessage() {
        this.messageData = new HashMap<>();
        this.message = new HashMap<>();
        this.message.put(KEY_CREATED_AT, System.currentTimeMillis());
        this.message.put(KEY_DATA, this.messageData);
    }

    public SkillMessage addData(String key, Object value) {
        this.messageData.put(key, value);
        return this;
    }

    public JSONObject toJSON() {
        return new JSONObject(this.message);
    }
}
