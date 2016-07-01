/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package com.finebi.datasource.sql.criteria.internal.metamodel;

import com.finebi.datasource.api.metamodel.BasicType;
import java.io.Serializable;

/**
 * @author Emmanuel Bernard
 */
public class BasicTypeImpl<X> implements BasicType<X>, Serializable {
	private final Class<X> clazz;
	private PersistenceType persistenceType;

	public PersistenceType getPersistenceType() {
		return persistenceType;
	}

	public Class<X> getJavaType() {
		return clazz;
	}

	public BasicTypeImpl(Class<X> clazz, PersistenceType persistenceType) {
		this.clazz = clazz;
		this.persistenceType = persistenceType;
	}
}
