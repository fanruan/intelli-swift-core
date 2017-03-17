/**
 * Created by roy on 15/9/14.
 */
FormulaPaneView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FormulaPaneView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-formula-function-pane-view"
        })
    },
    _init: function () {
        FormulaPaneView.superclass._init.apply(this, arguments);
    },
    _render: function (vessel) {
        BI.createWidget({
            element: vessel,
            type:"bi.left",
            hgap:20,
            items:[
                {
                    el:{
                        type: "bi.function_pane",
                        items: [
                            {
                                text: "ABS",
                                value: "ABS",
                                type: BICst.COLUMN.NUMBER,
                                description: "this is description",
                                title: "this is description"
                            }, {
                                text: "date1",
                                value: "date1",
                                type: BICst.COLUMN.DATE,
                                description: "this is another description ",
                                title: "this is another description"
                            }, {
                                text: "ABC",
                                value: "ABC",
                                type: BICst.COLUMN.STRING,
                                description: "this is the last description",
                                title: "this is the last description"
                            }
                        ]
                    }
                },
                {
                    el:{
                        type:"bi.search_editor"
                    }
                }
            ]




        })
    }
})

FormulaPaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(FormulaPaneModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        FormulaPaneModel.superclass._init.apply(this, arguments);
    }
})