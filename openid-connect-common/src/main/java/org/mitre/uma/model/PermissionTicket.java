/*******************************************************************************
 * Copyright 2018 The MIT Internet Trust Consortium
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

package org.mitre.uma.model;

import java.util.Collection;
import java.util.Date;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 *
 * An UMA permission, used in the protection API.
 *
 * @author jricher
 *
 */
@Entity
@Table(name = "permission_ticket")
@NamedQueries({
	@NamedQuery(name = PermissionTicket.QUERY_TICKET, query = "select p from PermissionTicket p where p.ticket = :" + PermissionTicket.PARAM_TICKET),
	@NamedQuery(name = PermissionTicket.QUERY_ALL, query = "select p from PermissionTicket p"),
	@NamedQuery(name = PermissionTicket.QUERY_BY_RESOURCE_SET, query = "select p from PermissionTicket p where p.permission.resourceSet.id = :" + PermissionTicket.PARAM_RESOURCE_SET_ID)
})
public class PermissionTicket {

	public static final String QUERY_TICKET = "PermissionTicket.queryByTicket";
	public static final String QUERY_ALL = "PermissionTicket.queryAll";
	public static final String QUERY_BY_RESOURCE_SET = "PermissionTicket.queryByResourceSet";

	public static final String PARAM_TICKET = "ticket";
	public static final String PARAM_RESOURCE_SET_ID = "rsid";

	private Long id;
	private Permission permission;
	private String ticket;
	private Date expiration;
	private Collection<Claim> claimsSupplied;

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
	 * @return the permission
	 */
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "permission_id")
	public Permission getPermission() {
		return permission;
	}

	/**
	 * @param permission the permission to set
	 */
	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	/**
	 * @return the ticket
	 */
	@Basic
	@Column(name = "ticket")
	public String getTicket() {
		return ticket;
	}

	/**
	 * @param ticket the ticket to set
	 */
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	/**
	 * @return the expiration
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "expiration")
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	/**
	 * @return the claimsSupplied
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "claim_to_permission_ticket",
			joinColumns = @JoinColumn(name = "permission_ticket_id"),
			inverseJoinColumns = @JoinColumn(name = "claim_id")
			)
	public Collection<Claim> getClaimsSupplied() {
		return claimsSupplied;
	}

	/**
	 * @param claimsSupplied the claimsSupplied to set
	 */
	public void setClaimsSupplied(Collection<Claim> claimsSupplied) {
		this.claimsSupplied = claimsSupplied;
	}


}
