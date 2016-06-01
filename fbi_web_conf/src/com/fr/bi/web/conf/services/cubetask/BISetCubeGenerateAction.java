package com.fr.bi.web.conf.services.cubetask;

import com.finebi.cube.conf.build.CubeBuildStuff;
import com.finebi.cube.conf.build.CubeBuildStuffManager;
import com.finebi.cube.conf.build.CubeBuildStuffManagerSingleTable;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.fr.bi.base.BIUser;
import com.fr.bi.stable.data.BITableID;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.stable.StringUtils;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BISetCubeGenerateAction extends AbstractBIConfigureAction {

    @Override
    public String getCMD() {
        return "set_cube_generate";
    }


    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req,
                                            HttpServletResponse res) throws Exception {

        long userId = ServiceUtils.getCurrentUserID(req);
        String tableId = WebUtils.getHTTPRequestParameter(req, "tableId");
        if (StringUtils.isEmpty(tableId)){
            CubeBuildStuff cubeBuildStuffManager= new CubeBuildStuffManager(new BIUser(userId));
            CubeTskBuild.CubeBuild(userId,cubeBuildStuffManager);            
        }else{
            CubeBuildStuff cubeBuildStuff = new CubeBuildStuffManagerSingleTable( new BIBusinessTable(new BITableID(tableId)),userId);
            CubeTskBuild.CubeBuild(userId, cubeBuildStuff);
        }
    }

}
