package com.fr.bi.conf.base.auth;

import com.finebi.cube.conf.pack.data.BIPackageID;
import com.fr.bi.conf.base.auth.data.BIPackageAuthority;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by young
 */
public class BIAuthorityManager {

    private Map<BIPackageID, List<BIPackageAuthority>> packagesAuth = new HashMap<BIPackageID, List<BIPackageAuthority>>();

    public void savePackageAuth(BIPackageID packageID, List<BIPackageAuthority> auth, long userId) {
        this.packagesAuth.put(packageID, auth);
    }

    public Map<BIPackageID, List<BIPackageAuthority>> getPackagesAuth(long userId) {
        return packagesAuth;
    }

    public List<BIPackageAuthority> getPackageAuthByID(BIPackageID packageID, long userId) {
        return this.packagesAuth.get(packageID);
    }

    public JSONObject createJSON(long userId) throws Exception {
        JSONObject jo = new JSONObject();
        Iterator<Map.Entry<BIPackageID, List<BIPackageAuthority>>> iterator = this.packagesAuth.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BIPackageID, List<BIPackageAuthority>> packAuth = iterator.next();
            List<BIPackageAuthority> authorities = packAuth.getValue();
            JSONArray ja = new JSONArray();
            for(int i = 0; i < authorities.size(); i++) {
                ja.put(authorities.get(i).createJSON());
            }
            jo.put(packAuth.getKey().getIdentity(), ja);
        }
        return jo;
    }

    public void clear(long userId) {
        synchronized (this.packagesAuth) {
            this.packagesAuth.clear();
        }
    }
}
