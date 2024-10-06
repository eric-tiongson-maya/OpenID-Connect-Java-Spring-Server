package org.mitre.oauth2.model.convert;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2TokenDeserializer extends JsonDeserializer<OAuth2AccessToken> {
	@Override
	public OAuth2AccessToken deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
		return null;
	}
}
