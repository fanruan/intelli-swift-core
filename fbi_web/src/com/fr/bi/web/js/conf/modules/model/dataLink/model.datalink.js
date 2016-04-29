/**
 * @class BIConf.DataLinkPaneModel 展示数据连接的界面
 * @extend BI.Model
 * @type {*|void|Object}
 */
BIConf.DataLinkPaneModel = BI.inherit(BI.Model, {
    _defaultConfig: function() {
        return _.extend( BIConf.DataLinkPaneModel.superclass._defaultConfig.apply(this, arguments), {

        })
    },

    change: function(){
        Data.SharingPool.put("links", this.toJSON());
    },

    createDistinctLinkName: function(id){
        var name = this.get(id).name;
        var names = [], links = this.toJSON();
        BI.each(links, function(i, link){
            names.push({name: link.name});
        });
        return BI.Func.createDistinctName(names, name);
    },

    patchURL: function(){
        return this.cmd('modify_data_link');
    },


    readURL: function(){
        return this.cmd('get_data_link');
    },

    updateURL: function(){
        return this.cmd("test_data_link");
    },

    _deleteLink: function(id){
        this.patch({
            unset: true,
            data:{
                actionType: "delete",
                name: this.get(id).name
            }});
    },

    local: function(){
        if(this.has("delete")){
            var id = this.get("delete");
            this._deleteLink(id);
            return true;
        }
        if(this.has("test")){
            this.get("test");
            return true;
        }
        if(this.has("set")){
            this.get("set");
            return true;
        }
        return false;
    },

    load: function(){
        Data.SharingPool.put("links", this.toJSON());
    },

    refresh: function(){

    }
});