PackageDataView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(PackageDataView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-package-data mvc-layout"
        })
    },

    _init: function(){
        PackageDataView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var tables_data = [{
            id: "fd0f2fc23ff748b3",
            used_fields: ["类型/类型", "基本ID", "A_ID", "B_ID", "值/值"]
        }, {
            id: "63c0620c4c4e2aeb",
            used_fields: []
        }]
        var table_data_data = [{"63c0620c4c4e2aeb":{
                connection_name: "local",
                fields: [],
                md5: "6b056ce5",
                table_name: "contract"
            },
            "fd0f2fc23ff748b3": {
                connection_name: "__FR_BI_ETL__",
                etl_type: "union",
                etl_value: {},
                fields: [],
                md5: "b23180c5",
                tables: [
                {
                    connection_name: "local",
                    fields: [],
                    md5: "81842912",
                    table_name: "a_基本信息"
                }, {
                    connection_name: "local",
                    fields: [],
                    md5: "b22db151",
                    table_name: "b_基本事实表"
                }
                ]
            }}];

        var titleLabel = BI.createWidget({
            type: "bi.label",
            text: "业务包数据结构",
            cls: "title-label"
        });
        var description1 = BI.createWidget({
            type: "bi.label",
            whiteSpace: "normal",
            textAlign: "left",
            text: "对于单个业务包，从业务包管理界面进入业务包的时候，除自身的id属性外，还包括了一个tables属性，" +
            "该tables包含id和used_fields，分别表示该业务包中表的id和对应的参与分析的字段名。",
            cls: "description-label",
            hgap: 20,
            vgap: 5
        });
        var description2 = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            whiteSpace: "normal",
            text: "在进入业务包界面后，会做一次读数据的操作（get_tables_of_one_package），数据读取完成后，" +
            "返回三个对象——translations(转义)、relations(关联)、table_data(表详细数据)。" +
            "在load中，将translations和relations放到model层的临时变量中做缓存，同时也分别放到Sharing Pool中，" +
            "作为一份可被共享的数据（主要使用其已保存的特性）；对于table_data对象，首先在model层的tmp中，要把每一个id对应的数据放入，" +
            "供后面点击某一张表时候skipTo使用，然后还要在Sharing Pool中放一份当前业务包的初始数据。",
            cls: "description-label",
            hgap: 20,
            vgap: 5
        });
        var description3 = BI.createWidget({
            type: "bi.label",
            textAlign: "left",
            whiteSpace: "normal",
            text: "当添加表的时候，维护两个对象，对于存放id和used_fields的tables对象，可以使用一个tmp_tables存放于tmp中，tmp中的添加id和对应的数据；" +
            "当表数据修改的时候，更新translations、relations和usedFields信息。" +
            "最后在保存的时候，tmp中的数据和model层数据根据需要封装提交到后台保存。",
            cls: "description-label",
            hgap: 20,
            vgap: 5
        });

        var tab = BI.createWidget({
            type: "bi.tab",
            tab: {
                height: 30,
                items: [{
                    text: "tables数据",
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
                    items: [titleLabel, description1, description2, description3]
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
                tables.element.html('<pre id="jsonOutput" class="json"><span class="json-open-bracket">[</span><span class="json-collapse-1"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">{</span><span class="json-collapse-2"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"id"</span><span class="json-semi-colon">: </span><span class="json-value">"fd0f2fc23ff748b3"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"used_fields"</span><span class="json-semi-colon">: </span><span class="json-open-bracket">[</span><span class="json-collapse-3"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-value">"类型/类型"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-value">"基本ID"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-value">"A_ID"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-value">"B_ID"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-value">"值/值"</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">]</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">{</span><span class="json-collapse-4"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"id"</span><span class="json-semi-colon">: </span><span class="json-value">"63c0620c4c4e2aeb"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"used_fields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><br> <span class="json-indent"></span></span><span class="json-close-bracket">]</span></pre>');
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [tables]
                });
            case 2:
                var tableData = BI.createWidget({
                    type: "bi.layout",
                    scrolly: true
                });
                tableData.element.html('<pre id="jsonOutput" class="json"><span class="json-open-bracket">[</span> <span class="json-collapse-1" style="display: inline;"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">{</span> <span class="json-collapse-2"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"63c0620c4c4e2aeb"</span><span class="json-semi-colon">: </span><span class="json-open-bracket">{</span> <span class="json-collapse-3"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"connection_name"</span><span class="json-semi-colon">: </span><span class="json-value">"local"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"fields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"md5"</span><span class="json-semi-colon">: </span><span class="json-value">"6b056ce5"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"table_name"</span><span class="json-semi-colon">: </span><span class="json-value">"contract"</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"fd0f2fc23ff748b3"</span><span class="json-semi-colon">: </span><span class="json-open-bracket">{</span> <span class="json-collapse-4"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"connection_name"</span><span class="json-semi-colon">: </span><span class="json-value">"__FR_BI_ETL__"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"etl_type"</span><span class="json-semi-colon">: </span><span class="json-value">"union"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"etl_value"</span><span class="json-semi-colon">: </span><span class="json-empty-object">{}</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"fields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"md5"</span><span class="json-semi-colon">: </span><span class="json-value">"b23180c5"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"tables"</span><span class="json-semi-colon">: </span><span class="json-open-bracket">[</span> <span class="json-collapse-5"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">{</span> <span class="json-collapse-6"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"connection_name"</span><span class="json-semi-colon">: </span><span class="json-value">"local"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"fields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"md5"</span><span class="json-semi-colon">: </span><span class="json-value">"81842912"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"table_name"</span><span class="json-semi-colon">: </span><span class="json-value">"a_基本信息"</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-open-bracket">{</span> <span class="json-collapse-7"><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"connection_name"</span><span class="json-semi-colon">: </span><span class="json-value">"local"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"fields"</span><span class="json-semi-colon">: </span><span class="json-empty-array">[]</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"md5"</span><span class="json-semi-colon">: </span><span class="json-value">"b22db151"</span><span class="json-comma">,</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span><span class="json-property">"table_name"</span><span class="json-semi-colon">: </span><span class="json-value">"b_基本事实表"</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">]</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><br> <span class="json-indent">&nbsp;&nbsp;&nbsp;</span></span><span class="json-close-bracket">}</span><br> <span class="json-indent"></span></span><span class="json-close-bracket">]</span></pre>');
                return BI.createWidget({
                    type: "bi.vertical",
                    items: [tableData]
                })
        }
    }
});

PackageDataModel = BI.inherit(BI.Model, {

});