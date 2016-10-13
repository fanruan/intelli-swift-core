/**
 * 有总页数和总行数的分页控件
 * Created by Young's on 2016/10/13.
 */
BI.AllCountPagger = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.AllCountPagger.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-all-pager",
            width: 190,
            height: 25,
            pages: 1, //必选项
            curr: 1, //初始化当前页， pages为数字时可用，
            count: 1 //总行数
        })
    },
    _init: function () {
        BI.AllCountPagger.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.editor = BI.createWidget({
            type: "bi.small_text_editor",
            cls: "pager-editor",
            validationChecker: function (v) {
                return BI.isPositiveInteger(v);
            },
            hgap: 4,
            vgap: 0,
            value: o.curr,
            errorText: BI.i18nText("BI-Please_Input_Integer"),
            width: 30,
            height: o.height - 2
        });
        this.pager = BI.createWidget({
            type: "bi.pager",
            width: 36,
            layouts: [{
                type: "bi.horizontal",
                hgap: 1,
                vgap: 1
            }],

            dynamicShow: false,
            pages: o.pages,
            curr: o.curr,
            groups: 0,

            first: false,
            last: false,
            prev: {
                type: "bi.icon_button",
                value: "prev",
                title: BI.i18nText("BI-Previous_Page"),
                warningTitle: BI.i18nText("BI-Current_Is_First_Page"),
                height: o.height - 2,
                cls: "all-pager-prev column-pre-page-h-font"
            },
            next: {
                type: "bi.icon_button",
                value: "next",
                title: BI.i18nText("BI-Next_Page"),
                warningTitle: BI.i18nText("BI-Current_Is_Last_Page"),
                height: o.height - 2,
                cls: "all-pager-next column-next-page-h-font"
            },

            hasPrev: o.hasPrev,
            hasNext: o.hasNext,
            firstPage: o.firstPage,
            lastPage: o.lastPage
        });

        this.editor.on(BI.TextEditor.EVENT_CONFIRM, function () {
            self.pager.setValue(BI.parseInt(self.editor.getValue()));
            self.fireEvent(BI.AllCountPagger.EVENT_CHANGE);
        });
        this.pager.on(BI.Pager.EVENT_CHANGE, function () {
            self.fireEvent(BI.AllCountPagger.EVENT_CHANGE);
        });
        this.pager.on(BI.Pager.EVENT_AFTER_POPULATE, function () {
            self.editor.setValue(self.pager.getCurrentPage());
        });

        this.allPages = BI.createWidget({
            type: "bi.label",
            width: 30,
            title: o.pages,
            text: "/" + o.pages
        });

        this.rowCount = BI.createWidget({
            type: "bi.label",
            text: o.count,
            title: o.count,
            width: 50
        });

        var count = BI.createWidget({
            type: "bi.center_adapt",
            columnSize: [15, 50, 19],
            items: [{
                type: "bi.label",
                height: o.height,
                text: BI.i18nText("BI-Total"),
                width: 15
            }, this.rowCount, {
                type: "bi.label",
                height: o.height,
                text: BI.i18nText("BI-Tiao"),
                width: 19,
                textAlign: "left"
            }]
        });
        BI.createWidget({
            type: "bi.center_adapt",
            element: this.element,
            columnSize: [84, 30, "", 36],
            items: [count, this.editor, this.allPages, this.pager]
        })
    },

    setAllPages: function (v) {
        this.allPages.setText("/" + v);
        this.allPages.setTitle(v);
        this.pager.setAllPages(v);
    },

    setValue: function (v) {
        this.pager.setValue(v);
    },

    setCount: function (count) {
        this.rowCount.setText(count);
        this.rowCount.setTitle(count);
    },

    getCurrentPage: function () {
        return this.pager.getCurrentPage();
    },

    hasPrev: function () {
        return this.pager.hasPrev();
    },

    hasNext: function () {
        return this.pager.hasNext();
    },

    populate: function () {
        this.pager.populate();
    }
});
BI.AllCountPagger.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.all_count_pager", BI.AllCountPagger);