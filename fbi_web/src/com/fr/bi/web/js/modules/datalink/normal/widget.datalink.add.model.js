/**
 * Created by Young's on 2016/3/18.
 */
BI.AddDataLinkModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.AddDataLinkModel.superclass._init.apply(this, arguments);
        var info = this.options.info;
        this.id = info.id || BI.UUID();
        this.database = info.database || null;
        this.name = info.name || "";
        this.oldName = info.name || "";
        this.driver = info.driver || "";
        this.url = info.url || "";
        this.user = info.user || "";
        this.password = info.password || "";
        this.newCharsetName = info.newCharsetName || "";
        this.originalCharsetName = info.originalCharsetName || "";
        this.copy = info.copy;
        if(BI.isNotNull(info.copy)){
            this.oldName = "";
        }
    },

    getId: function(){
        return this.id;
    },

    getDatabase: function(){
        return this.database;
    },

    getDatabaseByDriver: function(){
        return BICst.DATABASE[this.getDatabaseKey()];
    },

    getDatabaseKey: function(){
        var self = this;
        var databaseKey = "OTHERS";
        var database = this.getDatabase();
        if(BI.isNotNull(database)){
            BI.some(BICst.DATABASE, function(key, value){
                if(value === database){
                    databaseKey = key;
                    return true;
                }
            })
        } else {
            //通过driver查找
            BI.some(BICst.DATA_LINK_MANAGE.DRIVERS, function(key, drivers){
                var hasFound = false;
                BI.some(drivers, function(i, driver){
                    if(self.driver === driver.value){
                        databaseKey = key;
                        return hasFound = true;
                    }
                });
                return hasFound;
            });
        }
        return databaseKey;
    },

    getOldName: function(){
        return this.oldName;
    },

    getName: function(){
        return this.name;
    },

    setName: function(name){
        this.name = name;
    },

    getDriver: function(){
        return this.driver;
    },

    setDriver: function(driver){
        this.driver = driver;
    },

    getURL: function(){
        return this.url;
    },

    setURL: function(url){
        this.url = url;
    },

    getUser: function(){
        return this.user;
    },

    setUser: function(user){
        this.user = user;
    },

    getPassword: function(){
        return this.password;
    },

    setPassword: function(password){
        this.password = password;
    },

    getNewCharsetName: function(){
        return this.newCharsetName;
    },

    setNewCharsetName: function(name){
        this.newCharsetName = name;
    },

    getOriginalCharsetName: function(){
        return this.originalCharsetName;
    },

    setOriginalCharsetName: function(name){
        this.originalCharsetName = name;
    },
    
    isCopy: function(){
        return this.copy;  
    },

    checkDataLinkName: function(name){
        var self = this;
        var links = Data.SharingPool.get("links");
        var isValid = true;
        BI.some(links, function(id, link){
            if(link.name === name && id !== self.getId()){
                isValid = false;
                return true;
            }
        });
        return isValid;
    },

    getValue: function(){
        return {
            name: this.getName(),
            driver: this.getDriver(),
            url: this.getURL(),
            user: this.getUser(),
            password: this.getPassword(),
            newCharsetName: this.getNewCharsetName(),
            originalCharsetName: this.getOriginalCharsetName()
        }
    }
});