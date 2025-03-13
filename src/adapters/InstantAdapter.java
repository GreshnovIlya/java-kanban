package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class InstantAdapter extends TypeAdapter<Instant> {
    @Override
    public void write(final JsonWriter jsonWriter, Instant instant) throws IOException {
        jsonWriter.value(String.valueOf(instant));
    }

    @Override
    public Instant read(final JsonReader jsonReader) throws IOException {
        return Instant.parse(jsonReader.nextString());
    }
}
