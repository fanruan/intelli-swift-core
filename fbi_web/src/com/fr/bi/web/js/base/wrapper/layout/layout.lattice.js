/**
 * 靠左对齐的自由浮动布局
 * @class BI.LatticeLayout
 * @extends BI.Layout
 *
 * @cfg {JSON} options 配置属性
 * @cfg {Number} [hgap=0] 水平间隙
 * @cfg {Number} [vgap=0] 垂直间隙
 */
BI.LatticeLayout = BI.inherit(BI.Layout, {
    _defaultConfig: function () {
        return BI.extend(BI.LatticeLayout.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-lattice-layout clearfix"
            //columnSize: [0.2, 0.2, 0.6],
        });
    },
    _init: function () {
        BI.LatticeLayout.superclass._init.apply(this, arguments);
        this.populate(this.options.items);
    },

    _addElement: function (i, item) {
        var o = this.options;
        if (!this.hasWidget(this.getName() + i)) {
            var w = BI.createWidget(item);
            this.addWidget(this.getName() + i, w);
        } else {
            var w = this.getWidgetByName(this.getName() + i);
        }
        if (o.columnSize && o.columnSize[i]) {
            var width = o.columnSize[i] / BI.sum(o.columnSize) * 100 + "%";
        } else {
            var width = 1 / this.options.items.length * 100 + "%"
        }
        w.element.css({"position": "relative", "float": "left", "width": width});
        return w;
    },

    addItem: function (item) {
        BI.LatticeLayout.superclass.addItem.apply(this, arguments);
        var w = this._addElement(this.options.items.length, item);
        this.options.items.push(item);
        w.element.appendTo(this.element);
        this.resize();
        return w;
    },

    resize: function () {
        this.stroke(this.options.items);
    },

    stroke: function (items) {
        var self = this;
        BI.each(items, function (i, item) {
            if (!!item) {
                self._addElement(i, item);
            }
        });
    },

    populate: function (items) {
        BI.LatticeLayout.superclass.populate.apply(this, arguments);
        this.stroke(items);
        this.render();
    }
});
$.shortcut('bi.lattice', BI.LatticeLayout);