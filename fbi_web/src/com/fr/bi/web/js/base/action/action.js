/**
 * guy
 * 由一个元素切换到另一个元素的行为
 * @class BI.Action
 * @extends FR.OB
 * @abstract
 */
BI.Action = BI.inherit(FR.OB, {
    _defaultConfig: function() {
        return BI.extend(BI.Action.superclass._defaultConfig.apply(this, arguments), {
            src: null,
            tar: null
        });
    },

    _init : function() {
        BI.Action.superclass._init.apply(this, arguments);
    },

    actionPerformed: function(src, tar, callback){

    },

    actionBack: function(tar, src, callback){

    }
});

BI.ActionFactory = {
    createAction: function(key, options){
        var action;
        switch (key){
            case "show":
                action = BI.ShowAction;
                break;
        }
        return new action(options);
    }
}