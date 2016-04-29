/**
 * Created by roy on 16/3/10.
 */
BI.DetailTableCommonTableSet = BI.inherit(BI.Widget, {
    constants: {
        TABLE_LABEL_HEIGHT: 20,
        TABLE_LABEL_VGAP: 5,
        TABLE_LABEL_BORDER: 1,
        TABLE_LABEL_WIDTH: 60,
        lineColor: "#c4c6c6"
    },

    _defaultConfig: function () {
        return BI.extend(BI.DetailTableCommonTableSet.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "detail-table-common-table-set",
            items: []
        })
    },

    _init: function () {
        var o = this.options;
        this.radios = [];
        BI.DetailTableCommonTableSet.superclass._init.apply(this, arguments);
        this.tree = BI.createWidget(
            {
                type: "bi.branch_tree",
                expander: {
                    el: {
                        type: "bi.label",
                        cls: "relation-setting-label",
                        width: 60,
                        height: 20
                    }
                },
                el: {
                    layouts: [{
                        type: "bi.horizontal_auto"
                    }]
                },
                items: this._createTreeItems(o.items)
            }
        );


        this.pane = BI.createWidget({
            type: "bi.vertical",
            //element: this.element,
            items: [this.tree],
            lgap: 30
        });


        this.container = BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            scrollable: true,
            items: [{
                el: this.pane
            }]
        });

        this._drawLines(30);

    },


    _createTreeItems: function (items) {
        var c = this.constants;
        var itemsCp = BI.deepClone(items);
        BI.each(itemsCp, function (i, item) {
            BI.each(item.children, function (i, item) {
                item.type = "bi.absolute";
                item.items = [{
                    el: {
                        type: "bi.label",
                        cls: "relation-setting-label",
                        text: item.text,
                        title: item.text,
                        value: item.value,
                        width: 60,
                        textHeight: 20
                    },
                    top: 5,
                    bottom: 5,
                    left: 20,
                    right: 0
                }];
                item.width = 80 + c.TABLE_LABEL_BORDER * 2;
                item.height = 30 + c.TABLE_LABEL_BORDER * 2;
            })
        });
        return itemsCp;
    },

    _drawRadios: function (v, item) {
        var self = this;
        var selected = item.selected || false;
        var radio = BI.createWidget({
            type: "bi.radio",
            value: item.value,
            selected: selected
        });

        radio.on(BI.Radio.EVENT_CHANGE, function (v) {
            self.setValue(v);
            self.fireEvent(BI.DetailTableCommonTableSet.EVENT_CHANGE, self.getValue());
        });
        this.radios.push(radio);
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: radio,
                top: v,
                left: 8
            }]
        });
    },

    _drawLines: function (lgap) {
        var self = this, c = this.constants;
        this.svg = BI.createWidget({
            type: "bi.svg"
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.pane,
            items: [{
                el: this.svg,
                top: 0,
                bottom: 0,
                left: 0,
                right: 0
            }],
            top: 0,
            left: 0,
            bottom: 0,
            right: 0
        });

        var items = this.options.items;
        var path = "";
        var border = c.TABLE_LABEL_BORDER * 2;
        var allHeight = 0;
        BI.each(items, function (j, item) {
            var children = item.children;
            BI.each(children, function (i, item) {
                //画子节点横线
                var v = 32 * i + allHeight;
                var hstart = c.TABLE_LABEL_WIDTH + 10 + border + lgap;
                var vstart = c.TABLE_LABEL_VGAP + c.TABLE_LABEL_BORDER + 10 + v;
                var hend = c.TABLE_LABEL_WIDTH + 20 + border + lgap;
                var vend = c.TABLE_LABEL_VGAP + c.TABLE_LABEL_BORDER + 10 + v;
                path += "M" + hstart + "," + vstart + "L" + hend + "," + vend;
            });
            //画竖线
            var v = 32 * (BI.size(children) - 1);
            var hstart = c.TABLE_LABEL_WIDTH + 10 + border + lgap;
            var vstart = c.TABLE_LABEL_VGAP + c.TABLE_LABEL_BORDER + 10 + allHeight;
            var vend = c.TABLE_LABEL_VGAP + c.TABLE_LABEL_BORDER + 10 + v + allHeight;
            path += "M" + hstart + "," + vstart + "L" + hstart + "," + vend;
            //画父节点的横线
            var hstart = c.TABLE_LABEL_WIDTH + border + lgap;
            var vstart = 32 * (BI.size(children)) / 2 + allHeight;
            var hend = c.TABLE_LABEL_WIDTH + 10 + border + lgap;
            path += "M" + hstart + "," + vstart + "L" + hend + "," + vstart;
            self._drawRadios(vstart - 6.5, item);
            allHeight += 32 * children.length;
        });
        this.svg.path(path)
            .attr({
                stroke: self.constants.lineColor,
                'stroke-dasharray': '-'
            });
    },

    setValue: function (v) {
        var value = "";
        BI.isArray(v) ? value = v[0] : value = v;
        BI.each(this.radios, function (i, radio) {
            BI.isEqual(value, radio.getValue()) ? radio.setSelected(true) : radio.setSelected(false);
        })
    },

    getValue: function () {
        var result = [];
        BI.each(this.radios, function (i, radio) {
            if (radio.isSelected()) {
                result.push(radio.getValue());
            }
        });
        return result;
    },

    populate: function (items) {
        this.options.items = items;
        this.svg.destroy();
        BI.each(this.radios, function (i, radio) {
            radio.destroy();
        });
        var treeItems = this._createTreeItems(items);
        this.tree.populate(treeItems);
        this._drawLines(30);
    }
});
BI.DetailTableCommonTableSet.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.detail_table_common_table_set", BI.DetailTableCommonTableSet);