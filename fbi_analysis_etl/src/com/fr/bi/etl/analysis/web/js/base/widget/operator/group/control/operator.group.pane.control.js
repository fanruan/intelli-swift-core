BI.AnalysisETLOperatorGroupPaneController = BI.inherit(BI.MVCController, {


    populate : function (widget, model) {
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
                    var dm = widget.createDimension(v, idx, dimensions[v], parent);
                    widget.regions[idx].addDimension(dm)
                }
            });
        });
        this._checkStatus(widget, model)
        this._refreshPreview(widget, model);
    },

    _checkStatus : function (widget, model) {
        var view = model.get(BI.AnalysisETLOperatorGroupPaneModel.VIEWKEY)
        BI.each(view,function(region, id){
            widget.regions[region].setCommentVisible(BI.isEmpty(id));
        });
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, model.isValid())
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
        return model.setDimensionGroupById(id, group)
        this._refreshPreview(widget, model);
    },


    addDimensionByField : function (field, widget, model) {
        var id = model.addDimensionByField(field);
        var dm = widget.createDimension(id, field.regionType, model.getDimension(id), model.get(ETLCst.PARENTS)[0]);
        widget.regions[field.regionType].addDimension(dm)
        this._checkStatus(widget, model)
        this._refreshPreview(widget, model);
    },

    setSortBySortInfo: function (sorted,  widget, model) {
        model.setSortBySortInfo(sorted);
        this._checkStatus(widget, model);
        this._refreshPreview(widget, model);
    },


    deleteDimension: function (dId,  widget, model) {
        model.deleteDimension(dId);
        this._checkStatus(widget, model)
        this._refreshPreview(widget, model);
    },
    
    _refreshPreview : function (widget, model) {
        widget.fireEvent(BI.AnalysisETLOperatorAbstractController.PREVIEW_CHANGE, model, widget.options.value.operatorType)
    }


})