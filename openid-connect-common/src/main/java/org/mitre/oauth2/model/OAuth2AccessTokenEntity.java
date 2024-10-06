/*******************************************************************************
 * Copyright 2018 The MIT Internet Trust Consortium
 *
 * Portions copyright 2011-2013 The MITRE Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
/**
 *
 */
package org.mitre.oauth2.model;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.Transient;

import org.mitre.oauth2.model.convert.JWTStringConverter;
import org.mitre.oauth2.model.convert.OAuth2TokenDeserializer;
import org.mitre.oauth2.model.convert.OAuth2TokenSerializer;
import org.mitre.openid.connect.model.ApprovedSite;
import org.mitre.uma.model.Permission;

import org.springframework.security.oauth2.core.OAuth2AccessToken;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


import com.nimbusds.jwt.JWT;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;

/**
 * @author jricher
 *
 */
@Entity
@Table(name = "access_token")
@NamedQueries({
	@NamedQuery(name = OAuth2AccessTokenEntity.QUERY_ALL, query = "select a from OAuth2AccessTokenEntity a"),
	@NamedQuery(name = OAuth2AccessTokenEntity.QUERY_EXPIRED_BY_DATE, query = "select a from OAuth2AccessTokenEntity a where a.expiration <= :" + OAuth2AccessTokenEntity.PARAM_DATE),
	@NamedQuery(name = OAuth2AccessTokenEntity.QUERY_BY_REFRESH_TOKEN, query = "select a from OAuth2AccessTokenEntity a where a.refreshToken = :" + OAuth2AccessTokenEntity.PARAM_REFERSH_TOKEN),
	@NamedQuery(name = OAuth2AccessTokenEntity.QUERY_BY_CLIENT, query = "select a from OAuth2AccessTokenEntity a where a.client = :" + OAuth2AccessTokenEntity.PARAM_CLIENT),
	@NamedQuery(name = OAuth2AccessTokenEntity.QUERY_BY_TOKEN_VALUE, query = "select a from OAuth2AccessTokenEntity a where a.jwt = :" + OAuth2AccessTokenEntity.PARAM_TOKEN_VALUE),
	@NamedQuery(name = OAuth2AccessTokenEntity.QUERY_BY_APPROVED_SITE, query = "select a from OAuth2AccessTokenEntity a where a.approvedSite = :" + OAuth2AccessTokenEntity.PARAM_APPROVED_SITE),
	@NamedQuery(name = OAuth2AccessTokenEntity.QUERY_BY_RESOURCE_SET, query = "select a from OAuth2AccessTokenEntity a join a.permissions p where p.resourceSet.id = :" + OAuth2AccessTokenEntity.PARAM_RESOURCE_SET_ID),
	@NamedQuery(name = OAuth2AccessTokenEntity.QUERY_BY_NAME, query = "select r from OAuth2AccessTokenEntity r where r.authenticationHolder.userAuth.name = :" + OAuth2AccessTokenEntity.PARAM_NAME)
})
@JsonSerialize(using = OAuth2TokenSerializer.class)
@JsonDeserialize(using = OAuth2TokenDeserializer.class)
public class OAuth2AccessTokenEntity extends OAuth2AccessToken {

	public static final String QUERY_BY_APPROVED_SITE = "OAuth2AccessTokenEntity.getByApprovedSite";
	public static final String QUERY_BY_TOKEN_VALUE = "OAuth2AccessTokenEntity.getByTokenValue";
	public static final String QUERY_BY_CLIENT = "OAuth2AccessTokenEntity.getByClient";
	public static final String QUERY_BY_REFRESH_TOKEN = "OAuth2AccessTokenEntity.getByRefreshToken";
	public static final String QUERY_EXPIRED_BY_DATE = "OAuth2AccessTokenEntity.getAllExpiredByDate";
	public static final String QUERY_ALL = "OAuth2AccessTokenEntity.getAll";
	public static final String QUERY_BY_RESOURCE_SET = "OAuth2AccessTokenEntity.getByResourceSet";
	public static final String QUERY_BY_NAME = "OAuth2AccessTokenEntity.getByName";

	public static final String PARAM_TOKEN_VALUE = "tokenValue";
	public static final String PARAM_CLIENT = "client";
	public static final String PARAM_REFERSH_TOKEN = "refreshToken";
	public static final String PARAM_DATE = "date";
	public static final String PARAM_RESOURCE_SET_ID = "rsid";
	public static final String PARAM_APPROVED_SITE = "approvedSite";
	public static final String PARAM_NAME = "name";

	public static final String ID_TOKEN_FIELD_NAME = "id_token";

	private Long id;

	private ClientDetailsEntity client;

	private AuthenticationHolderEntity authenticationHolder; // the authentication that made this access

	private JWT jwtValue; // JWT-encoded access token value

	private Date expiration;

	private TokenType tokenType = TokenType.BEARER;

	private OAuth2RefreshTokenEntity refreshToken;

	private Set<String> scope;

	private Set<Permission> permissions;

	private ApprovedSite approvedSite;

	private Map<String, Object> additionalInformation = new HashMap<>(); // ephemeral map of items to be added to the OAuth token response

	public OAuth2AccessTokenEntity(TokenType tokenType, String tokenValue, Instant issuedAt, Instant expiresAt) {
		super(tokenType, tokenValue, issuedAt, expiresAt);
	}

	protected OAuth2AccessTokenEntity() {
		super(null, null, null, null);
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Get all additional information to be sent to the serializer as part of the token response.
	 * This map is not persisted to the database.
	 */
	@Transient
	public Map<String, Object> getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * The authentication in place when this token was created.
	 * @return the authentication
	 */
	@ManyToOne
	@JoinColumn(name = "auth_holder_id")
	public AuthenticationHolderEntity getAuthenticationHolder() {
		return authenticationHolder;
	}

	/**
	 * @param authenticationHolder the authentication to set
	 */
	public void setAuthenticationHolder(AuthenticationHolderEntity authenticationHolder) {
		this.authenticationHolder = authenticationHolder;
	}

	/**
	 * @return the client
	 */
	@ManyToOne
	@JoinColumn(name = "client_id")
	public ClientDetailsEntity getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(ClientDetailsEntity client) {
		this.client = client;
	}

	/**
	 * Get the string-encoded value of this access token.
	 */
	@Transient
	public String getValue() {
		return jwtValue.serialize();
	}

	@Basic
	@Temporal(jakarta.persistence.TemporalType.TIMESTAMP)
	@Column(name = "expiration")
	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	@Override
	public TokenType getTokenType() {
		return tokenType;
	}

	public void setTokenType(TokenType tokenType) {
		this.tokenType = tokenType;
	}

	@Basic
	@Column(name = "token_type")
	public String getTokenTypeValue() {
		return getTokenType().getValue();
	}

	@ManyToOne
	@JoinColumn(name="refresh_token_id")
	public OAuth2RefreshTokenEntity getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(OAuth2RefreshTokenEntity refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setRefreshToken(OAuth2RefreshToken refreshToken) {
		if (!(refreshToken instanceof OAuth2RefreshTokenEntity)) {
			throw new IllegalArgumentException("Not a storable refresh token entity!");
		}
		// force a pass through to the entity version
		setRefreshToken((OAuth2RefreshTokenEntity)refreshToken);
	}

	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(
			joinColumns=@JoinColumn(name="owner_id"),
			name="token_scope"
			)
	public Set<String> getScope() {
		return scope;
	}

	public void setScope(Set<String> scope) {
		this.scope = scope;
	}

	@Transient
	public boolean isExpired() {
		return getExpiration() == null ? false : System.currentTimeMillis() > getExpiration().getTime();
	}

	/**
	 * @return the jwtValue
	 */
	@Basic
	@Column(name="token_value")
	@Convert(converter = JWTStringConverter.class)
	public JWT getJwt() {
		return jwtValue;
	}

	/**
	 * @param jwt the jwtValue to set
	 */
	public void setJwt(JWT jwt) {
		this.jwtValue = jwt;
	}

	@Transient
	public int getExpiresIn() {

		if (getExpiration() == null) {
			return -1; // no expiration time
		} else {
			int secondsRemaining = (int) ((getExpiration().getTime() - System.currentTimeMillis()) / 1000);
			if (isExpired()) {
				return 0; // has an expiration time and expired
			} else { // has an expiration time and not expired
				return secondsRemaining;
			}
		}
	}

	/**
	 * @return the permissions
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(
			name = "access_token_permissions",
			joinColumns = @JoinColumn(name = "access_token_id"),
			inverseJoinColumns = @JoinColumn(name = "permission_id")
			)
	public Set<Permission> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}

	@ManyToOne
	@JoinColumn(name="approved_site_id")
	public ApprovedSite getApprovedSite() {
		return approvedSite;
	}

	public void setApprovedSite(ApprovedSite approvedSite) {
		this.approvedSite = approvedSite;
	}

	/**
	 * Add the ID Token to the additionalInformation map for a token response.
	 * @param idToken
	 */
	@Transient
	public void setIdToken(JWT idToken) {
		if (idToken != null) {
			additionalInformation.put(ID_TOKEN_FIELD_NAME, idToken.serialize());
		}
	}
}
