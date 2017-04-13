/**
 * Created by windy on 2017/4/12.
 */
BI.AnalysisETLMergeSheetFields = BI.inherit(BI.Widget, {

    props: {
        extraCls:"bi-analysis-etl-merge-fields"
    },

    render: function(){
        var self = this;
        this.model = new BI.AnalysisETLMergeSheetFieldsModel({});
        this.joinTableFields = null;
        return {
            type:"bi.vtape",
            cls: "join-merge-field",
            items:[{
                el: {
                    type: "bi.left_right_vertical_adapt",
                    cls: "add-merge-field",
                    height: 40,
                    llgap:10,
                    rrgap:10,
                    items: {
                        left: [{
                            type: "bi.label",
                            cls: "title",
                            text: BI.i18nText("BI-Merge_By_Following_Fields")
                        }],
                        right: [{
                            type: "bi.button",
                            text: BI.i18nText("BI-Add_The_Merged_Basis"),
                            height: 30,
                            listeners: [{
                                eventName: BI.Button.EVENT_CHANGE,
                                action: function(){
                                    self.addEmptyMerge();
                                    self.fireEvent(BI.AnalysisETLMergeSheetFields.MERGE_CHANGE, false)
                                }
                            }]
                        }]
                    }
                },
                height: 40
            }, {
                el: {
                    type:"bi.vertical",
                    items:[{
                        type: "bi.table_add_union",
                        tables: [],
                        listeners: [{
                            eventName: BI.TableAddUnion.EVENT_REMOVE_UNION,
                            action: function(index){
                                self.removeMergeField(index);
                                self.fireEvent(BI.AnalysisETLMergeSheetFields.MERGE_CHANGE, self.isValid())
                            }
                        }, {
                            eventName: BI.TableAddUnion.EVENT_CHANGE,
                            action: function(row, col, nValue, oValue){
                                self.changeMergeField(row, col, nValue);
                                self.fireEvent(BI.AnalysisETLMergeSheetFields.MERGE_CHANGE, self.isValid())
                            }
                        }],
                        ref: function(_ref){
                            self.joinTableFields = _ref;
                        }
                    }],
                    rgap:10,
                    lgap:10,
                    tgap:20
                }
            }]
        };
    },

    _populate : function () {
        this.joinTableFields.populate(this.model.getAllTables(), this.model.getCopyValue(ETLCst.FIELDS));
    },

    populate: function(m, options){
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },


    addEmptyMerge: function () {
        var joinFields = this.model.getCopyValue(ETLCst.FIELDS);
        joinFields.push([BI.TableAddUnion.UNION_FIELD_NULL, BI.TableAddUnion.UNION_FIELD_NULL]);
        this.model.set(ETLCst.FIELDS, joinFields);
        this._populate();
    },

    isValid : function () {
        return this.model.isValid();
    },

    removeMergeField : function (index) {
        var joinFields = this.model.getCopyValue(ETLCst.FIELDS);
        BI.removeAt(joinFields, index);
        this.model.set(ETLCst.FIELDS, joinFields);
        this._populate();
    },

    changeMergeField : function (row, col, nValue) {
        var joinFields = this.model.getCopyValue(ETLCst.FIELDS);
        BI.each(joinFields, function(i, fields){
            fields[col] === nValue && (joinFields[i][col] = -1);
        });
        joinFields[row][col] = nValue;
        this.model.set(ETLCst.FIELDS, joinFields);
        this._populate();
    },

    update: function(){
        return this.model.update();
    }
})
BI.AnalysisETLMergeSheetFields.MERGE_CHANGE = "MERGE_CHANGE";
BI.shortcut("bi.analysis_etl_merge_fields",BI.AnalysisETLMergeSheetFields)