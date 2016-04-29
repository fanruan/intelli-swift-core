ETLDataView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(ETLDataView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-etl-data"
        })
    },

    _init: function(){
        ETLDataView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var titleLabel = BI.createWidget({
            type: "bi.label",
            text: "ETL数据结构",
            cls: "title-label"
        });
        var description1 = BI.createWidget({
            type: "bi.label",
            whiteSpace: "normal",
            textAlign: "left",
            text: "etl界面（也就是点击进入表设置界面），对于普通表（原始表）来说，table的属性一般包括connection_name, table_name, fields, schema_name, id ；" +
            "对于etl表来说，table的属性一般包括connection_name, etl_type, etl_value, fields, tables ；",
            cls: "description-label",
            hgap: 20,
            vgap: 5
        });
        var description2 = BI.createWidget({
            type: "bi.label",
            whiteSpace: "normal",
            textAlign: "left",
            text: "进入etl界面后，会在model层缓存一些数据，translations(转义), relations(关联), usedFields(参与分析字段), tables_id(所有表的id，包括elt的字表), allTables(所有表)；" +
            "因为中间层的表在后台是不保存id属性的，所有，首次进入的时候，或者新添加的，都要为所有的表添加id属性，添加id后，tables_id里存放的就是第一次添加的id；" +
            "allTables为一个数组，保存的是所有的表，如果这个数组的长度为一，那么可以确定只有一个最终表，可以通过添加表按钮，向这个allTables数组push一些table；",
            cls: "description-label",
            hgap: 20,
            vgap: 5
        });
        var description3 = BI.createWidget({
            type: "bi.label",
            whiteSpace: "normal",
            textAlign: "left",
            text: "除去上面的那些缓存数据，在tmp中，也会将所有的表（包括子表）的id对应具体表数据放进去，用来在右侧的流程图点击操作d时候使用。",
            cls: "description-label",
            hgap: 20,
            vgap: 5
        });
        var description4 = BI.createWidget({
            type: "bi.label",
            whiteSpace: "normal",
            textAlign: "left",
            text: "在进行具体的etl操作的时候，也就是点击右侧流程图中的某个节点进行etl操作，在view层中skipTo到相应的id中，操作完成后，返回到etl界面，" +
            "重新装配原先的树结构，使用替换的原则。",
            cls: "description-label",
            hgap: 20,
            vgap: 5
        });
        var description5 = BI.createWidget({
            type: "bi.label",
            whiteSpace: "normal",
            textAlign: "left",
            text: "保存一定是在model层中的缓存allTables只有一个元素的时候进行，作为最终表，进行适当的封装set到model层数据。",
            cls: "description-label",
            hgap: 20,
            vgap: 5
        });

        var allTables = [
            [{
                connection_name: "__FR_BI_ETL__",
                etl_type: "union",
                etl_value: {},
                fields: [],
                id: "203d0be8bd5290ce",
                relations: {},
                tables: [{
                    connection_name: "local",
                    fields: [],
                    id: "f551c98c427a03a0",
                    md5: "81842912",
                    table_name: "a_基本信息"
                }, {
                    connection_name: "local",
                    fields: [],
                    id: "d1d34a2c4180c92d",
                    md5: "b22db151",
                    table_name: "b_基本事实表"
                }],
                translations: {},
                usedFields: []
            }]
        ];

        var tab = BI.createWidget({
            type: "bi.tab",
            tab: {
                height: 30,
                items: [{
                    text: "allTables数据",
                    value: 1,
                    cls: "mvc-button layout-bg3"
                },{
                    text: "table_data数据",
                    value: 2,
                    cls: "mvc-button layout-bg4"
                }]
            },
            cardCreator: BI.bind(this._createTabs, this)
        });
        tab.setSelect(1);

        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items: [{
                el: {
                    type: "bi.vertical",
                    items: [titleLabel, description1, description2, description3, description4, description5]
                },
                height: 200
            }, {
                el: tab,
                height: "fill"
            }]
        })
    },

    _createTabs: function(v) {
        switch (v) {
            case 1:
                var tables = BI.createWidget({
                    type: "bi.layout",
                    scrolly: true
                });
                tables.element.html('<pre id="jsonOutput" class="json"><span class="json-open-bracket">[</span><span class="json-collapse-1"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">[</span><span class="json-collapse-2"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">{</span><span class="json-collapse-3"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"connection_name"</span><span class="json-semi-colon">: </span><span class="json-value">"__FR_BI_ETL__"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"etl_type"</span><span class="json-semi-colon">: </span><span class="json-value">"union"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"etl_value"</span><span class="json-semi-colon">: </span><span class="json-empty-object">{}</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"fields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"id"</span><span class="json-semi-colon">: </span><span class="json-value">"203d0be8bd5290ce"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"relations"</span><span class="json-semi-colon">: </span><span class="json-empty-object">{}</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"tables"</span><span class="json-semi-colon">: </span><span class="json-open-bracket">[</span><span class="json-collapse-4"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">{</span><span class="json-collapse-5"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"connection_name"</span><span class="json-semi-colon">: </span><span class="json-value">"local"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"fields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"id"</span><span class="json-semi-colon">: </span><span class="json-value">"f551c98c427a03a0"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"md5"</span><span class="json-semi-colon">: </span><span class="json-value">"81842912"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"table_name"</span><span class="json-semi-colon">: </span><span class="json-value">"a_基本信息"</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">{</span><span class="json-collapse-6"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"connection_name"</span><span class="json-semi-colon">: </span><span class="json-value">"local"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"fields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"id"</span><span class="json-semi-colon">: </span><span class="json-value">"d1d34a2c4180c92d"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"md5"</span><span class="json-semi-colon">: </span><span class="json-value">"b22db151"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"table_name"</span><span class="json-semi-colon">: </span><span class="json-value">"b_基本事实表"</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">]</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"translations"</span><span class="json-semi-colon">: </span><span class="json-empty-object">{}</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"usedFields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">]</span><br> <span class="json-indent"></span></span><span class="json-close-bracket">]</span></pre>');return BI.createWidget({
                    type: "bi.vertical",
                    items: [tables]
                });
        }
    }
});

ETLDataModel = BI.inherit(BI.Model, {

});