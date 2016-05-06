package com.fr.bi.web.conf.services.packs;

import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.provider.BISystemPackageConfigurationProvider;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BIGetPackageGroupAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "get_business_package_group";
    }

    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        BISystemPackageConfigurationProvider packageManager = BIConfigureManagerCenter.getPackageManager();
        JSONObject jo = new JSONObject().put("packages", packageManager.createPackageJSON(userId))
                .put("groups", packageManager.createGroupJSON(userId));
        WebUtils.printAsJSON(res, jo);

    }
}
