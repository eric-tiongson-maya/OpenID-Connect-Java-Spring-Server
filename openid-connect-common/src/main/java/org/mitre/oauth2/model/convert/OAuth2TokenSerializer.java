package org.mitre.oauth2.model.convert;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2TokenSerializer extends JsonSerializer<OAuth2Token> {
	@Override
	public void serialize(OAuth2Token oAuth2Token, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {

	}
}
