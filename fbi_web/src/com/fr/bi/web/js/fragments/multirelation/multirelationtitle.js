/**
 * Created by zcf on 2017/2/10.
 */
BI.MultiRelationTitle=BI.inherit(BI.Widget,{
    _defaultConfig:function () {
        return BI.extend(BI.MultiRelationTitle.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-relation-expander",
            relationsArray: []
        });
    },

    _init:function () {
        BI.MultiRelationTitle.superclass._init.apply(this, arguments);

        var title = BI.createWidget({
            type: "bi.label",
            element:this.element,
            cls: "multi-relation-expander-title",
            height: 40,
            textAlign: BI.HorizontalAlign.Left,
            text: this._createText()
        });
    },

    _getTableName: function (field) {
        return field[BICst.JSON_KEYS.TABLE_TRAN_NAME];
    },

    _createText: function () {
        var o = this.options;
        var text = "";
        return this._getTableName(BI.last(o.relationsArray[0]).foreignKey)
            + "——" + this._getTableName(BI.first(o.relationsArray[0]).primaryKey);
    },

    populate:function () {

    }
});
$.shortcut("bi.multi_relation_title",BI.MultiRelationTitle);