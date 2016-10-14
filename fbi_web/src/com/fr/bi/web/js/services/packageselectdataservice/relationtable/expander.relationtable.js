/**
 * create by young
 * 相关表expander
 */
BI.RelationTableExpander = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.RelationTableExpander.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-relation-table-expander",
            el: {},
            popup: {
                items: [],
                itemsCreator: BI.emptyFn
            }
        });
    },

    _init: function () {
        BI.RelationTableExpander.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.trigger = BI.createWidget(o.el);
        this.expander = BI.createWidget({
            type: "bi.expander",
            element: this.element,
            el: this.trigger,
            popup: BI.extend({
                type: "bi.select_data_loader"
            }, o.popup)
        });
        this.expander.on(BI.Controller.EVENT_CHANGE, function (type) {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
    },

    doBehavior: function () {
        this.trigger.doRedMark.apply(this.trigger, arguments);
        this.expander.doBehavior.apply(this.expander, arguments);
    },

    setValue: function (v) {
        this.expander.setValue(v);
    },
    getValue: function () {
        return this.expander.getValue() || [];
    },

    getAllButtons: function(){
        return this.expander.getView().getAllButtons();
    },

    showView: function(){
        this.expander.showView();
    },

    hideView: function(){
        this.expander.hideView();
    },

    isExpanded: function () {
        return this.expander.isExpanded();
    },

    getAllLeaves: function () {
        return this.expander.getAllLeaves();
    },

    getNodeById: function (id) {
        return this.expander.getNodeById(id);
    },

    getNodeByValue: function (value) {
        return this.expander.getNodeByValue(value);
    }
});
$.shortcut("bi.relation_table_expander", BI.RelationTableExpander);