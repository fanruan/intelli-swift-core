/**
 * Created by fay on 2016/12/6.
 */
BI.AccumulationGroup = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.AccumulationGroup.superclass._defaultConfig.apply(this, arguments), {
            cls: "bi-accumulation-group"
        })
    },

    _init: function () {
        BI.AccumulationGroup.superclass._init.apply(this, arguments);
        var self = this;
        this.defaultGroup = BI.createWidget({
            type: "bi.accumulate_container",
            width: "42%",
            height: "100%",
            scrolly: true
        });
        this.defaultGroup.element.sortable({
            connectWith: ".items-container",
            tolerance: "pointer",
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            items: ".accumulate-item",
            scroll: false,
            helper: function (event, ui) {
                var drag = BI.createWidget();
                drag.element.append(ui.html());
                BI.createWidget({
                    type: "bi.absolute",
                    element: self.element,
                    items: [{
                        el: drag,
                        top: 0,
                        left:0,
                        bottom: 0
                    }]
                })
                return drag.element;
            },
            update: function (event, ui) {
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        });
        this.group = BI.createWidget({
            type: "bi.button_group",
            width: "50%",
            height: "100%",
            layouts: [{
                type: "bi.vertical"
            }]
        });
        BI.createWidget({
            type: "bi.inline_vertical_adapt",
            element: this.element,
            items: [{
                el: this.defaultGroup,
                lgap: 10,
                rgap: 20
            }, {
                el: this.group,
                lgap: 10
            }]
        });
    },
    
    createItem: function (index) {
        var next = index || this.getNextIndex();
        var item = BI.createWidget({
            type: "bi.accumulate_container",
            index: next,
            title: BI.i18nText("BI-Accumulation_Group") + next
        })
        this.group.addItems([item]);
        return item;
    },
    
    populate: function (items) {
        var self = this;
        if(BI.isEmpty(items)) {
            return;
        }
        self.defaultGroup.populate(items[0] && items[0].items);
        BI.each(items.slice(1), function (idx, data) {
            var container = self.createItem(data.index);
            container.populate(data.items);
        })
    },

    getAllContainers: function () {
        return BI.concat([this.defaultGroup], this.group.getAllButtons());
    },

    getNextIndex: function () {
        var i = 0;
        var indexArray = [];
        BI.each(this.getAllContainers(), function (idx, container) {
            container.getIndex && indexArray.push(container.getIndex());
        })
        while (++i)  {
            if(!BI.contains(indexArray, i)) {
                return i;
            }
        }
    },

    getValue: function () {
        var self = this, o = this.options || {}, result = [];

        BI.each(this.getAllContainers(), function (idx, group) {
            if(BI.isNotEmptyArray(group.getValue())) {
                result.push({
                    title: group.getTitle(),
                    index: group.getIndex(),
                    items: group.getValue()
                });
            }
        });

        return result;
    }
});

$.shortcut("bi.accumulation_group", BI.AccumulationGroup);