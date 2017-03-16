BI.extend(BI.Utils, {
    isRealTime: function () {
        return Data.SharingPool.get("description") === "true";
    }
});