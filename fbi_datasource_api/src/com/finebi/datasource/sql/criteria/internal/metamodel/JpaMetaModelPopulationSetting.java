
package com.finebi.datasource.sql.criteria.internal.metamodel;

/**
 * @author Steve Ebersole
 */
public enum JpaMetaModelPopulationSetting {
    ENABLED,
    DISABLED,
    IGNORE_UNSUPPORTED;

    public static JpaMetaModelPopulationSetting parse(String setting) {
        if ("enabled".equalsIgnoreCase(setting)) {
            return ENABLED;
        } else if ("disabled".equalsIgnoreCase(setting)) {
            return DISABLED;
        } else {
            return IGNORE_UNSUPPORTED;
        }
    }


}
