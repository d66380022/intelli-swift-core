package com.fr.swift.property;

import com.fr.third.springframework.beans.factory.annotation.Autowired;
import com.fr.third.springframework.beans.factory.annotation.Value;
import com.fr.third.springframework.stereotype.Service;

/**
 * This class created on 2018/6/8
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
@Service
public class SwiftProperty {

    private String serverAddress;

    private String masterAddress;

    private String rpcAddress;

    private boolean clusterIsConfigure;

    private boolean isCluster;

    @Autowired
    public void setRpcAddress(@Value("${swift.http_server_address}") String rpcAddress) {
        this.rpcAddress = rpcAddress;
    }

    @Autowired
    public void setServerAddress(@Value("${swift.master_address}") String serverAddress) {
        this.serverAddress = serverAddress;
    }

    @Autowired
    public void setMasterAddress(@Value("${rpc.server_address}") String masterAddress) {
        this.masterAddress = masterAddress;
    }

    @Autowired
    public void setClusterIsConfigure(@Value("${swift.cluster_is_configure}") String clusterWhetherConfigure) {
        this.clusterIsConfigure = Boolean.parseBoolean(clusterWhetherConfigure);
    }

    @Autowired
    public void setCluster(@Value("${swift.is_cluster}") String cluster) {
        this.isCluster = Boolean.parseBoolean(cluster);
    }

    public String getRpcAddress() {
        return rpcAddress;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public boolean isClusterIsConfigure() {
        return clusterIsConfigure;
    }

    public boolean isCluster() {
        return isCluster;
    }
}
