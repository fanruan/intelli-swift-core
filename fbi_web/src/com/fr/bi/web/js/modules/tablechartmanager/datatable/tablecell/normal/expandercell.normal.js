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
                    type: "bi.icon_button",
                    cls: isExpanded === true ? "tree-expand-icon-type1" : "tree-collapse-icon-type1",
                    iconHeight: 25,
                    iconWidth: 25,
                    handler: function () {
                        o.expandCallback();
                    }
                },
                width: 25,
                height: 25
            })
        }

        //日期的需要format
        var text = o.text;
        var dGroup = BI.Utils.getDimensionGroupByID(o.dId);
        if (BI.isNotNull(dGroup) && BI.isNumeric(text)) {
            if(dGroup.type === BICst.GROUP.YMD) {
                var date = new Date(BI.parseInt(text));
                text = date.print("%Y-%X-%d");
            }
            if(dGroup.type === BICst.GROUP.S) {
                text = Date._QN[text];
            }
            if(dGroup.type === BICst.GROUP.M) {
                text = Date._MN[text - 1];
            }
            if(dGroup.type === BICst.GROUP.W) {
                text = Date._DN[text];
            }
        }


        var cls = "expander-cell-text";
        //交叉表的item
        var regionType = BI.Utils.getRegionTypeByDimensionID(o.dId);
        if (regionType === BICst.REGION.DIMENSION2) {
            cls += " cross-item-cell"
        }
        items.push({
            el: {
                type: "bi.label",
                text: text,
                title: text,
                cls: cls,
                height: 25,
                whiteSpace: "nowrap",
                textAlign: "left",
                lgap: 5
            },
            width: "fill"
        });

        var drillCombo = this._createDrillItems();
        if (BI.isNotNull(drillCombo)) {
            items.push({
                el: drillCombo,
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
        var self = this, o = this.options;
        var dId = o.dId, drillCallback = o.drillCallback;
        var widgetId = BI.Utils.getWidgetIDByDimensionID(dId);
        var allDims = BI.Utils.getAllDimDimensionIDs(widgetId);
        var allUsedDims = BI.Utils.getAllUsableDimDimensionIDs(widgetId);

        //并非有未勾选的维度就一定对于当前节点来说可以下钻，如果未勾选维度已被钻取，自然也不能再次钻取
        if (allDims.length > allUsedDims.length) {
            var drillMap = BI.Utils.getDrillByID(widgetId);
            var drilledIds = [], upDrillName = null;
            BI.each(drillMap, function (drId, ds) {
                //存在于钻取中
                if (ds.length > 0 && (dId === drId || ds[ds.length - 1].dId === dId)) {
                    if (ds.length > 1) {
                        upDrillName = BI.Utils.getDimensionNameByID(ds[ds.length - 2].dId);
                    } else {
                        upDrillName = BI.Utils.getDimensionNameByID(drId);
                    }
                }
                BI.each(ds, function (i, drs) {
                    drilledIds.push(drs.dId);
                });
            });
            var upName = BI.isNotNull(upDrillName) ? BI.i18nText("BI-Drill_up") + "(" + upDrillName + ")" : BI.i18nText("BI-Drill_up");

            var downChildren = [];
            //下钻节点的时候需要去掉那些已下钻的
            BI.each(allDims, function (i, dim) {
                if (!allUsedDims.contains(dim) && !drilledIds.contains(dim)) {
                    downChildren.push({
                        text: BI.Utils.getDimensionNameByID(dim),
                        value: dim
                    })
                }
            });
            if (BI.isNull(upDrillName) && BI.isEmptyArray(downChildren)) {
                return;
            }
            if (BI.isEmptyArray(downChildren)) {
                downChildren.push({
                    text: BI.i18nText("BI-Null"),
                    disabled: true
                })
            }

            var drillCombo = BI.createWidget({
                type: "bi.down_list_combo",
                chooseType: BI.Selection.None,
                title: BI.i18nText("BI-Drill"),
                cls: "drill-combo",
                height: 25
            });
            drillCombo.on(BI.DownListCombo.EVENT_BEFORE_POPUPVIEW, function () {
                var items = [[{
                    text: upName,
                    value: BI.NormalExpanderCell.UP_DRILL,
                    disabled: BI.isNull(upDrillName)
                }], [{
                    el: {
                        text: BI.i18nText("BI-Drill_down")
                    },
                    children: downChildren
                }]];
                this.populate(BI.deepClone(items));
            });
            drillCombo.on(BI.DownListCombo.EVENT_SON_VALUE_CHANGE, function (v) {
                drillCallback(v);
            });
            drillCombo.on(BI.DownListCombo.EVENT_CHANGE, function (v) {
                drillCallback(v);
            });
            drillCombo.setVisible(false);
            this.element.hover(function () {
                drillCombo.setVisible(true);
            }, function () {
                drillCombo.setVisible(false);
            });
            return drillCombo;
        }
    }
});
BI.extend(BI.NormalExpanderCell, {
    UP_DRILL: "___up__drill___"
});
$.shortcut("bi.normal_expander_cell", BI.NormalExpanderCell);