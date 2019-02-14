package com.fr.swift.config.entity;


import com.fr.swift.config.bean.SwiftConfigBean;
import com.fr.swift.converter.ObjectConverter;
import com.fr.swift.util.Strings;
import com.fr.third.javax.persistence.Column;
import com.fr.third.javax.persistence.Entity;
import com.fr.third.javax.persistence.Id;
import com.fr.third.javax.persistence.Table;

import java.io.Serializable;

import static com.fr.swift.config.SwiftConfigConstants.LONG_TEXT_LENGTH;


/**
 * @author yee
 * @date 2018/7/6
 */
@Entity
@Table(name = "fine_swift_config_entity")
public class SwiftConfigEntity implements Serializable, ObjectConverter<SwiftConfigBean> {
    private static final long serialVersionUID = 3522815101688011116L;
    @Id
    private String configKey;
    @Column(length = LONG_TEXT_LENGTH)
    private String configValue;

    public SwiftConfigEntity(String configKey, String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public SwiftConfigEntity() {
    }

    public SwiftConfigEntity(SwiftConfigBean swiftConfigBean) {
        this.configKey = swiftConfigBean.getConfigKey();
        this.configValue = swiftConfigBean.getConfigValue();
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        if (Strings.isEmpty(configValue)) {
            configValue = "__EMPTY__";
        }
        this.configValue = configValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SwiftConfigEntity that = (SwiftConfigEntity) o;

        if (configKey != null ? !configKey.equals(that.configKey) : that.configKey != null) {
            return false;
        }
        return configValue != null ? configValue.equals(that.configValue) : that.configValue == null;
    }

    @Override
    public int hashCode() {
        int result = configKey != null ? configKey.hashCode() : 0;
        result = 31 * result + (configValue != null ? configValue.hashCode() : 0);
        return result;
    }

    @Override
    public SwiftConfigBean convert() {
        return new SwiftConfigBean(configKey, configValue);
    }
}
