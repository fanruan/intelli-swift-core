/**
 * Created by roy on 15/9/1.
 */
FormulaInsertView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(FormulaInsertView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-formula-insert-view bi-mvc-layout",
            fieldItems: [
                {
                    text: "text1",
                    value: "text1",
                    title: "text1",
                    fieldType: BICst.COLUMN.STRING
                }, {
                    text: "num111.111+1111111111111",
                    value: "num111.111+1111111111111",
                    title: "num111.111+1111111111111",
                    fieldType: BICst.COLUMN.NUMBER
                }, {
                    text: "date1",
                    value: "date1",
                    title: "date1",
                    fieldType: BICst.COLUMN.DATE
                }, {
                    text: "text2",
                    value: "text2",
                    title: "text2",
                    fieldType: BICst.COLUMN.STRING
                }, {
                    text: "num2",
                    value: "num2",
                    title: "num2",
                    fieldType: BICst.COLUMN.NUMBER
                }, {
                    text: "num3",
                    value: "num3",
                    title: "num3",
                    fieldType: BICst.COLUMN.NUMBER
                }
            ],
            functionItems: [
                {
                    text: "ABS",
                    value: "ABS",
                    fieldType: BICst.COLUMN.NUMBER,
                    description: "this is description",
                    title: "this is description"
                }, {
                    text: "date1",
                    value: "date1",
                    fieldType: BICst.COLUMN.DATE,
                    description: "this is another description ",
                    title: "this is another description"
                }, {
                    text: "ABC",
                    value: "ABC",
                    fieldType: BICst.COLUMN.STRING,
                    description: "this is the last description",
                    title: "this is the last description"
                }
            ]
        })
    },
    _init: function () {
        FormulaInsertView.superclass._init.apply(this, arguments)
    },

    _render: function (vessel) {
        var self = this, o = this.options;
        this.popupButton = BI.createWidget({
            type: "bi.left",
            items: [{
                el: {
                    type: "bi.button",
                    cls: "float-box-button",
                    text: "click",
                    width: 30,
                    handler: function () {
                        FloatBoxes.open("ddd", "test", {}, self);
                    }
                }
            }]
        });

        this.formula = BI.createWidget({
            type: "bi.formula_insert",
            height: 200,
            width: 420,
            fieldItems: o.fieldItems,
            functionItems: o.functionItems


        });
        this.button = BI.createWidget({
            type: "bi.button",
            text: "getValue"
        });
        this.button.on(BI.TextButton.EVENT_CHANGE, function () {
            BI.Msg.alert("", self.formula.getValue());
        });

        this.setValueButton = BI.createWidget({
            type: "bi.button",
            text: "setValue"
        });

        this.setValueButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.formula.setValue(" $\{asdfadf\} + ABS($\{asd\},ABC($\{asdasdf\}))+ $\{asdfasdf\}+\"asdfasdf\"");
        });

        this.searchFunctionTree = BI.createWidget({
            type: "bi.function_tree",
            cls: "style-top",
            redmark: function (val, ob) {
                return true
            }
        });

        this.functiontree = BI.createWidget({
            type: "bi.function_tree",
            cls: "style-top",
            width:50,
            items: o.functionItems
        });

        var searcher = BI.createWidget({
            type: "bi.searcher",
            adapter: this.functiontree,
            popup: {
                type: "bi.function_searcher_pane",
                searcher: self.searchFunctionTree
            },
            height: 30,
            width: 110
        });

        this.div1 = BI.createWidget({
            type: "bi.absolute",
            items: [{
                //el: {
                //    type: "bi.absolute",
                //    items: [{
                //        el:searcher,
                //        top:20,
                //        left:20,
                //    }]
                //},
                el:searcher,
                top:0,
                right:0,
                left:0,
                bottom:0
            }],
            height:50
        })

        BI.createWidget({
            element: vessel,
            type: "bi.vertical",
            hgap: 10,
            items: [
                {
                    el: self.formula
                }, {
                    el: self.button
                }, {
                    el: self.popupButton
                }, {
                    el: self.setValueButton
                },{
                    el: searcher,
                },{
                    el:this.functiontree
                }
            ]
        })

    }

});

FormulaInsertModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(FormulaInsertModel.superclass._defaultConfig.apply(this, arguments), {})
    },
    _init: function () {
        FormulaInsertModel.superclass._init.apply(this, arguments);
    }
});


FormulaFloatBoxChildView = BI.inherit(BI.BarFloatSection, {
    _defaultConfig: function () {
        return BI.extend(FormulaFloatBoxChildView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-float-box-child",
            fieldItems: [
                {
                    text: "text1",
                    value: "text1",
                    title: "text1",
                    fieldType: BICst.COLUMN.STRING
                }, {
                    text: "num1111111111111111111",
                    value: "num1111111111111111111",
                    title: "num1111111111111111111",
                    fieldType: BICst.COLUMN.NUMBER
                }, {
                    text: "date1",
                    value: "date1",
                    title: "date1",
                    fieldType: BICst.COLUMN.DATE
                }, {
                    text: "text2",
                    value: "text2",
                    title: "text2",
                    fieldType: BICst.COLUMN.STRING
                }, {
                    text: "num2",
                    value: "num2",
                    title: "num2",
                    fieldType: BICst.COLUMN.NUMBER
                }, {
                    text: "num3",
                    value: "num3",
                    title: "num3",
                    fieldType: BICst.COLUMN.NUMBER
                }
            ],
            functionItems: [
                {
                    text: "ABS",
                    value: "ABS",
                    fieldType: BICst.COLUMN.NUMBER,
                    description: "this is description",
                    title: "this is description"
                }, {
                    text: "date1",
                    value: "date1",
                    fieldType: BICst.COLUMN.DATE,
                    description: "this is another description ",
                    title: "this is another description"
                }, {
                    text: "ABC",
                    value: "ABC",
                    fieldType: BICst.COLUMN.STRING,
                    description: "this is the last description",
                    title: "this is the last description"
                }
            ]
        })
    },

    _init: function () {
        FormulaFloatBoxChildView.superclass._init.apply(this, arguments);
    },

    rebuildNorth: function (north) {
        BI.createWidget({
            type: "bi.label",
            element: north,
            textAlign: "left",
            text: BI.UUID(),
            height: 50,
            hgap: 20
        });
        return true;
    },

    rebuildCenter: function (center) {
        var self = this, o = this.options;
        this.formula = BI.createWidget({
            element: center,
            type: "bi.formula_insert",
            height: 200,
            fieldItems: o.fieldItems,
            functionItems: o.functionItems


        });
        this.button = BI.createWidget({
            type: "bi.button",
            text: "getValue"
        });
        this.button.on(BI.TextButton.EVENT_CHANGE, function () {
            BI.Msg.alert("", self.formula.getValue());
        })
    },

    end: function () {
        this.model.destroy();
    },

    refresh: function () {
    }
});

FormulaFloatBoxChildModel = BI.inherit(BI.Model, {
    _defaultConfig: function () {
        return BI.extend(FormulaFloatBoxChildModel.superclass._defaultConfig.apply(this, arguments), {})
    },

    _init: function () {
        FormulaFloatBoxChildModel.superclass._init.apply(this, arguments);
    }
});