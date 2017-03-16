/**
 * Created by zcf on 2017/1/18.
 */
BI.DetailSelectDataLevel0ExcelNode = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.DetailSelectDataLevel0ExcelNode.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-detail-select-data-level0-node",
            id: "",
            pId: "",
            layer: 0,
            open: false,
            height: 25
        })
    },
    _init: function () {
        BI.DetailSelectDataLevel0ExcelNode.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.node = BI.createWidget({
            type: "bi.select_data_level0_node",
            layer: o.layer,
            id: o.id,
            pId: o.pId,
            open: o.open,
            text: o.text,
            value: o.value,
            keyword: o.keyword,
            title: o.title
        });
        this.node.on(BI.Controller.EVENT_CHANGE, function () {
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });
        BI.Broadcasts.on(BICst.BROADCAST.DIMENSIONS_PREFIX + o.wId, function () {
            self.setValue([]);
        });
        var setExcelButton=BI.createWidget({
            type:"bi.update_excel_combo",
            tableId:o.id,
            height:o.height,
            width:90
        });

        BI.createWidget({
            type:"bi.htape",
            element:this.element,
            items:[{
                el:this.node,
                width:"fill"
            },{
                el:setExcelButton,
                width:90
            }]
        });
    },

    doRedMark: function () {
        this.node.doRedMark.apply(this.node, arguments);
    },

    unRedMark: function () {
        this.node.unRedMark.apply(this.node, arguments);
    },

    setSelected: function (b) {
        return this.node.setSelected(b);
    },

    isSelected: function () {
        return this.node.isSelected();
    },

    isOpened: function () {
        return this.node.isOpened();
    },

    setOpened: function (v) {
        this.node.setOpened(v);
    },

    setValue: function (items) {
        var vals = [];
        BI.each(items, function (i, item) {
            if (BI.isString(item)) {
                vals.push(BI.Utils.getFieldNameByID(item));
            } else {
                if (BI.isNotNull(item.field_id)) {
                    var name = BI.Utils.getFieldNameByID(item.field_id);

                    if (BI.isNotNull(item.group)) {
                        switch (item.group.type) {
                            case BICst.GROUP.Y:
                                name += "(" + BI.i18nText("BI-Year") + ")";
                                break;
                            case BICst.GROUP.M:
                                name += "(" + BI.i18nText("BI-Month") + ")";
                                break;
                            case BICst.GROUP.YMD:
                                name += "(" + BI.i18nText("BI-Date") + ")";
                                break;
                            case BICst.GROUP.S:
                                name += "(" + BI.i18nText("BI-Quarter") + ")";
                                break;
                        }
                    }
                    vals.push(name);
                }
            }
        });
        this.node.setValue(vals);
    },

    setEnable: function (b) {
        BI.DetailSelectDataLevel0ExcelNode.superclass.setEnable.apply(this, arguments);
        !b && this.node.isOpened() && this.node.triggerCollapse();
    }
});

$.shortcut("bi.detail_select_data_level0_excel_node", BI.DetailSelectDataLevel0ExcelNode);