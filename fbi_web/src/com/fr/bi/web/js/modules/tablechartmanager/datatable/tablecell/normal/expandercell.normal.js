/**
 * created by young
 * 表格中可展开节点——维度展开值
 */
BI.NormalExpanderCell = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.NormalExpanderCell.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-normal-expander-cell",
            layer: 0,
            width: "100%",
            height: "100%"
        })
    },

    _init: function () {
        BI.NormalExpanderCell.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var needExpand = o.needExpand, isExpanded = o.isExpanded;
        var items = [];
        if (needExpand === true) {
            items.push({
                el: {
                    type: "bi.center_adapt",
                    items: [{
                        type: "bi.icon_button",
                        width: 25,
                        height: 25,
                        iconHeight: 25,
                        iconWidth: 25,
                        cls: isExpanded === true ? "tree-expand-icon-type1" : "tree-collapse-icon-type1",
                        handler: function () {
                            o.expandCallback();
                        }
                    }]
                },
                width: 25,
                height: o.height,
            })
        }

        //日期的需要format
        var text = o.text;
        var dGroup = BI.Utils.getDimensionGroupByID(o.dId);
        if (BI.isNotNull(dGroup) && BI.isNumeric(text)) {
            if (dGroup.type === BICst.GROUP.YMD) {
                var date = new Date(BI.parseInt(text));
                text = date.print("%Y-%X-%d");
            }
            if (dGroup.type === BICst.GROUP.YMDHMS) {
                var date = new Date(BI.parseInt(text));
                text = date.print("%Y-%X-%d  %H:%M:%S");
            }
            if (dGroup.type === BICst.GROUP.S) {
                text = BICst.FULL_QUARTER_NAMES[text];
            }
            if (dGroup.type === BICst.GROUP.M) {
                text = BICst.FULL_MONTH_NAMES[text];
            }
            if (dGroup.type === BICst.GROUP.W) {
                text = BICst.FULL_WEEK_NAMES[text];
            }
        }

        //科学计数法的显示
        var fieldType = BI.Utils.getFieldTypeByDimensionID(o.dId);
        if (fieldType === BICst.COLUMN.NUMBER &&
            BI.isNotNull(dGroup) &&
            dGroup.type === BICst.GROUP.ID_GROUP &&
            BI.isNumeric(text)) {
            text = BI.parseFloat(text);
        }

        var cls = "expander-cell-text";
        //交叉表的item
        var regionType = BI.Utils.getRegionTypeByDimensionID(o.dId);
        if (BI.Utils.isDimensionRegion2ByRegionType(regionType)) {
            cls += " cross-item-cell"
        }
        items.push({
            el: {
                type: "bi.label",
                text: text,
                title: text,
                cls: cls,
                height: o.height,
                whiteSpace: "nowrap",
                textAlign: "left",
                lgap: 5
            },
            width: "fill"
        });

        var drillCombo = this._createDrillItems();
        if (BI.isNotNull(drillCombo)) {
            items.push({
                el: {
                    type: "bi.vertical_adapt",
                    items: [drillCombo]
                },
                width: 25
            });
        }
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.htape",
                    items: items
                },
                left: o.layer * 30,
                right: 0,
                top: 0,
                bottom: 0
            }]
        });

        if (BI.isNotNull(o.styles) && BI.isObject(o.styles)) {
            this.element.css(o.styles);
        }
    },

    /**
     * 结构 {1: [{dId: 2, value: x}, {dId: 3, value: y}], 5: [{dId: 4, value: z}}
     * 此结构即为 维度1钻取两次，2-3，最终展示的维度为3  维度5钻取一次，4，最终显示4
     * 上钻名称：从drill中找到currId，找到，取数组倒数第二个或key，找不到为null；下钻items：所有不在drill里的used=false的
     * 下钻逻辑：当前点击的值currValue所在维度currId，先从drill中找，
     * 1、如果找到，说明一定是钻取后的结果，往此节点drill数组下继续添加{dId: currId, value: currValue}；2、没找到，即新开一个节点
     * 上钻逻辑：一定能找到currId，在drill中，找到即从数组中pop出去
     * @private
     */
    _createDrillItems: function () {
        var o = this.options;
        var dId = o.dId;
        //并非有未勾选的维度就一定对于当前节点来说可以下钻，如果未勾选维度已被钻取，自然也不能再次钻取
        if (BI.isNotEmptyArray(BI.Utils.getDrillDownDIdsByWidgetId(BI.Utils.getWidgetIDByDimensionID(dId)))
            || BI.isNotNull(BI.Utils.getDrillUpDimensionIdByDimensionId(dId))) {
            var button = BI.createWidget({
                type: "bi.center_adapt",
                invisible: true,
                items: [{
                    type: "bi.icon_button",
                    cls: "table-drill-up-down",
                    height: 25,
                    handler: function () {
                        o.drillCallback()
                    }
                }]
            });
            this.element.hover(function () {
                button.setVisible(true);
            }, function () {
                button.setVisible(false);
            });
            return button;
        }
    }
});
BI.extend(BI.NormalExpanderCell, {
    UP_DRILL: "___up__drill___"
});
$.shortcut("bi.normal_expander_cell", BI.NormalExpanderCell);