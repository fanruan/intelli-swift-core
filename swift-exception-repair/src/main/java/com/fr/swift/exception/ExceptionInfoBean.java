package com.fr.swift.exception;

import com.fr.swift.annotation.persistence.Column;
import com.fr.swift.annotation.persistence.Convert;
import com.fr.swift.annotation.persistence.Entity;
import com.fr.swift.annotation.persistence.Enumerated;
import com.fr.swift.annotation.persistence.Enumerated.EnumType;
import com.fr.swift.annotation.persistence.Id;
import com.fr.swift.annotation.persistence.Table;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.util.Assert;

import java.util.Objects;

/**
 * @author anchore
 * @date 2019/8/9
 * <p>
 * immut对象哦
 */
@Table(name = "fine_swift_exception_info")
@Entity
public class ExceptionInfoBean implements ExceptionInfo {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "sourceNodeId")
    private String sourceNodeId;
    @Convert(converter = ExceptionInfoTypeConverter.class)
    @Column(name = "type")
    private Type type;
    @Column(name = "occurredTime")
    private long occurredTime = -1;
    @Column(name = "context")
    @Convert(converter = ExceptionContextConverter.class)
    private ExceptionContext context;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    private State state;
    @Column(name = "operateNodeId")
    private String operateNodeId;

    private ExceptionInfoBean() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getSourceNodeId() {
        return sourceNodeId;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public long getOccurredTime() {
        return occurredTime;
    }

    @Override
    public ExceptionContext getContext() {
        return context;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public String getOperateNodeId() {
        return operateNodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExceptionInfoBean that = (ExceptionInfoBean) o;
        return Objects.equals(id, that.id) &&
                state == that.state &&
                Objects.equals(operateNodeId, that.operateNodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state, operateNodeId);
    }

    @Override
    public String toString() {
        return "ExceptionInfoBean{" +
                "sourceNodeId='" + sourceNodeId + '\'' +
                ", type=" + type +
                ", occurredTime=" + occurredTime +
                ", context=" + context +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ExceptionInfo exceptionInfo) {
        return new Builder(exceptionInfo);
    }

    public static class Builder {
        private ExceptionInfoBean bean;

        /**
         * 从已有对象，创建新对象，build完之前能修改属性，且不影响原对象
         * 秉承immut原则
         *
         * @param bean bean
         */
        public Builder(ExceptionInfo bean) {
            this.bean = new Builder().setId(bean.getId())
                    .setSourceNodeId(bean.getSourceNodeId())
                    .setType(bean.getType())
                    .setOccurredTime(bean.getOccurredTime())
                    .setContext(bean.getContext())
                    .setState(bean.getState())
                    .setOperateNodeId(bean.getOperateNodeId()).build();
        }

        /**
         * 全新对象
         */
        public Builder() {
            bean = new ExceptionInfoBean();
        }

        public Builder setId(String id) {
            bean.id = id;
            return this;
        }

        public Builder setSourceNodeId(String sourceNodeId) {
            bean.sourceNodeId = sourceNodeId;
            return this;
        }

        public Builder setType(Type type) {
            bean.type = type;
            return this;
        }

        public Builder setOccurredTime(long occurredTime) {
            bean.occurredTime = occurredTime;
            return this;
        }

        public Builder setContext(ExceptionContext context) {
            bean.context = context;
            return this;
        }

        public Builder setState(State state) {
            bean.state = state;
            return this;
        }

        public Builder setOperateNodeId(String operateNodeId) {
            bean.operateNodeId = operateNodeId;
            return this;
        }

        /**
         * 便利方法，此时此地发生异常
         *
         * @return builder
         */
        public Builder setNowAndHere() {
            String thisNodeId = SwiftProperty.getProperty().getClusterId();
            return setSourceNodeId(thisNodeId)
                    .setOccurredTime(System.currentTimeMillis())
                    .setState(State.PENDING)
                    .setOperateNodeId(thisNodeId);
        }

        /**
         * 根据sourceNodeId，type，occurredTime和context确定id
         *
         * @return id
         */
        private String genId() {
            // fixme
            return String.format("%s,%d,%d,%s",
                    bean.sourceNodeId,
                    bean.type.getExceptionCode(),
                    bean.occurredTime,
                    bean.context.toString());
        }

        public ExceptionInfoBean build() {
            Assert.notNull(bean.sourceNodeId);
            Assert.isTrue(bean.occurredTime >= 0);
            Assert.notNull(bean.type);
            Assert.notNull(bean.context);
            Assert.notNull(bean.state);
            Assert.notNull(bean.operateNodeId);
            if (bean.id == null) {
                bean.id = genId();
            }
            return bean;
        }
    }
}