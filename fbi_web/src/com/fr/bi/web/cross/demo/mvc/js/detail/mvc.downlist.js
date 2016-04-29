/**
 * Created by roy on 15/8/31.
 */
DownListView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(DownListView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "mvc-down-list-combo bi-mvc-layout"
        })
    },
    _init: function () {
        DownListView.superclass._init.apply(this, arguments);
    },

    _createDownList: function () {
        this.down = BI.createWidget({
            type: "bi.down_list_combo",
            height: 30,
            items: [
                [{
                    el: {
                        text: "column 1111",
                        iconCls1: "check-mark-e-font",
                        value: 11
                    },
                    children: [
                        {
                            text: "column 1.1",
                            value: 21,
                            cls: "dot-e-font"
                        }, {
                            text: "column 1.222222222222222222222222222222222222",
                            value: 22,
                            cls: "dot-e-font"
                        }, {
                            text: "column 1.3",
                            value: 23,
                            cls: "dot-e-font"
                        }, {
                            text: "column 1.4",
                            value: 24,
                            cls: "dot-e-font"
                        }, {
                            text: "column 1.5",
                            value: 25
                        }
                    ]
                }],
                [
                    {
                        el: {
                            type: "bi.icon_text_icon_item",
                            text: "column 2",
                            iconCls1: "chart-type-e-font",
                            value: 12
                        },
                        disabled: true,
                        children: [{
                            type: "bi.icon_text_item",
                            height: 25,
                            text: "column 2.1",
                            value: 11
                        }, {text: "column 2.2", value: 12}],


                    }
                ],
                [
                    {
                        text: "column 33333333333333333333333333333333",
                        cls: "style-set-e-font",
                        value: 13
                    }
                ],
                [
                    {
                        text: "column 4",
                        cls: "filter-e-font",
                        value: 14
                    }
                ]
                ,
                [
                    {
                        text: "column 5",
                        cls: "copy-e-font",
                        value: 15

                    }
                ]
                ,
                [
                    {
                        text: "column 6",
                        cls: "delete-e-font",
                        value: 16
                    }
                ]
                ,
                [
                    {
                        text: "column 7",
                        cls: "dimension-from-e-font",
                        value: 17,
                        disabled: true
                    }
                ]
                ,
                [
                    {
                        text: "column 8",
                        cls: "dot-e-font",
                        value: 18
                    },
                    {

                        text: "column 9",
                        cls: "dot-e-font",
                        value: 19

                    }
                ]

            ]
        });
        this.down.on(BI.DownListCombo.EVENT_SON_VALUE_CHANGE, function (value, fatherValue) {
            BI.Msg.alert("", value);
        });

        this.down.on(BI.DownListCombo.EVENT_CHANGE, function (value) {
            BI.Msg.alert("", value);
        });

        return this.down;
    },

    _render: function (vessel) {
        var self = this;
        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            hgap: 30,
            vgap: 20,
            items: [{
                el: this._createDownList()
            }, {
                type: "bi.button",
                text: "getValue",
                handler: function () {
                    BI.Msg.alert("", JSON.stringify(self.down.getValue()));
                }
            }, {
                type: "bi.button",
                text: "setValue",
                handler: function () {
                    self.down.setValue([{value: 11, childValue: 24}, {value: 18}])
                }
            },
                {
                    type: "bi.button",
                    text: "populate",
                    handler: function () {
                        self.down.populate([
                            [{
                                el: {
                                    text: "column 1111",
                                    iconCls1: "check-mark-e-font",
                                    value: 1
                                },
                                children: [
                                    {
                                        text: "column 1.1",
                                        value: 21,
                                        cls: "dot-e-font"
                                    }
                                ]
                            }],
                            [
                                {
                                    el: {
                                        type: "bi.icon_text_icon_item",
                                        text: "column 2",
                                        iconCls1: "chart-type-e-font"
                                    },
                                    disabled: true,
                                    children: [{
                                        type: "bi.icon_text_item",
                                        height: 25,
                                        text: "column 2.1",
                                        value: 11
                                    }, {type: "bi.icon_text_icon_item", height: 25, text: "column 2.2", value: 12}]

                                }
                            ],
                            [
                                {
                                    text: "column 51312341234",
                                    cls: "style-set-e-font",
                                    value: 13
                                }
                            ],
                            [
                                {
                                    text: "column 12341234",
                                    cls: "filter-e-font",
                                    value: 14
                                }
                            ]
                            ,
                            [
                                {
                                    text: "column 123412341234",
                                    cls: "copy-e-font",
                                    value: 15

                                }
                            ]
                            ,
                            [
                                {
                                    text: "column 1234123512345",
                                    cls: "delete-e-font",
                                    value: 16
                                }
                            ]
                        ]);
                    }
                }]
        })
    }

})

DownListModel = BI.inherit(BI.Model, {});