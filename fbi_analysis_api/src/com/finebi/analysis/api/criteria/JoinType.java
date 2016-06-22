<<<<<<< HEAD

=======
/*******************************************************************************
 * Copyright (c) 2008 - 2013 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/
>>>>>>> JPA接口
package com.finebi.analysis.api.criteria;

/**
 * Defines the three types of joins.
 *
 * Right outer joins and right outer fetch joins are not required 
<<<<<<< HEAD
 * to be supported in Advanced Fine BI 5.1.  Applications that use
 * <code>RIGHT</code> join types will not be portable.
 *
 * @since Advanced FineBI Analysis 1.0
=======
 * to be supported in Java Persistence 2.1.  Applications that use
 * <code>RIGHT</code> join types will not be portable.
 *
 * @since Java Persistence 2.0
>>>>>>> JPA接口
 */
public enum JoinType {

    /** Inner join. */
    INNER, 

    /** Left outer join. */
    LEFT, 

    /** Right outer join. */
    RIGHT
}
