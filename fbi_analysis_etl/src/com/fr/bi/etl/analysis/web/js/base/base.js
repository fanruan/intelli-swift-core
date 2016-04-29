/**
 * 自带model与controller的Widget
 *
 */
BI.MVCWidget = FR.extend(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.MVCWidget.superclass._defaultConfig.apply(this, arguments), {
        })
    },

    _init : function () {
        BI.MVCWidget.superclass._init.apply(this, arguments);
        this.simpleWidgets = {};
        this.childWidgets = {};
        //initView的时候model和controller都是空对象。view就是纯粹的view无法访问model,只能在事件里面访问controller的代理
        this._initView();
        var modelClass, model;
        if(BI.isFunction(this._initModel)){
            modelClass = this._initModel();
        }

        var self = this;
        //本来可以直接加到该类型的原型，但是那样容易写出重复的方法，所以还是分model与controller吧
        //只要widget不重新new proxy对象不会改变
        var modelProxy = {};
        //禁止直接访问model成员和私有函数
        var c = modelClass;
        while(BI.isNotNull(c) && BI.isNotNull(c.prototype)){
            BI.each(c.prototype, function(idx, item){
                if(BI.isFunction(item) && !idx.startWith("_")) {
                    modelProxy[idx] = modelProxy[idx] || function () {
                            return model[idx].apply(model, arguments)
                        }
                }
            })
            c = c.prototype.superclass;
        }
        var controllerClass, controller;
        if(BI.isFunction(this._initController)) {
            controllerClass = this._initController();
            // controller = new controllerClass(this.options.controller || {});
        }
        this.controller = {};
        //controller需要调用widget的属性和model的方法，禁止访问model成员和私有函数
        c = controllerClass;
        while(BI.isNotNull(c) && BI.isNotNull(c.prototype)){
            BI.each(c.prototype, function(idx, item){
                if(BI.isFunction(item) && !idx.startWith("_")) {
                    self.controller[idx] = self.controller[idx] || function () {
                            var args = Array.prototype.slice.call(arguments)
                            //在最后放入widget作为参数
                            args.push(self);
                            args.push(modelProxy);
                            if(BI.isNull(controller)){
                                throw new Error("child widget must do populate in parent's populate function after appended to dom body")
                            }
                            return controller[idx].apply(controller, args)
                        }
                }
            })
            c = c.prototype.superclass;
        }

        this.controller._createPopulateArgs = function (key, c) {
            return modelProxy.createPopulateArgs(key, c)
        }

        this.controller._populate = function (m, c, pm, key) {
            if(BI.isNotNull(modelClass)){
                model = new modelClass(m || {});
            }
            if(BI.isNotNull(controllerClass)) {
                controller = new controllerClass(c || {});
                if(BI.isFunction(controller._construct)) {
                    controller._construct(self, modelProxy)
                }
            }
            if(BI.isNotNull(pm) && BI.isFunction(pm.set) && BI.isNotNull(key)) {
                //使用modelProxy 这样在内部发生刷新populate的时候不会影响model的正确获取
                pm.set(key, modelProxy);
            }
        }
        this.populate(this.options.model, this.options.controller)
    },

    _initModel : function() {
        return BI.MVCModel;
    },

    registerSimpleWidget : function (widget) {
        this.simpleWidgets[widget.getName()] = widget;
    },

    registerChildWidget : function (key, widget, c) {
        this.childWidgets[key] = widget;
        if(BI.isNotNull(this.controller) && BI.isFunction(widget.populate)) {
            widget.populate.apply(widget, this.controller._createPopulateArgs(key, c))
        }
    },

    /**
     * 暂时没想到更好的办法来组合model先每次populate的时候来做吧
     * @param model
     * @param controller
     * @param parentModel
     * @param key
     */
    populate : function (model, controller, parentModel, key) {
        if (this.element.parents("body").length > 0){
            var self = this;
            this.controller._populate(model, controller,  parentModel, key);
            BI.each(this.simpleWidgets, function (idx, item) {
                if(BI.isFunction(item.populate)) {
                    item.populate.apply(item)
                }
            })
            BI.each(this.childWidgets, function (idx, item) {
                if(BI.isFunction(item.populate)) {
                    item.populate.apply(item, self.controller._createPopulateArgs(idx))
                }
            })
            //controller需要实现populate方法
            if(BI.isFunction(this.controller.populate)) {
                var self = this;
                // BI.defer(function () {
                self.controller.populate.apply(self.controller);
                // })
            }
            if(BI.isFunction(this.controller.deferChange)) {
                var self = this;
                BI.defer(function () {
                    self.controller.deferChange.apply(self.controller);
                })
            }
        }
    },

    refreshView : function () {
        BI.each(this.simpleWidgets, function (idx, item) {
            if(BI.isFunction(item.refreshView)) {
                item.refreshView.apply(item)
            }
        })
        BI.each(this.childWidgets, function (idx, item) {
            if(BI.isFunction(item.refreshView)) {
                item.refreshView.apply(item)
            }
        }) //controller需要实现populate方法
        var self = this;
        if(BI.isFunction(this.controller.populate)) {
            this.controller.populate.apply(self.controller);
        }
        if(BI.isFunction(this.controller.deferChange)) {
            var self = this;
            BI.defer(function () {
                self.controller.deferChange.apply(self.controller);
            })
        }
    },
    
    update : function () {
        if(BI.isFunction(this.controller.update)) {
            return this.controller.update.apply(this.controller);
        }
    }

})

BI.MVCController = BI.inherit(BI.Controller,  {
    _init : function () {
        BI.MVCController.superclass._init.apply(this, arguments);
    },

    _construct : function (widget, model) {
    },

    update : function () {
        var widget = arguments[arguments.length - 2];
        var model = arguments[arguments.length - 1];
        if(BI.isFunction(model.update)) {
            return model.update.apply(model);
        }

    },

    deferChange : function () {
    },

    refreshModel : function (m, widget, model) {
        widget.populate(m, this.options)
    }
})

BI.MVCModel = BI.inherit(BI.M,  {


    _init : function () {
        BI.MVCModel.superclass._init.apply(this, arguments)
        var value = this.get("value") || BI.UUID();
        this.set("value", value)
    },

    getChildValue : function (key) {
        var childModel = this.get(key);
        if(BI.isNotNull(childModel) && BI.isFunction(childModel.update)) {
            return childModel.update()
        }
    },

    createPopulateArgs : function (key, c) {
        return [this.getChildValue(key), c, this, key]
    },
    
    toJSON : function () {
        var attr = BI.MVCModel.superclass.toJSON.apply(this, arguments)
        return this._toJSON(attr);
    },

    update : function () {
        return BI.deepClone(this.toJSON());
    },

    /**
     * 返回是否修改
     * @param key
     * @param v
     * @returns {boolean}
     */
    setValue : function (key, v) {
        var isChange = !BI.isEqual(v, this.get(key));
        this.set(key, v)
        return isChange;
    },


    getValue : function (key) {
        return BI.deepClone(this.get(key))
    },

    _toJSON : function (attr) {
        var ob = {};
        var self  = this;
        BI.each(attr, function (idx, item) {
            ob[idx] = BI.isNotNull(item) && BI.isFunction(item.toJSON) ? self._toJSON(item.toJSON()) :item;
        })
        return ob;
    }
})

BI.TestModel = BI.inherit(BI.M, {
    _defaultConfig: function () {
        return BI.extend(BI.TestModel.superclass._defaultConfig.apply(this, arguments), {
        })
    },

    _init : function () {
        BI.TestModel.superclass._init.apply(this, arguments);
        this.value = 10
    },

    getVV : function () {
        return this.value;
    },

    setVV : function (v) {
        this.value = v
    }
})

BI.TestController = BI.inherit(BI.Controller, {
    _defaultConfig: function () {
        return BI.extend(BI.TestController.superclass._defaultConfig.apply(this, arguments), {
        })
    },

    _init : function () {
        BI.TestController.superclass._init.apply(this, arguments);

    },

    test : function(v, widget, model) {
        alert(v);
        alert(model.getVV());
        model.setVV(200);
        alert(model.getVV());
    }
})

BI.TestMVC = BI.inherit(BI.MVCWidget, {
    _defaultConfig: function () {
        return BI.extend(BI.TestMVC.superclass._defaultConfig.apply(this, arguments), {
        })
    },

    _initModel : function() {
        return BI.TestModel;
    },

    _initController : function() {
        return BI.TestController;
    },

    _initView : function () {
    }
})
ETLCst = {};
