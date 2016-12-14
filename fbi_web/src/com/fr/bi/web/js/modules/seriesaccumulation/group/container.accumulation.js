/**
 * Created by fay on 2016/12/6.
 */
BI.AccumulationContainer = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AccumulationContainer.superclass._defaultConfig.apply(this, arguments), {
            title: BI.i18nText("BI-Accumulation_Group"),
            cls: "bi-accumulation-container",
            scrolly: false,
            index: 0
        })
    },

    _init: function () {
        BI.AccumulationContainer.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        var header = this._createHeader();
        this.group = BI.createWidget({
            type: "bi.vertical",
            cls: "items-container",
            lgap: 20,
            rgap: 30,
            vgap: 10
        });
        this.group.element.sortable({
            connectWith: ".items-container",
            tolerance: "pointer",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height()
                    });
                    holder.element.css({"margin": "10px 30px 10px 20px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            helper: function (event, ui) {
                var drag = BI.createWidget();
                drag.element.append(ui.html());
                BI.createWidget({
                    type: "bi.default",
                    element: o.helperContainer ? o.helperContainer : self.element,
                    items: [drag]
                })
                return drag.element;
            } ,
            items: ".accumulate-item",
            update: function (event, ui) {
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
        BI.createWidget({
            type: o.scrolly ? "bi.vtape" : "bi.default",
            items: [{
                el: header,
                height: 40
            }, this.group],
            element: this.element
        });
    },

    _createHeader: function () {
        var self = this, o = this.options;
        this.title = BI.createWidget({
            type: "bi.label",
            text: o.title,
            title: o.title
        });
        var button = BI.createWidget({
            type: "bi.button",
            text: "test"
        })
        var selectType = BI.createWidget({
            type: "bi.combo",
            el: button,
            popup: {
                el: this.popup
            }
        });
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            items: {
                left: [{
                    el: this.title,
                    lgap: 10
                }],
                right: [{
                    el: selectType,
                    rgap: 10
                }]
            },
            height: 40
        });
    },

    populate: function (items) {
        var self = this;
        BI.each(items, function (idx, item) {
            self.group.addItem(BI.createWidget({
                type: "bi.label",
                cls: "accumulate-item",
                text: item,
                height: 30,
                vtap: 10,
                htap: 5
            }));
        })
    },

    getIndex: function () {
        return this.options.index;
    },

    getTitle: function () {
        return this.title.getText();
    },

    getValue: function () {
        var self = this, o = this.options || {};
        var result = [];
        var items = $(".accumulate-item", this.group.element);
        BI.each(items, function (i, dom) {
            result.push($(dom).text());
        });

        return result;

    }
});

BI.AccumulationContainer.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.accumulate_container", BI.AccumulationContainer);