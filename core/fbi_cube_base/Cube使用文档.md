#Cube使用手册

##Cube结构


cube现分为三层结构：

* cube对象层。主要作用是对外提供对于cube的访问。例如cube，tableEntity等对象。
* cube数据发现层。连接cube对象和底层cube数据。
* cube数据层。负责具体的数据存储工作。

![](http://7xrn7f.com1.z0.glb.clouddn.com/16-6-21/57869750.jpg)
<center>cube结构图</center>


##Cube接口简单说明

使用的接口多是由cube对象层提供的。包括以下几个基本接口：

* com.finebi.cube.structure.Cube cube主接口
* com.finebi.cube.structure.CubeTableEntityGetterService cube中table的接口
* com.finebi.cube.structure.column.CubeColumnReaderService cube中column的接口
* com.finebi.cube.structure.CubeRelationEntityGetterService cube中relation的接口，包括Table的Relation和Column的Relation


##Cube接口详细使用说明
###Cube接口
访问cube数据最主要的接口。要想访问cube数据，首先得获得该cube。通过此接口才能进而获得Table，Column和Relation的接口。

Cube接口有一个BICube实现。BICube的获得必须要有ICubeResourceRetrievalService和ICubeResourceDiscovery两个对象。他们分别对应cube结构图中的DataDiscover和CubeData。ICubeResourceDiscovery对象可以通过Factory直接获得。ICubeResourceRetrievalService需要依据cube的配置来获得。

下面的代码是获得标准Cube的方法。

	  ICubeResourceDiscovery discovery = BIFactoryHelper.getObject(ICubeResourceDiscovery.class);
      ICubeResourceRetrievalService resourceRetrievalService = new BICubeResourceRetrieval(BICubeConfiguration.getConf());
      Cube cube = new BICube(resourceRetrievalService, discovery);


###TableEntityGetterService 
使用Cube中table的接口。通过TableSource生成的TableKey，利用Cube接口中的

	/**
     * 获得表的操作接口
     *
     * @param tableSource 数据源
     * @return 表的操作对象
     */
    CubeTableEntityGetterService Cube.getCubeTable(ITableKey tableKey);

获得TableEntityGetterService对象后，可以访问与当前Table相关的版本号、生成时间等元数据。TableEntityGetterService接口是没有办法直接获得表中数据的。例如希望获取表索引、关联索引等具体的数据，还需要进一步获得相关接口才可以，下面内容会详细介绍。

###CubeColumnReaderService
Column对象的接口，如果要获取表的详细数据，首先要获得Column对象。具体的获取方法如下

	
    /**
     * 获取列的接口
     *
     * @param key 列
     * @return 获取列的接口
     */
    CubeColumnReaderService TableEntityGetterService.getColumnDataGetter(BIColumnKey columnKey) throws BICubeColumnAbsentException;

通过以上在TableEntityGetterService中的接口，传递指定的Column参数即可获得相应的CubeColumnReaderService对象。通过该对象除了可以获得相应元数据外，还可以快速访问详细数据、分组数据和相应的索引数据。而这也是Table接口中获得表数据的内部方法。一切与表数据的获得都通过此接口。

**访问详细数据**

通过指定在数据源中的行号，即可获得当前Column所在行号的值。


	 /**
     * 根据行号获得对应的原始值。
     *
     * @param rowNumber 数据库中的行号
     * @return 原始值
     */
    T getOriginalValueByRow(int rowNumber);
  
**获得索引值**

指定行号，获得Column在当前表中的索引值。

	/**
     * 根据数据库中的行号来获得相应的索引值
     *
     * @param rowNumber 行号
     * @return 索引
     */
    GroupValueIndex getIndexByRow(int rowNumber) throws BIResourceInvalidException, BICubeIndexException;

指定分组值，获得相应的索引值

	/**
     * 根据分组值来获得相应的索引值
     * @param groupValues 分组值
     * @return 索引值
     * @throws BIResourceInvalidException 资源不可以
     * @throws BICubeIndexException 索引异常
     */
    GroupValueIndex getIndexByGroupValue(T groupValues) throws BIResourceInvalidException, BICubeIndexException;


**获得分组位置**

指定value，获取当改value前Column中某值的分组位置。（分组位置，详见Cube的数据结构与算法）

	 /**
     * 获得分组值对应的位置。
     *
     * @param groupValues
     * @return 分组值的位置
     */
    int getPositionOfGroup(T groupValues) throws BIResourceInvalidException;

###CubeRelationEntityGetterService
关联对象的接口。包括表间关联和字段关联。

* 表间关联：是指通过指定的关联，基于表的行号建立起来的关联索引。例如A表的第一行对应B表的二三行数据。
* 字段关联：是指利用表间关联，基于该字段分组位置简历的索引。例如A表的a列里面的小明，在分组排序的位置是3，对应B表是三五行数据。

表间关联的获取方法

	/**
     * 获取关联数据的接口
     *
     * @param path  关联的路径
     * @return 获取关联数据的接口
     */
    CubeRelationEntityGetterService TableEntityGetterService.getRelationIndexGetter(BICubeTablePath path)
            throws BICubeRelationAbsentException,  IllegalRelationPathException;

通过以上在TableEntityGetterService中的接口，依据指定的Path即可获得相应的表间关联。

字段关联的获取方法

	 /**
     * 获取字段关联
     * @param path 关联路径
     * @return 字段关联
     * @throws BICubeRelationAbsentException 关联缺失
     * @throws IllegalRelationPathException 路径不合法
     */
    CubeRelationEntityGetterService CubeColumnReaderService.getRelationIndexGetter(BICubeTablePath path) throws BICubeRelationAbsentException, IllegalRelationPathException;


参数与表间关联一致，唯一区别是字段关联是从目标字段的CubeColumnReaderService中获取的。

##总结
通过以上的四个主要接口，便可以访问cube的全部数据。以上的方法是标准的获取数据的方法。除此以外，还会发现可以从Cube接口中获得关联对象等等，这些方法只是做封装，方便使用。归根结底还是以上介绍的方法。