BI.Model.prototype.urlRoot = function () {
    return FR.servletURL + "?op=fr_bi_dezi";
};
BI.Model.prototype.cmd = function (cmd) {
    return this.urlRoot() + "&cmd=" + cmd + "&_=" + Math.random();
};

BI.Model.prototype._opt = function (options) {
    options || (options = {});
    options.data || (options.data = {});
    options.data.sessionID = Data.SharingPool.get("sessionID");
    options.error = function () {
        BI.Msg.toast("Ajax Error!", "warning")
    };
    return options;
};
BI.Model.prototype.read = function (options) {
    if (this._start == true || this._changing_ === true) {
        this._F.push({f: this.read, arg: arguments});
        return;
    }
    this._read(this._opt(options));
};

BI.Model.prototype.update = function (options) {
    if (this._start == true || this._changing_ === true) {
        this._F.push({f: this.update, arg: arguments});
        return;
    }
    options || (options = {});
    options.data || (options.data = {});
    BI.extend(options.data, this.toJSON());
    this._save(null, this._opt(options));
};

BI.Model.prototype.patch = function (options) {
    if (this._start == true || this._changing_ === true) {
        this._F.push({f: this.patch, arg: arguments});
        return;
    }
    this._save(null, BI.extend({}, this._opt(options), {
        patch: true
    }));
};