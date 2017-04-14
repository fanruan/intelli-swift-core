/**
 * Created by windy on 2017/4/13.
 */
BI.MergeHistory = BI.inherit(BI.Widget,  {

    props: {
        extraCls: "bi-analysis-etl-merge-history"
    },

    render: function(){
        var self = this, o = this.options;
        this.model = new BI.AnalysisETLMergeHistoryModel(o.items);
        this.trigger = BI.Utils.triggerPreview();
        return {
            type:"bi.htape",
            items:[{
                type:"bi.layout",
                width:20
            },{
                el:{
                    type:"bi.vtape",
                    items:[{
                        el :{
                            type:"bi.left_right_vertical_adapt",
                            cls:"title border",
                            llgap:10,
                            rrgap:10,
                            height:40,
                            items:{
                                left:[{
                                    type:"bi.label",
                                    text:BI.i18nText("BI-ETL_Merge_History")
                                }],
                                right:[{
                                    type:"bi.button",
                                    level:"ignore",
                                    height:30,
                                    text:BI.i18nText("BI-Basic_Close"),
                                    title:BI.i18nText("BI-Basic_Close"),
                                    handler : function () {
                                        self.fireEvent(BI.MergeHistory.CANCEL)
                                    }
                                }]
                            }
                        },
                        height:40
                    }, {
                        type:"bi.layout",
                        height:10
                    }, {
                        type:"bi.htape",
                        items:[{
                            type:"bi.vertical",
                            cls:"border",
                            items:[{
                                type: "bi.etl_branch_relation",
                                hgap:40,
                                ref: function(_ref){
                                    self.path = _ref;
                                }
                            }],
                            width:300
                        }, {
                            type:"bi.layout",
                            width:10
                        }, {
                            el:{
                                type:"bi.vtape",
                                items:[{
                                    el :  {
                                        type:"bi.vtape",
                                        cls:"border",
                                        items:[{
                                            type:"bi.left_right_vertical_adapt",
                                            cls:"title content",
                                            llgap:10,
                                            items:{
                                                left:[{
                                                    type:"bi.label",
                                                    ref: function(_ref){
                                                        self.contentTitle = _ref;
                                                    }
                                                }]
                                            },
                                            height:40
                                        }, {
                                            el: {
                                                type:"bi.button_group",
                                                ref: function(_ref){
                                                    self.contentView = _ref;
                                                },
                                                layouts: [{
                                                    type:"bi.vertical",
                                                    scrollx:false,
                                                    scrolly:false
                                                }]
                                            }
                                        }]
                                    },
                                    height:300
                                }, {
                                    type:"bi.layout",
                                    height:10
                                }, {
                                    el : {
                                        type:"bi.border",
                                        cls:"border",
                                        items:{
                                            north:{
                                                type:"bi.layout",
                                                height:10
                                            },
                                            center: {
                                                type: "bi.analysis_etl_preview_table",
                                                cls: "bi-analysis-tab-table",
                                                header:[],
                                                items:[],
                                                ref: function(_ref){
                                                    self.previewTable = _ref;
                                                }
                                            },
                                            south:{
                                                type:"bi.layout",
                                                height:10
                                            },
                                            east:{
                                                type:"bi.layout",
                                                width:10
                                            },
                                            west:{
                                                type:"bi.layout",
                                                width:10
                                            },
                                        }
                                    }
                                }]
                            }
                        }]
                    }, {
                        type:"bi.layout",
                        height:10
                    }]
                }

            }, {
                type:"bi.layout",
                width:20
            }]
        }
    },

    createButton : function (item) {
        var button = BI.createWidget(BI.extend(item, {
            type:"bi.button",
            cls:"single_operator",
            level:"ignore",
            forceSelected :true,
            width:110,
            height:30,
            value:item.id,
            text:item.text
        }))
        var self = this;
        button.on(BI.Controller.EVENT_CHANGE, function () {
            self.showContentAndPreview(item.id);
        })
        return button;
    },

    //原controller
    _populate : function () {
        this.buttonGroup = [];
        var self = this;
        var items = this.model.get(ETLCst.ITEMS);


        var branchArray = [];
        BI.each(items, function (idx, item) {
            var button = this.createButton(item);
            self.buttonGroup.push(button);
            branchArray.push({
                el:button,
                id:item.id,
                pId:item.pId
            })
        });
        this.path.populate(branchArray)
        //获取根节点
        var root =  this.path.tree.getRoot();
        if(BI.isNotNull(root)){
            var child = root.getFirstChild();
            if(BI.isNotNull(child)) {
                this.showContentAndPreview(child.id)
            }
        }
    },

    popualte: function(m, options){
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    showContentAndPreview : function (id) {
        if( this.branchValue === id) {
            return;
        }
        var items = this.model.get(ETLCst.ITEMS);
        this._setBranchValue(id)
        var item = BI.find(items, function (idx, item) {
            return item.id === id;
        });
        var name = item.text;
        var paneType = item.operatorType + ETLCst.ANALYSIS_TABLE_PANE
        var pane = BI.createWidget({
            type:paneType,
            width:"100%",
            height:"100%"
        })

        this.contentTitle.setText(name);
        this.contentView.empty();
        this.contentView.populate([pane])
        pane.populate(item);
        BI.Layers.remove(this.getName());
        BI.Layers.make(this.getName(), this.contentView,  {
            render: {
                //cls:"disable"
            }
        });
        BI.Layers.show(this.getName());
        this._refreshPopData();
    },

    _setBranchValue: function (id) {
        this.branchValue = id;
        BI.each(this.buttonGroup, function (idx, item) {
            item.setSelected(item.getValue() === id);
        })
    },

    _refreshPopData : function (){
        this.trigger(this.previewTable, this.model, ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.NULL, ETLCst.PREVIEW.MERGE)
    }
})
BI.MergeHistory.CANCEL="cancel_view";
BI.shortcut("bi.analysis_etl_merge_history", BI.MergeHistory);