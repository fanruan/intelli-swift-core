BI.AnalysisETLOperatorGroupPaneController = BI.inherit(BI.MVCController, {


    populate : function (widget, model) {
        this._check(widget, model);
        var parent = model.get(ETLCst.PARENTS)[0]
        widget.items_group.populate(parent[ETLCst.FIELDS]);
        var view = model.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY)
        var dimensions = model.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY)
        BI.each(widget.regions, function (idx, region) {
            region.getRegion().empty();
            region.getRegion().element.droppable(widget.dropField(region));
            region.getRegion().element.sortable(widget.sortField(region));
        })
        BI.each(view, function(idx, vc){
            BI.each(vc, function(id, v){
                if(BI.has(dimensions, v)){
                    var dm = widget.createDimension(v, idx, dimensions[v]);
                    widget.regions[idx].addDimension(dm)
                }
            });
        });
        this.doCheck(widget, model);
        this._refreshPreview(widget, model);
    },

    doCheck : function (widget, model) {
        var view = model.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY)
        BI.each(view,function(region, id){
            widget.regions[region].setCommentVisible(BI.isEmpty(id));
        });
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, model.isFieldValid())
    },

    _doModelCheck : function (widget, model) {
        var parent = model.get(ETLCst.PARENTS)[0];
        var view = model.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY);
        var dimensions = model.get(BI.AnalysisETLOperatorGroupPaneModel.DIMKEY);
        var found = BI.some(view[BICst.REGION.DIMENSION1], function (i, v) {
            var  dimension = dimensions[v];
            var f = BI.find(parent[ETLCst.FIELDS], function (idx, field) {
                return field.field_name === dimension._src.field_name
            })
            if (BI.isNull(f)){
                widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-group_summary') + dimension["name"] + BI.i18nText('BI-Not_Fount'))
                return true;
            } else if (dimension.group.type !== BICst.GROUP.ID_GROUP &&  f.field_type !== dimension._src.field_type){
                widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-group_summary') + dimension["name"] + BI.i18nText('BI-Illegal_Field_Type'))
                return true;
            }
        })
        if (!found){
            found = BI.some(view[BICst.REGION.TARGET1], function (i, v) {
                var  dimension = dimensions[v];
                var f = BI.find(parent[ETLCst.FIELDS], function (idx, field) {
                    return field.field_name === dimension._src.field_name
                })
                if (BI.isNull(f)){
                    widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-group_summary') + dimension["name"] + BI.i18nText('BI-Not_Fount'))
                    return true;
                } else if (f.field_type !== BICst.COLUMN.NUMBER && dimension.group.type !== BICst.SUMMARY_TYPE.COUNT ){
                    widget.fireEvent(BI.TopPointerSavePane.EVENT_INVALID, BI.i18nText('BI-group_summary') + dimension["name"] + BI.i18nText('BI-Illegal_Field_Type'))
                    return true;
                }
            })
        }
        model.setValid(!found);
        return found;
    },

    _check : function (widget, model) {
        var found = this._doModelCheck(widget, model)
        if (!found){
            widget.fireEvent(BI.TopPointerSavePane.EVENT_FIELD_VALID, model.createFields())
        } else {
            model.set(ETLCst.FIELDS, model.createFields());
        }
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.VALID_CHANGE, !found);
    },


    getDimensionUsedById : function (id, widget, model) {
        return model.getDimensionUsedById(id)
    },

    setDimensionUsedById : function (id, isSelected, widget, model) {
        model.setDimensionUsedById(id, isSelected)
        this._refreshPreview(widget, model);
    },

    getDimension : function (id, widget, model) {
        return model.getDimension(id)
    },

    getDimensionNameById : function (id, widget, model) {
        return model.getDimensionNameById(id)
    },

    setDimensionNameById : function (id, name, widget, model) {
        model.setDimensionNameById(id, name)
        this._refreshPreview(widget, model);
    },

    getTextByType: function(id, groupOrSummary, fieldtype, widget, model){
        return model.getTextByType(id, groupOrSummary, fieldtype)
    },

    getDimensionGroupById : function (id, widget, model) {
        return model.getDimensionGroupById(id)
    },

    setDimensionGroupById : function (id, group, widget, model) {
        model.setDimensionGroupById(id, group)
        this._doModelCheck(widget, model)
        this._refreshPreview(widget, model);
    },


    addDimensionByField : function (field, widget, model) {
        var id = model.addDimensionByField(field);
        var dm = widget.createDimension(id, field.regionType, model.getDimension(id), model.get(ETLCst.PARENTS)[0]);
        widget.regions[field.regionType].addDimension(dm)
        this.doCheck(widget, model)
        this._refreshPreview(widget, model);
    },

    setSortBySortInfo: function (sorted,  widget, model) {
        model.setSortBySortInfo(sorted);
        this.doCheck(widget, model);
        this._refreshPreview(widget, model);
    },


    deleteDimension: function (dId,  widget, model) {
        model.deleteDimension(dId);
        this.doCheck(widget, model)
        this._doModelCheck(widget, model)
        this._refreshPreview(widget, model);
    },
    
    _refreshPreview : function (widget, model) {
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, model, model.isValid() ? widget.options.value.operatorType : ETLCst.ANALYSIS_TABLE_OPERATOR_KEY.ERROR)
    },

    getMinMaxValueForNumberCustomGroup : function (fieldName,callback, widget, model) {
        var table = {};
        table[ETLCst.ITEMS] = model.get(ETLCst.PARENTS)
        return BI.ETLReq.reqFieldMinMaxValues({
            table : table,
            field : fieldName
        }, callback)
    },

    getValuesForCustomGroup : function (fieldName,callback, widget, model) {
        var table = {};
        table[ETLCst.ITEMS] = model.get(ETLCst.PARENTS)
        return BI.ETLReq.reqFieldValues({
            table : table,
            field : fieldName
        }, callback)
    }

})