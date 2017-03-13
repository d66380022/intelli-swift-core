package com.fr.bi.web.conf.services.cubetask;

import com.fr.bi.manager.PerformancePlugManager;
import com.fr.bi.stable.utils.code.BIPrintUtils;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by roy on 2016/12/14.
 */
public class BISetTransportThreadPoolSizeAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        String size = WebUtils.getHTTPRequestParameter(req, "value");

        try {
            PerformancePlugManager.getInstance().setBiTransportThreadPoolSize(Integer.parseInt(size));
            WebUtils.printAsJSON(res, new JSONObject().put("message:", "the cube TransportThreadPool size has been set:" + PerformancePlugManager.getInstance().getBiTransportThreadPoolSize()));
        } catch (Exception e) {
            WebUtils.printAsJSON(res, new JSONObject().put("message:", BIPrintUtils.outputException(e)));
        }
    }

    @Override
    public String getCMD() {
        return "set_transport_thread_pool_size";
    }
}
