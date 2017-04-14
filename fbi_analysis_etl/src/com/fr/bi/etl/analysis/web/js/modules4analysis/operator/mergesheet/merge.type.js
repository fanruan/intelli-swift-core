/**
 * Created by windy on 2017/4/12.
 */
BI.AnalysisETLMergeSheetType = BI.inherit(BI.Widget, {

    props: {
        extraCls:"bi-analysis-etl-merge-type"
    },

    render: function(){
        var self = this;
        var height = 80;
        this.model = new BI.AnalysisETLMergeSheetCommonModel({});
        var svg = BI.createWidget({
            type:"bi.svg",
            height: height,
            width: 1
        });
        svg.path("M0,0L0," + height);
        return {
            type:"bi.vtape",
            items:[{
                el :{
                    type:"bi.left_right_vertical_adapt",
                    cls:"title",
                    llgap : 10,
                    items:{
                        left:[{
                            type:"bi.label",
                            text:BI.i18nText("BI-Merge_Type"),
                            cls:"label"
                        }]
                    }
                },
                height:40
            }, {
                type:"bi.left_right_vertical_adapt",
                scrollable : false,
                lrgap:5,
                llgap:5,
                rrgap:10,
                items:{
                    left:[{
                        type: "bi.button_group",
                        items: BI.createItems(BI.JoinTypeGroup.JOIN_TYPE_GROUP, {
                            type: "bi.join_type_button",
                            height: 115,
                            width: 72
                        }),
                        layouts: [{
                            type: "bi.left",
                            lgap: 5,
                            vgap:10
                        }],
                        ref: function(_ref){
                            self.join_type = _ref;
                        },
                        listeners: [{
                            eventName: BI.ButtonGroup.EVENT_CHANGE,
                            action: function(v){
                                self.setValue(v)
                                self.fireEvent(BI.AnalysisETLMergeSheetType.EVENT_CHANGE, v)
                            }
                        }]
                    }, svg],
                    right:[{
                        type: "bi.join_type_button",
                        height: 115,
                        width: 72,
                        iconCls: "union-icon",
                        text: BI.i18nText("BI-Union"),
                        value: ETLCst.ETL_UNION_STYLE,
                        ref: function(_ref){
                            self.union = _ref;
                        },
                        listeners: [{
                            eventName: BI.Controller.EVENT_CHANGE,
                            action: function(){
                                self.setValue(self.union.getValue())
                                self.fireEvent(BI.AnalysisETLMergeSheetType.EVENT_CHANGE, self.union.getValue())
                            }
                        }]
                    }]
                }
            }]
        }
    },

    _populate : function () {
        var value = this.model.get("merge") || BICst.ETL_JOIN_STYLE.LEFT_JOIN;
        this.model.set("merge", value)
        this.union.setSelected(value === ETLCst.ETL_UNION_STYLE)
        this.join_type.setValue(value)
    },

    setValue : function (v) {
        this.model.set("merge", v)
        this._populate()
    },

    populate: function(m, options){
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    update: function(){
        return this.model.update();
    }

})

BI.AnalysisETLMergeSheetType.EVENT_CHANGE = "merge_change";

BI.shortcut("bi.analysis_etl_merge_type",BI.AnalysisETLMergeSheetType)