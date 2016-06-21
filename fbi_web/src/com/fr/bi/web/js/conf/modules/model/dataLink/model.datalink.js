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
        Data.SharingPool.put("links", this.get("links"));
    },

    createDistinctLinkName: function(id){
        var name = this.get("links")[id].name;
        var names = [], links = this.get("links");
        BI.each(links, function(i, link){
            names.push({name: link.name});
        });
        return BI.Func.createDistinctName(names, name);
    },

    createDistinctLinkId: function(){
        var linkIds = BI.keys(this.get("links"));
        var names = [{name: "link"}];
        BI.each(linkIds, function(i, id){
            names.push({name: id});
        });
        return BI.Func.createDistinctName(names, "link");
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
                name: this.get("links")[id].name
            }
        });
        var links = this.get("links");
        delete links[id];
        this.set("links", links);
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
        if(this.has("copy")) {
            this.get("copy");
            return true;
        }
        if(this.has("addLink")) {
            var newLink = this.get("addLink");
            var links = this.get("links");
            links[this.createDistinctLinkId()] = newLink;
            this.set("links", links);
            return true;
        }
        return false;
    },

    load: function(){
        Data.SharingPool.put("links", this.get("links"));
    },

    refresh: function(){

    }
});