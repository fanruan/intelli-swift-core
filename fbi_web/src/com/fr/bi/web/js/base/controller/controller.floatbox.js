/**
 * guy
 * FloatBox弹出层控制器, z-index在100w层级
 * @class BI.FloatBoxController
 * @extends BI.Controller
 */
BI.FloatBoxController = BI.inherit(BI.Controller, {
    _defaultConfig: function () {
        return BI.extend(BI.FloatBoxController.superclass._defaultConfig.apply(this, arguments), {
            modal: true, // 模态窗口
            render: "body"
        });
    },

    _init: function () {
        BI.FloatBoxController.superclass._init.apply(this, arguments);
        this.modal = this.options.modal;
        this.floatManager = {};
        this.floatLayer = {};
        this.zindex = BI.zIndex_floatbox;
        this.zindexMap = {};
    },

    _check: function (name) {
        return BI.isNotNull(this.floatManager[name]);
    },

    create: function (name, section, options) {
        if (this._check(name)) {
            return this;
        }
        var floatbox = BI.createWidget({
            type: "bi.float_box"
        }, options);
        floatbox.populate(section);
        this.add(name, floatbox);
        return this;
    },

    add: function (name, floatbox) {
        var self = this;
        if (this._check(name)) {
            return this;
        }
        BI.createWidget({
            type: "bi.absolute",
            element: "body",
            items: [{
                el: (this.floatLayer[name] = BI.createWidget({
                    type: 'bi.center_adapt',
                    items: [floatbox]
                })),
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }]
        });
        this.floatManager[name] = floatbox;
        (function (key) {
            floatbox.on(BI.FloatBox.EVENT_FLOAT_BOX_CLOSED, function () {
                self.close(key);
            })
        })(name);
        return this;
    },

    open: function (name) {
        if (!this._check(name)) {
            return this;
        }
        this.$body || (this.$body = $(this.options.render));
        this.modal && this.$body.__hasZIndexMask__(this.zindexMap[name]) && this.$body.__releaseZIndexMask__(this.zindexMap[name]);
        this.zindexMap[name] = this.zindex;
        this.modal && this.$body.__buildZIndexMask__(this.zindex++);
        this.get(name).setZindex(this.zindex++);
        this.floatLayer[name].visible();
        this.get(name).show();
        return this;
    },

    close: function (name) {
        if (!this._check(name)) {
            return this;
        }
        this.floatLayer[name].invisible();
        this.modal && this.$body.__releaseZIndexMask__(this.zindexMap[name]);
        return this;
    },

    get: function (name) {
        return this.floatManager[name];
    },

    remove: function (name) {
        if (!this._check(name)) {
            return this;
        }
        this.floatLayer[name].destroy();
        this.modal && this.$body.__releaseZIndexMask__(this.zindexMap[name]);
        delete this.floatManager[name];
        delete this.floatLayer[name];
        delete this.zindexMap[name];
        return this;
    }
});