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
package org.mitre.oauth2.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.mitre.oauth2.model.convert.SerializableStringConverter;
import org.mitre.oauth2.model.convert.SimpleGrantedAuthorityStringConverter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Entity
@Table(name = "authentication_holder")
@NamedQueries ({
    @NamedQuery(name = AuthenticationHolderEntity.QUERY_ALL, query = "select a from AuthenticationHolderEntity a"),
    @NamedQuery(name = AuthenticationHolderEntity.QUERY_GET_UNUSED, query = "select a from AuthenticationHolderEntity a where " +
            "a.id not in (select t.authenticationHolder.id from OAuth2AccessTokenEntity t) and " +
            "a.id not in (select r.authenticationHolder.id from OAuth2RefreshTokenEntity r) and " +
            "a.id not in (select c.authenticationHolder.id from AuthorizationCodeEntity c)")
})
public class AuthenticationHolderEntity {

    public static final String QUERY_GET_UNUSED = "AuthenticationHolderEntity.getUnusedAuthenticationHolders";
    public static final String QUERY_ALL = "AuthenticationHolderEntity.getAll";

    private Long id;

    private SavedUserAuthentication userAuth;

    private Collection<GrantedAuthority> authorities;

    private Set<String> resourceIds;

    private boolean approved;

    private String redirectUri;

    private Set<String> responseTypes;

    private Map<String, Serializable> extensions;

    private String clientId;

    private Set<String> scope;

    private Map<String, String> requestParameters;

    private OAuth2AuthorizationRequest authorizationRequest;
    private OAuth2AccessToken accessToken;
    private Authentication userAuthentication;

    public AuthenticationHolderEntity() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public Authentication getAuthentication() {
        return this.userAuthentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.userAuthentication = authentication;
    }

    @Transient
    public OAuth2AuthorizationRequest getAuthorizationRequest() {
        return this.authorizationRequest;
    }

    public void setAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest) {
        this.authorizationRequest = authorizationRequest;
        
        setClientId(authorizationRequest.getClientId());
        setRedirectUri(authorizationRequest.getRedirectUri());
        setResponseTypes(new HashSet<>(authorizationRequest.getResponseTypes()));
        setScope(new HashSet<>(authorizationRequest.getScopes()));
        setRequestParameters(new HashMap<>(authorizationRequest.getAdditionalParameters()));
    }

    @Transient
    public OAuth2AccessToken getAccessToken() {
        return this.accessToken;
    }

    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * @return the userAuth
     */
    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "user_auth_id")
    public SavedUserAuthentication getUserAuth() {
        return userAuth;
    }

    /**
     * @param userAuth the userAuth to set
     */
    public void setUserAuth(SavedUserAuthentication userAuth) {
        this.userAuth = userAuth;
    }

    /**
     * @return the authorities
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="authentication_holder_authority",
            joinColumns=@JoinColumn(name="owner_id")
            )
    @Convert(converter = SimpleGrantedAuthorityStringConverter.class)
    @Column(name="authority")
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * @param authorities the authorities to set
     */
    public void setAuthorities(Collection<GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * @return the resourceIds
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="authentication_holder_resource_id",
            joinColumns=@JoinColumn(name="owner_id")
            )
    @Column(name="resource_id")
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    /**
     * @param resourceIds the resourceIds to set
     */
    public void setResourceIds(Set<String> resourceIds) {
        this.resourceIds = resourceIds;
    }

    /**
     * @return the approved
     */
    @Basic
    @Column(name="approved")
    public boolean isApproved() {
        return approved;
    }

    /**
     * @param approved the approved to set
     */
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    /**
     * @return the redirectUri
     */
    @Basic
    @Column(name="redirect_uri")
    public String getRedirectUri() {
        return redirectUri;
    }

    /**
     * @param redirectUri the redirectUri to set
     */
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    /**
     * @return the responseTypes
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="authentication_holder_response_type",
            joinColumns=@JoinColumn(name="owner_id")
            )
    @Column(name="response_type")
    public Set<String> getResponseTypes() {
        return responseTypes;
    }

    /**
     * @param responseTypes the responseTypes to set
     */
    public void setResponseTypes(Set<String> responseTypes) {
        this.responseTypes = responseTypes;
    }

    /**
     * @return the extensions
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="authentication_holder_extension",
            joinColumns=@JoinColumn(name="owner_id")
            )
    @Column(name="val")
    @MapKeyColumn(name="extension")
    @Convert(converter=SerializableStringConverter.class)
    public Map<String, Serializable> getExtensions() {
        return extensions;
    }

    /**
     * @param extensions the extensions to set
     */
    public void setExtensions(Map<String, Serializable> extensions) {
        this.extensions = extensions;
    }

    /**
     * @return the clientId
     */
    @Basic
    @Column(name="client_id")
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the scope
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="authentication_holder_scope",
            joinColumns=@JoinColumn(name="owner_id")
            )
    @Column(name="scope")
    public Set<String> getScope() {
        return scope;
    }

    /**
     * @param scope the scope to set
     */
    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    /**
     * @return the requestParameters
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="authentication_holder_request_parameter",
            joinColumns=@JoinColumn(name="owner_id")
            )
    @Column(name="val")
    @MapKeyColumn(name="param")
    public Map<String, String> getRequestParameters() {
        return requestParameters;
    }

    /**
     * @param requestParameters the requestParameters to set
     */
    public void setRequestParameters(Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }

    @Transient
    private OAuth2AuthorizationRequest createOAuth2AuthorizationRequest() {
        return OAuth2AuthorizationRequest.authorizationCode()
            .clientId(clientId)
            .redirectUri(redirectUri)
            .scopes(scope)
            .state(requestParameters.get("state"))
            .additionalParameters(requestParameters)
            .build();
    }
}
