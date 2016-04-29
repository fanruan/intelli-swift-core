BI.TopPointerSavePaneController = BI.inherit(BI.MVCController, {

    _construct : function (widget, model) {
        BI.TopPointerSavePaneController.superclass._construct.apply(this, arguments)
        this._editing = this.options.editing || false;
    },

    refreshButtonsStatus : function (widget) {
        widget.cancel.setEnable(this._editing);
        widget.save.setText(this._editing ? BI.i18nText("BI-Save") : BI.i18nText("BI-Edit"));
        this.refreshEditMask(widget);
    },

    refreshSaveButtonStatus : function (status, title, widget) {
        widget.save.setEnable(status);
        widget.save.setTitle(status === true ? "" : title);
    },

    setEnable : function (v, widget) {
        //正在编辑不允许修改
        if(this._editing === true){
            return;
        }
        widget.save.setEnable(v)
        widget.cancel.setEnable(v);
        this.refreshButtonsStatus(widget);
    },

    changeEditingState : function (widget) {
        this._editing = !this._editing;
        this.refreshButtonsStatus(widget);
    },

    setEditing : function(editing, widget) {
        this._editing = editing;
        this.refreshButtonsStatus(widget);
        this.refreshEditMask(widget)
    },

    isEditing : function () {
       return this._editing
    },

    refreshEditMask : function (widget) {
       this._editing === true ? this._clearMask(widget): this._showMask(widget);
    },

    _showMask:function (widget) {
        var masker = BI.Layers.make(widget.getName(),  widget.contentItemWidget, {
            render : {
                cls:"disable"
            }
        });
        BI.Layers.show(widget.getName());
    },

    _clearMask : function (widget) {
        BI.Layers.hide(widget.getName());
    },

    fireSaveOrEditEvent : function (widget) {
        widget.fireEvent(this._editing ? BI.TopPointerSavePane.EVENT_EDIT : BI.TopPointerSavePane.EVENT_SAVE, arguments)
    }
})