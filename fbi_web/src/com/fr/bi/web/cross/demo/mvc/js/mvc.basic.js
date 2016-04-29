//View的结构
BasicView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(BasicView.superclass._defaultConfig.apply(this, arguments),{
            baseCls: "bi-basic"
        })
    },

    _init: function(){
        BasicView.superclass._init.apply(this, arguments);
    },

    _createViewIntroduction: function(){
        return BI.createWidget({
            type: "bi.vertical",
            cls: "center",
            items: BI.createItems([{
                text: "1、_render(vessel)函数：在_init之前执行，只执行一次         --静态页面的构造函数"
            }, {
                text: "2、refresh()函数，第一次进入该页面或该View对应的Model层数据发生改变时调用     --全局刷新函数"
            }, {
                text: "3、local()函数，当执行set函数而引起数据变化时调用, 返回true表示是临时数据   --局部刷新函数"
            }, {
                text: "4、load(data), 向后台请求数据的回调函数，若请求过了，则不会重新请求，data表示接受的数据   --后台数据回调函数"
            }, {
                text: "5、change(changed)函数, 只要Model层的非临时数据发生改变即会调用，changed参数表示改变的数据  --change函数"
            }, {
                text: "6、listenEnd(key1, key2, key3...), 监听到子节点结束的事件时触发， key1..表示子节点树, 返回false表示不刷新当前面板   --监听子节点结束函数"
            }, {
                text: "7、splice(key1, key2, key3...), 监听到子节点删除事件的回调函数, key1..表示子节点树   --删除子节点函数"
            }, {
                text: "8、duplicate(copy, key1, key2, key3...), 监听到子节点复制事件的回调函数, copy表示新开辟的节点，key1..表示子节点树   --复制子节点函数"
            }], {
                type: "bi.label",
                height: 30
            })

        });
    },

    _createModelIntroduction: function(){
        return BI.createWidget({
            type: "bi.vertical",
            cls: "center",
            items: BI.createItems([{
                text: "1、_static()函数：创建静态数据         --静态数据构造函数"
            }, {
                text: "2、similar()函数，子节点数据的类似副本，在触发子节点数据copy事件时调用   --子节点数据拷贝构造函数"
            }, {
                text: "3、parse(data)函数， 后台数据转换成model的数据函数，默认返回data本身  --数据转换函数"
            }, {
                text: "4、refresh()函数，  同View   --全局刷新函数"
            }, {
                text: "5、local()函数， 同View  --局部刷新函数"
            }, {
                text: "6、load(data),  同View  --后台数据回调函数"
            }, {
                text: "7、change(changed)函数, 同View  --change函数"
            }, {
                text: "8、splice(key1, key2, key3...),  同View  --删除子节点函数"
            }, {
                text: "9、duplicate(copy, key1, key2, key3...),  同View  --复制子节点函数"
            }], {
                type: "bi.label",
                height: 30
            })

        });
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 1,
            rows: 2,
            items: [{
                column:0,
                row: 0,
                el : {
                    type: "bi.border",
                    items: {
                        north: {
                            el: {
                                type: "bi.label",
                                cls: "title",
                                text: "View层的核心函数--以下函数根据需求选择性实现",
                                height: 50
                            },
                            height: 50
                        },
                        center: {
                            el : this._createViewIntroduction()
                        }
                    }
                }
            }, {
                column:0,
                row: 1,
                el : {
                    type: "bi.border",
                    items: {
                        north: {
                            el: {
                                type: "bi.label",
                                cls: "title",
                                text: "Model层的核心函数--以下函数根据需求选择性实现",
                                height: 50
                            },
                            height: 50
                        },
                        center: {
                            el : this._createModelIntroduction()
                        }
                    }
                }
            }]
        })
        BI.createWidget()
    },

    splice: function(key1, key2, key3){
        if(key1 === "type"){
            //...
        }
    },

    duplicate: function(copy, key1, key2, key3){
        if(key1 === "type"){
            // do something about copy;
        }
    },

    change: function(changed){
        if(changed.type){
            //...
        }
    },

    listenEnd: function(key1, key2, key3){
        return this;
    },

    local: function(){
        return false;
    },

    refresh: function(){

    }
})

BasicModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BasicModel.superclass._defaultConfig.apply(this, arguments),{

        })
    },

    _static: function(){
        return {

        }
    },

    _init: function(){
        BasicModel.superclass._init.apply(this, arguments);
    },

    similar: function(value, key1, key2, key3){
        if(key1 === "type"){
            value.name = ".."
        }
        return value;
    },

    parse: function(data){
        return data;
    },

    splice: function(key1, key2, key3){
        if(key1 === "type"){
            //...
        }
    },

    duplicate: function(copy, key1, key2, key3){
        if(key1 === "type"){
            // do something about copy;
        }
    },

    change: function(changed){
        if(changed.type){
            //...
        }
    },

    load: function(data){

    },

    local: function(){
        return false;
    },

    refresh: function(){

    }

})