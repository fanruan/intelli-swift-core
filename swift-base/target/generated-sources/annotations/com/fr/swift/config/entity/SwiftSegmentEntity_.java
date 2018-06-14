package com.fr.swift.config.entity;

import com.fr.swift.cube.io.Types.StoreType;
import com.fr.third.javax.persistence.metamodel.SingularAttribute;
import com.fr.third.javax.persistence.metamodel.StaticMetamodel;
import java.net.URI;
import javax.annotation.Generated;

@Generated(value = "com.fr.third.org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SwiftSegmentEntity.class)
public abstract class SwiftSegmentEntity_ {

	public static volatile SingularAttribute<SwiftSegmentEntity, URI> segmentUri;
	public static volatile SingularAttribute<SwiftSegmentEntity, StoreType> storeType;
	public static volatile SingularAttribute<SwiftSegmentEntity, Integer> segmentOrder;
	public static volatile SingularAttribute<SwiftSegmentEntity, String> id;
	public static volatile SingularAttribute<SwiftSegmentEntity, String> segmentOwner;

}

