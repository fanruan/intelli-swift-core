BI.AnalysisETLOperatorGroupPane = FR.extend(BI.MVCWidget, {


    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorGroupPane.superclass._defaultConfig.apply(this, arguments), {
            extraCls :"bi-analysis-etl-operator-group-pane",
            value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.GROUP
        })
    },
    
    _initModel : function () {
        return BI.AnalysisETLOperatorGroupPaneModel;
    },

    _initController : function () {
        return BI.AnalysisETLOperatorGroupPaneController;
    },


    _initView: function () {
        var o = this.options;
        var self = this;
        var group = {
            type:"bi.group_select_fields_item_group"
        };
        group[ETLCst.FIELDS] = [];
        this.items_group = BI.createWidget(group)
        this.items_group.on(BI.SelectSingleTableField.EVENT_CHANGE, function () {

        });
        this.regions = {};
        this.regions[BICst.REGION.DIMENSION1] = BI.createWidget({
            type: "bi.string_region",
            cls: "dimension-region",
            titleName: BI.i18nText("BI-Group"),
            regionType: BICst.REGION.DIMENSION1
        });

        this.regions[BICst.REGION.TARGET1] = BI.createWidget({
            type:"bi.string_region",
            cls: "target-region",
            titleName:BI.i18nText("BI-Statistic"),
            regionType: BICst.REGION.TARGET1
        });

        
        BI.createWidget({
            type:"bi.center",
            element:this.element,
            items:[{
                type:"bi.htape",
                rgap : 10,
                items : [{
                    el:{
                        type:"bi.vtape",
                        cls:"select-field",
                        items:[{
                            type:"bi.layout",
                            height:10
                        },this.items_group]
                    },
                    width:190
                }, {
                    type:"bi.vtape",
                    scrollx:true,
                    items:[{
                        type:"bi.vtape",
                        tgap:10,
                        bgap:10,
                        items:[{
                            type:"bi.htape",
                            rgap:10,
                            items:[{
                                el:this.regions[BICst.REGION.DIMENSION1],
                                width:210
                            }, {
                                el:this.regions[BICst.REGION.TARGET1],
                                width:210
                            }]
                        }]}]
                }]
            }]
        })
    },

    dropField: function (item) {
        var self = this;
        return {
            accept:".group-select-fields-item-button",
            tolerance:"pointer",
            drop: function(event,ui){
                item.removePlaceHolder();
                var helper = ui.helper;
                var field = helper.data("field");
                self.controller.addDimensionByField({fieldInfo: field,regionType: item.getRegionType()});
            },
            over: function(event, ui){
                item.addPlaceHolder();
            },
            out: function(event, ui){
                item.removePlaceHolder()
            }
        };
    },

    sortField: function(item){
        var self = this;
        return {
            containment: item.element,//sortable 对象的(范围)父区域
            tolerance: "pointer",
            items: ".bi-string-dimension-container, .bi-string-target-container",
            scroll: false,
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            update: function (event, ui) {
                var dimensions = item.getAllDimensions();
                self.controller.setSortBySortInfo({dimensions: dimensions, regionType: item.getRegionType()});
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        }
    },
    
    createDimension:function(id, regionType, dm){
        var self = this;
        var cls = "bi-string-target-container";
        if (BI.parseInt(regionType) === BI.parseInt(BICst.REGION.DIMENSION1)){
            cls = "bi-string-dimension-container";
        }
        var dimension = BI.createWidget({
            type: BI.GroupStatistic.prototype._getDimensionTypeByRegionAndDimensionType(regionType, dm.type),
            cls: cls + " sign-style-editor",
            attributes: {
                dimensionid: id
            },
            info: dm,
            dId: id,
            model: {
                getDimensionUsedById : function (id) {
                    return self.controller.getDimensionUsedById(id)
                },

                setDimensionUsedById : function (id, isSelected) {
                    return self.controller.setDimensionUsedById(id, isSelected)
                },

                getDimensionGroupById : function (id) {
                    return self.controller.getDimensionGroupById(id)
                },

                setDimensionGroupById : function (id, group) {
                    return self.controller.setDimensionGroupById(id, group)
                },

                getDimension: function (id) {
                    return self.controller.getDimension(id)
                },

                getDimensionNameById : function (id) {
                    return self.controller.getDimensionNameById(id)
                },

                setDimensionNameById : function (id, name) {
                    return self.controller.setDimensionNameById(id, name)
                },

                getTextByType : function () {
                    return self.controller.getTextByType.apply(self.controller, arguments)
                },

                getMinMaxValueForNumberCustomGroup : function (dId, callback) {
                    return self.controller.getMinMaxValueForNumberCustomGroup.apply(self.controller, [dm._src.field_name, callback])
                },

                getValuesForCustomGroup : function (dId, callback) {
                    return self.controller.getValuesForCustomGroup.apply(self.controller, [dm._src.field_name, function(res){
                        var items =[];
                        BI.each(res.value, function (i, item) {
                            items.push(item);
                        })
                        callback(items)
                    }])
                }
            },
            fieldName: dm._src.field_name
          
        });
        dimension.on(BI.AbstractDimension.EVENT_DESTROY, function(){
            self.controller.deleteDimension(id);
            dimension.destroy();
        });
        return dimension;
    }
    

})

$.shortcut(ETLCst.ANALYSIS_ETL_PAGES.GROUP_SUMMARY + ETLCst.ANALYSIS_TABLE_PANE , BI.AnalysisETLOperatorGroupPane);