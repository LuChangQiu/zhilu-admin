package com.zl.mjga.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.jooq.JSON;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
    return builder ->
        builder
            .serializationInclusion(JsonInclude.Include.USE_DEFAULTS)
            .serializers(new JooqJsonSerializer());
  }

  private static class JooqJsonSerializer extends StdSerializer<JSON> {
    public JooqJsonSerializer() {
      super(JSON.class);
    }

    @Override
    public void serialize(JSON value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
      gen.writeRawValue(value.data());
    }
  }
}
