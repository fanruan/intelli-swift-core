BI.AnalysisETLOperatorAddColumnPaneTitleController = BI.inherit(BI.MVCController, {
    _defaultConfig: function () {
        return BI.extend(BI.AnalysisETLOperatorAddColumnPaneTitleController.superclass._defaultConfig.apply(this, arguments), {
        })
    },

    _init : function () {
        BI.AnalysisETLOperatorAddColumnPaneTitleController.superclass._init.apply(this, arguments);
        var o = this.options;
        this.columnNames = o.columnNames;

    },

    _getDefaultColumnName : function () {
        return BI.Utils.createDistinctName(this.columnNames, BI.i18nText("BI-New_Column_Name"))
    },

    checkName : function (name) {
        return BI.indexOf(this.columnNames, name) < 0
    },


    changeColumnType : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        var type = widget.typeCombo.getValue()[0];
        var acceptTypes = ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[type];
        widget.fieldTypeLabel.setVisible(type !== BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT)
        widget.fieldTypeSegment.setVisible(type !== BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT)
        widget.fieldTypeSegment.setEnabledValue(acceptTypes);
        widget.fieldTypeSegment.setValue(acceptTypes[0]);
        model.setAddColumnType(type)
        model.setFieldType(widget.fieldTypeSegment.getValue()[0])
    },

    changeFieldType:function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        model.setFieldType(widget.fieldTypeSegment.getValue()[0])
    },

    changeColumnName : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        model.setName(widget.nameEditor.getValue())
    },

    populate : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        var name = model.getName();
        if(BI.isNull(name)) {
            name = this._getDefaultColumnName();
            model.setName(name);
        }
        widget.nameEditor.setValue(name)
        widget.typeCombo.setValue(model.getAddColumnType())
        widget.fireEvent(BI.AnalysisETLOperatorAddColumnPaneTitle.EVENT_ADD_COLUMN_TYPE_CHANGE, widget.typeCombo.getValue())
        var acceptTypes = ETLCst.ANALYSIS_ADD_COLUMN_TYPE_ACCEPT_FIELDS[model.getAddColumnType()];
        widget.fieldTypeSegment.setEnabledValue(acceptTypes);
        widget.fieldTypeSegment.setValue(model.getFieldType());
        var type = model.getAddColumnType();
        widget.fieldTypeLabel.setVisible(type !== BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT)
        widget.fieldTypeSegment.setVisible(type !== BICst.ETL_ADD_COLUMN_TYPE.VALUE_CONVERT)
    }

})