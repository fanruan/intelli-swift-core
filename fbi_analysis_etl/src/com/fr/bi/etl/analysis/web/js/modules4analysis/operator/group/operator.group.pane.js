/**
 * Created by windy on 2017/4/7.
 */
BI.AnalysisETLOperatorGroupPane = BI.inherit(BI.Widget, {

    props: {
        extraCls :"bi-analysis-etl-operator-group-pane",
        value:ETLCst.ANALYSIS_TABLE_HISTORY_TABLE_MAP.GROUP
    },

    render: function(){
        var o = this.options;
        var self = this;
        this.itemsGroup = null;
        this.regions = {};
        this.model = new BI.AnalysisETLOperatorGroupPaneModel(o.items);
        var group = {
            type:"bi.group_select_fields_item_group",
            ref: function(_ref){
                self.itemsGroup = _ref;
            },
            listeners: [{
                eventName: BI.SelectSingleTableField.EVENT_CHANGE,
                action: function(){}
            }]
        };
        group[ETLCst.FIELDS] = [];

        return {
            type:"bi.center",
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
                        }, group]
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
                                el: {
                                    type: "bi.string_region",
                                    cls: "dimension-region",
                                    titleName: BI.i18nText("BI-Basic_Group"),
                                    regionType: BICst.REGION.DIMENSION1,
                                    ref: function(_ref){
                                        self.regions[BICst.REGION.DIMENSION1] = _ref;
                                    }
                                },
                                width:210
                            }, {
                                el: {
                                    type:"bi.string_region",
                                    cls: "target-region",
                                    titleName:BI.i18nText("BI-Basic_Statistic"),
                                    regionType: BICst.REGION.TARGET1,
                                    ref: function(_ref){
                                        self.regions[BICst.REGION.TARGET1] = _ref;
                                    }
                                },
                                width:210
                            }]
                        }]}]
                }]
            }]
        };
    },

    mounted: function(){
        this._populate();
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
                self.addDimensionByField({fieldInfo: field,regionType: item.getRegionType()});
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
                self.setSortBySortInfo({dimensions: dimensions, regionType: item.getRegionType()});
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
        if (BI.Utils.isDimensionRegionByRegionType(regionType)){
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
                    return self.getDimensionUsedById(id)
                },

                setDimensionUsedById : function (id, isSelected) {
                    return self.setDimensionUsedById(id, isSelected)
                },

                getDimensionGroupById : function (id) {
                    return self.getDimensionGroupById(id)
                },

                setDimensionGroupById : function (id, group) {
                    return self.setDimensionGroupById(id, group)
                },

                getDimension: function (id) {
                    return self.getDimension(id)
                },

                getDimensionNameById : function (id) {
                    return self.getDimensionNameById(id)
                },

                setDimensionNameById : function (id, name) {
                    return self.setDimensionNameById(id, name)
                },

                getTextByType : function () {
                    return self.getTextByType.apply(self, arguments)
                },

                getMinMaxValueForNumberCustomGroup : function (dId, callback) {
                    return self.getMinMaxValueForNumberCustomGroup.apply(self, [dm._src.fieldName, callback])
                },

                getValuesForCustomGroup : function (dId, callback) {
                    return self.getValuesForCustomGroup.apply(self, [dm._src.fieldName, function(res){
                        var items =[];
                        BI.each(res.value, function (i, item) {
                            items.push(item);
                        })
                        callback(items)
                    }])
                }
            },
            fieldName: dm._src.fieldName

        });
        dimension.on(BI.AbstractDimension.EVENT_DESTROY, function(){
            self.deleteDimension(id);
            dimension.destroy();
        });
        return dimension;
    },

    _populate: function(){
        var self = this;
        this._check();
        var parent = this.model.get(ETLCst.PARENTS)[0]
        this.itemsGroup.populate(parent[ETLCst.FIELDS]);
        var view = this.model.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY)
        var dimensions = this.model.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY)
        BI.each(this.regions, function (idx, region) {
            region.getRegion().populate();
            region.getRegion().element.droppable(self.dropField(region));
            region.getRegion().element.sortable(self.sortField(region));
        })
        BI.each(view, function(idx, vc){
            BI.each(vc, function(id, v){
                if(BI.has(dimensions, v)){
                    var dm = self.createDimension(v, idx, dimensions[v]);
                    self.regions[idx].addDimension(dm)
                }
            });
        });
        this.doCheck();
        this._refreshPreview();
    },

    //原controller
    populate : function (m, options) {
        this.model.populate(m);
        BI.extend(this.options, options);
        this._populate();
    },

    doCheck : function () {
        var self = this;
        var view = this.model.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY)
        BI.each(view,function(region, id){
            self.regions[region].setCommentVisible(BI.isEmpty(id));
        });
        this.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, this.model.isFieldValid(), BI.i18nText('BI-Please_Set_Group_Summary'))
    },

    _doModelCheck : function () {
        var found = this.model.check();
        if(found[0] === true) {
            this.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, found[1])
        }
        return found[0];
    },

    _check : function () {
        var found = this._doModelCheck()
        if (!found){
            this.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, this.model.createFields())
        } else {
            this.model.set(ETLCst.FIELDS, model.createFields());
        }
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, !found);
    },


    getDimensionUsedById : function (id) {
        return this.model.getDimensionUsedById(id);
    },

    setDimensionUsedById : function (id, isSelected) {
        this.model.setDimensionUsedById(id, isSelected);
        this._refreshPreview();
    },

    getDimension : function (id) {
        return this.model.getDimension(id);
    },

    getDimensionNameById : function (id) {
        return this.model.getDimensionNameById(id);
    },

    setDimensionNameById : function (id, name) {
        this.model.setDimensionNameById(id, name);
        this._refreshPreview();
    },

    getTextByType: function(id, groupOrSummary, fieldtype){
        return this.model.getTextByType(id, groupOrSummary, fieldtype);
    },

    getDimensionGroupById : function (id) {
        return this.model.getDimensionGroupById(id);
    },

    setDimensionGroupById : function (id, group) {
        this.model.setDimensionGroupById(id, group);
        this._doModelCheck();
        this._refreshPreview();
    },


    addDimensionByField : function (field) {
        var id = this.model.addDimensionByField(field);
        var dm = this.createDimension(id, field.regionType, this.model.getDimension(id), this.model.get(ETLCst.PARENTS)[0]);
        this.regions[field.regionType].addDimension(dm)
        this.regions[field.regionType].getRegion().element.scrollTop(BI.MAX)
        this.doCheck()
        this._refreshPreview();
    },

    setSortBySortInfo: function (sorted) {
        this.model.setSortBySortInfo(sorted);
        this.doCheck();
        this._refreshPreview();
    },


    deleteDimension: function (dId) {
        this.model.deleteDimension(dId);
        this.doCheck()
        this._doModelCheck()
        this._refreshPreview();
    },

    _refreshPreview : function () {
        this.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, this.model, this.model.isValid() ? this.options.value.operatorType : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
    },

    getMinMaxValueForNumberCustomGroup : function (fieldName,callback) {
        var table = {};
        table[ETLCst.ITEMS] = this.model.get(ETLCst.PARENTS)
        return BI.ETLReq.reqFieldMinMaxValues({
            table : table,
            field : fieldName
        }, callback)
    },

    getValuesForCustomGroup : function (fieldName,callback) {
        var table = {};
        table[ETLCst.ITEMS] = this.model.get(ETLCst.PARENTS)
        return BI.ETLReq.reqFieldValues({
            table : table,
            field : fieldName
        }, callback)
    },

    getValue: function(){
        return this.model.update();
    }
})

BI.shortcut(ETLCst.ANALYSIS_ETL_PAGES.GROUP_SUMMARY + ETLCst.ANALYSIS_TABLE_PANE , BI.AnalysisETLOperatorGroupPane);