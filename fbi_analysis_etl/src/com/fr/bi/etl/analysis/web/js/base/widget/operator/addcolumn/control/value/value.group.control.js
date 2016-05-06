/**
 * Created by 小灰灰 on 2016/3/31.
 */
BI.AnalysisETLOperatorAddColumnValueGroupController = BI.inherit(BI.MVCController, {

    _construct : function (widget, model) {
        this.childPane = {};
        this.childValid = {};
    },

    _checkCanSave : function (widget, model) {
        return BI.isNull(BI.find(this.childValid, function (idx, item) {
            return item === false;
        })) && (this.editorValid === true || !widget.checkBox.isSelected()) && (model.get(ETLCst.ITEMS).length > 0);
    },

    addCondition : function (widget, model) {
        var v = widget.combo.getValue()[0];
        var f = model.getFieldByValue(v);
        this._addCondition(f, {}, widget, model);
        this._buildItems(model);
        this.checkValid(widget, model)
    },

    clickCheckBox : function (widget, model) {
        model.set("showOther", widget.checkBox.isSelected())
        widget.editor.setEnable(widget.checkBox.isSelected());
        if(widget.checkBox.isSelected() === true) {
            widget.editor.editor.focus();
        }
        this.checkValid(widget, model)
    },

    setOtherValue : function (widget, model) {
        model.set('other', widget.editor.getValue());
    },

    populate : function (widget, model) {
        var fields = model.get(ETLCst.FIELDS)
        widget.combo.populate(fields);
        widget.pane.empty();
       
        widget.combo.setValue(fields.length > 0  ? fields[0].value : null);
        widget.checkBox.setSelected(model.get('showOther') || false);
        widget.createEditor(this.options.field_type, model.get('other') || "");
        this.clickCheckBox(widget, model)
        var items = model.get(ETLCst.ITEMS);
        var self = this;
        BI.each(items, function (idx, item) {
            var field = model.getFieldByValue(item.field)
            self._addCondition(field, item, widget, model)
        })
        this.checkValid(widget, model)
    },

    _addCondition : function (field, value, widget, model) {
        var pane =  widget.pane.addItem(widget.createItem(field, value, this.options.field_type));
        this.childPane[pane.getName()] = pane;
        return pane;
    },

    setValid : function (key, valid, widget, model) {
        this.childValid[key] = valid;
        this.checkValid(widget, model)
    },

    checkValid : function (widget, model) {
        widget.fireEvent(BI.TopPointerSavePane.EVENT_CHECK_SAVE_STATUS, this._checkCanSave(widget, model))
    },

    setEditorValid : function (valid) {
        this.editorValid =   valid
    },

    changeItem : function (widget, model) {
        this._buildItems(model);
    },

    deleteItem : function (key, widget, model) {
        var pane = this.childPane[key];
        if(BI.isNotNull(pane)) {
            pane.destroy()
        }
        delete this.childPane[key];
        delete this.childValid[key];
        this._buildItems(model);
        this.checkValid(widget, model)
    },

    _buildItems : function (model) {
        var items = [];
        BI.each(this.childPane, function (idx, item) {
           items.push(item.update())
        })
        model.set(ETLCst.ITEMS, items);
    },

    changeFieldType : function (field_type, widget, model) {
        this.options.field_type = field_type;
        var oldValue = widget.combo.getValue();
        this._construct(widget, model)
        this.populate(widget, model)
        widget.combo.setValue(oldValue)
    }
})