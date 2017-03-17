
package com.finebi.datasource.api;

/**
 * Specifies the mode of a parameter of a stored procedure query.
 *
 * @see StoredProcedureQuery
 * @see StoredProcedureParameter
 *
 * @since Advanced Fine BI 5.1
 */
public enum ParameterMode {

    /**
     *  Stored procedure input parameter
     */
    IN,

    /**
     *  Stored procedure input/output parameter
     */
    INOUT,

    /**
     *  Stored procedure output parameter
     */
    OUT,

    /**
     *  Stored procedure reference cursor parameter.   Some databases use
     *  REF_CURSOR parameters to return result sets from stored procedures.
     */
    REF_CURSOR,

}
