import java.util.Map;

import com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue; 
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;

public class pulseJumpAnalizer {

    private static final int DEFAULT_TRESHOLD = 90;
    private static final int TRESHOLD = initTreshold();

    public void handleRequest(final DynamodbEvent event, final Context context) {

        LambdaLogger logger = context.getLogger();

        event.getRecords().forEach(rec -> {
            Map<String, AttributeValue> map = rec.getDynamodb().getNewImage();

            int patientId = Integer.parseInt(map.get("patientId").getN());
            long timestamp = Long.parseLong(map.get("timestamp").getN());
            int pulse = Integer.parseInt(map.get("pulse").getN());

            if (pulse >= TRESHOLD) {
                logger.log(String.format(
                        "Warning! Pulse level higher than %d: PatientId: %d, timestamp: %d, pulse: %d%n",
                        TRESHOLD, patientId, timestamp, pulse));
            }
        });
    }

    private static int initTreshold() {
        String value = System.getenv("TRESHOLD");
        if (value == null)
            return DEFAULT_TRESHOLD;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return DEFAULT_TRESHOLD;
        }
    }
}
