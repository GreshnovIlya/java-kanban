package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(final JsonWriter jsonWriter, Duration duration) throws IOException {
        try {
            jsonWriter.value(Long.toString(duration.toMinutes()));
        } catch (Exception e) {
            jsonWriter.value("null");
        }
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        try {
            return Duration.ofMinutes(jsonReader.nextLong());
        } catch (Exception e) {
            return null;
        }
    }
}