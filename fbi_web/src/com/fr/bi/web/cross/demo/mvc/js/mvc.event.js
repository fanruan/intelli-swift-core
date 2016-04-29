//set、get函数
EventView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(EventView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-event"
        })
    },

    events: {
        "sortable dragContainer": "_sortLabel",
        "click clickWidgets": "_clickLabel"
    },

    _sortLabel: function(item){
        var self = this;
        return {
            containment: this.$vessel,
            tolerance:"pointer",
            start: function(event, ui) {

            },
            update: function(event, ui) {
                var sort = self.dragContainer.element.sortable("toArray", {attribute: "val"});
                self.dragLabel.setText(sort.toString());
            },
            over: function(event, ui) {

            }
        }
    },

    _clickLabel: function(item){
        var self = this;
        return function(e){
            self.clickLabel.setText(item.getText());
        }
    },

    _init: function(){
        EventView.superclass._init.apply(this, arguments);
    },

    _createDragCenter: function(){
        return (this.dragContainer = BI.createWidget({
            type: "bi.vertical",
            cls: "drag-container vertical",
            items: BI.createItems(this.model.get("arr"), {
                type: "bi.label",
                tagName: "li",
                cls: "nav",
                height: 30
            }),
            hgap: 10,
            vgap: 5
        }))
    },

    _createDragSouth: function(){
        return (this.dragLabel = BI.createWidget({
            type: "bi.label",
            cls: "front",
            text:"",
            whiteSpace: "normal"
        }));
    },

    _createClickCenter: function(){
        this.clickWidgets = BI.createWidgets(this.model.get("arr"), {
            type: "bi.label",
            tagName: "li",
            cls: "nav",
            height: 30
        })
        return BI.createWidget({
            type: "bi.vertical",
            tagName: "ol",
            items: this.clickWidgets,
            hgap: 10,
            vgap: 5
        })
    },

    _createClickSouth: function(){
        return (this.clickLabel = BI.createWidget({
            type: "bi.label",
            cls: "front",
            text:"",
            whiteSpace: "normal"
        }));
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.center",
            element: vessel,
            items: [{
                el: {
                    type: "bi.border",
                    items:{
                        north: {
                            el: {
                                type: "bi.label",
                                cls: "title",
                                text:"拖拽事件绑定",
                                height: 30
                            },
                            height:30
                        },
                        center: {
                            el: this._createDragCenter()
                        },
                        south: {
                            el: this._createDragSouth(),
                            height:30
                        }
                    }
                }
            }, {
                el: {
                    type: "bi.border",
                    items:{
                        north: {
                            el: {
                                type: "bi.label",
                                cls: "title",
                                text:"对一系列控件绑定click",
                                height: 30
                            },
                            height:30
                        },
                        center: {
                            el: this._createClickCenter()
                        },
                        south: {
                            el: this._createClickSouth(),
                            height:30
                        }
                    }
                }
            }],
            hgap: 50,
            vgap: 100
        })
    }
})

EventModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(EventModel.superclass._defaultConfig.apply(this, arguments),{
            arr: [
                {text: "item1", attributes:{val: "item1"}},
                {text: "item2", attributes:{val: "item2"}},
                {text: "item3", attributes:{val: "item3"}},
                {text: "item4", attributes:{val: "item4"}},
                {text: "item5", attributes:{val: "item5"}},
                {text: "item6", attributes:{val: "item6"}},
                {text: "item7", attributes:{val: "item7"}},
                {text: "item8", attributes:{val: "item8"}}
            ]
        })
    },

    _init: function(){
        EventModel.superclass._init.apply(this, arguments);
    }
})