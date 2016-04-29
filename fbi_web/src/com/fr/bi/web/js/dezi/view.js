BI.View.prototype.createView = function (url, modelData, viewData) {
    return BI.Factory.createView(url, BIDezi.Views.get(url), _.extend({}, BIDezi.Models.get(url), modelData), viewData || {}, this);
};
BI.View.prototype._opt = function (options) {
    options || (options = {});
    options.data || (options.data = {});
    options.data.sessionID = Data.SharingPool.get("sessionID");
    options.error = function () {
        BI.Msg.toast("Ajax Error!", "warning")
    };
    BI.extend(options.data, this.model.toJSON());
    return options;
};
BI.View.prototype.read = BI.throttle(function (options) {
    this.model.fetch(this._opt(options))
}, 30, {leading: false});

BI.View.prototype.update = BI.throttle(function (options) {
    this.model.save(null, this._opt(options));
}, 30, {leading: false});

BI.View.prototype.patch = BI.throttle(function (options) {
    this.model.save(null, BI.extend(this._opt(options), {
        patch: true
    }));
}, 30, {leading: false});