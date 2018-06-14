package com.fr.swift.config.entity;

import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.third.javax.persistence.metamodel.ListAttribute;
import com.fr.third.javax.persistence.metamodel.SingularAttribute;
import com.fr.third.javax.persistence.metamodel.StaticMetamodel;
import javax.annotation.Generated;

@Generated(value = "com.fr.third.org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SwiftMetaDataEntity.class)
public abstract class SwiftMetaDataEntity_ {

	public static volatile SingularAttribute<SwiftMetaDataEntity, Schema> swiftSchema;
	public static volatile SingularAttribute<SwiftMetaDataEntity, String> remark;
	public static volatile SingularAttribute<SwiftMetaDataEntity, String> id;
	public static volatile SingularAttribute<SwiftMetaDataEntity, String> schemaName;
	public static volatile ListAttribute<SwiftMetaDataEntity, SwiftMetaDataColumn> fields;
	public static volatile SingularAttribute<SwiftMetaDataEntity, String> tableName;

}

