/**
 * 关联视图
 *
 * Created by GUY on 2015/12/23.
 * @class BI.RelationViewRegion
 * @extends BI.BasicButton
 */
BI.RelationViewRegion = BI.inherit(BI.BasicButton, {

    _defaultConfig: function () {
        return BI.extend(BI.RelationViewRegion.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-relation-view-region cursor-pointer",
            width: 150,
            text: "",
            value: "",
            header: "",
            items: []
        });
    },

    _init: function () {
        BI.RelationViewRegion.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.title = BI.createWidget({
            type: "bi.label",
            cls: "relation-view-region-title",
            height: 25,
            text: o.text,
            value: o.value,
            title: o.header
        });
        this.button_group = BI.createWidget({
            type: "bi.button_group",
            items: this._createItems(o.items),
            layouts: [{
                type: "bi.vertical"
            }]
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.vertical",
                cls: "relation-view-region-container",
                items: [this.title, this.button_group]
            }],
            hgap: 25,
            vgap: 20
        })
    },

    _createItems: function (items) {
        var self = this;
        return BI.map(items, function (i, item) {
            return BI.extend(item, {
                type: "bi.relation_view_item",
                hoverIn: function () {
                    self.setValue(item.value);
                    self.fireEvent(BI.RelationViewRegion.EVENT_HOVER_IN, item.value);
                },
                hoverOut: function () {
                    self.setValue([]);
                    self.fireEvent(BI.RelationViewRegion.EVENT_HOVER_OUT, item.value);
                }
            })
        });
    },

    doRedMark: function(){
        this.title.doRedMark.apply(this.title, arguments);
    },

    unRedMark: function(){
        this.title.unRedMark.apply(this.title, arguments);
    },

    getWidth: function () {
        return this.options.width;
    },

    getHeight: function () {
        return this.button_group.getAllButtons().length * 25 + 25 + 2 * 20 + 3;
    },

    //获取上方开始划线的位置
    getTopLeftPosition: function () {
        return {
            x: 25 + 10,
            y: 20
        }
    },

    getTopRightPosition: function () {
        return {
            x: this.getWidth() - 25 - 10,
            y: 20
        }
    },

    getBottomPosition: function () {
        return {
            x: 25 + 10,
            y: this.getHeight() - 20
        }
    },

    getLeftPosition: function () {
        return {
            x: 25,
            y: 20 + 10
        }
    },

    getRightPosition: function () {
        return {
            x: this.getWidth() - 25,
            y: 20 + 10
        }
    },

    setValue: function (v) {
        this.button_group.setValue(v);
    }
});
BI.RelationViewRegion.EVENT_HOVER_IN = "RelationViewRegion.EVENT_HOVER_IN";
BI.RelationViewRegion.EVENT_HOVER_OUT = "RelationViewRegion.EVENT_HOVER_OUT";
BI.RelationViewRegion.EVENT_CHANGE = "RelationViewRegion.EVENT_CHANGE";
$.shortcut('bi.relation_view_region', BI.RelationViewRegion);