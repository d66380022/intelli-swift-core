package com.fr.bi.cluster.zookeeper;

import com.fr.base.FRContext;
import com.fr.bi.cluster.wrapper.ZooKeeperWrapper;
import com.fr.bi.cluster.zookeeper.exception.MissionNotStopException;
import com.fr.bi.cluster.zookeeper.exception.NotInitializeException;
import com.fr.bi.cluster.zookeeper.watcher.BIWorker;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.general.FRLogger;
import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Connery on 2015/3/28.
 * 成为主节点后，当worker出现后。初始化controller。
 * 开始分配任务。
 */
public class BIMasterController implements BIMissionListener, Watcher {
    private static final FRLogger LOG = FRContext.getLogger();
    ArrayList<BIWorkerNodeValue> currentMission;
    private ZooKeeperWrapper zk;
    private BIMissionDispatcher dispatcher;
    private BIMissionMonitor monitor;
    private CountDownLatch countDownLatch = new CountDownLatch(1);
//    private static final Logger LOG = LoggerFactory.getLogger(BIClusterMissionManager.class);
    private boolean isInitialized;

    public BIMasterController(ZooKeeperWrapper zk) {
        this.zk = zk;
    }
    public BIMasterController(ZooKeeperWrapper zk,CountDownLatch countDownLatch) {
        this.zk = zk;
        this.countDownLatch = countDownLatch;
    }
    public void registerMissionListener(BIMissionListener listener) {
        monitor.registerListener(listener);
    }

    private void init() {
        dispatcher = BIMissionDispatcher.getInstance();
        dispatcher.init(zk);
        monitor = new BIMissionMonitor();
        monitor.init(zk, this);
        isInitialized = true;
    }

    public CountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    /**
     * 一定要确保worker路径下有工作节点存在，才能初始化。
     * 否则，监听worker节点，等到子节点出现，再初始化。
     */
    public void startWork() {
        List children = null;
        try {
            if(zk.exists(BIWorker.PARENT_PATH, false)==null){
                try {
                    zk.create(BIWorker.PARENT_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                } catch (Exception ex) {
                    BILoggerFactory.getLogger().error(ex.getMessage(), ex);
//                LOG.error("Zookeeper已经存在" + PARENT_PATH + "路径，报错信息：" + ex.toString());
                }
            }
            children = zk.getChildren(BIWorker.PARENT_PATH, false);
        } catch (Exception ex) {
             BILoggerFactory.getLogger(BIMasterController.class).error(ex.getMessage(), ex);
        }
        if (children != null && !children.isEmpty()) {
            init();
        } else {
            try {
                zk.getChildren(BIWorker.PARENT_PATH, this);
            } catch (Exception ex) {
                 BILoggerFactory.getLogger(BIMasterController.class).error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public void process(WatchedEvent event) {
//        LOG.info("worker路径下存在工作节点");
        BILoggerFactory.getLogger(BIMasterController.class).info("worker路径下存在工作节点");
        this.countDownLatch.countDown();
        init();
        checkMission();
    }

    public void addMission(ArrayList<BIWorkerNodeValue> mission) {
        dispatcher.addMission(mission);
        if (isInitialized) {
            //如果没有初始化，那么等到初始化时候，再check
            checkMission();
        } else {
//            LOG.info("当前controller没有初始化，需等待到worker路径下存在工作节点再初始化");
        }
    }

    /**
     * 检查当前任务。
     * 如果没有任务在进行，同时有任务待分配，那么分配任务。
     */
    private void checkMission() {
        if (monitor.isNotMonitorMission() && dispatcher.getMissionSize() > 0) {
            dispatcherMission();
        }
    }

    private void dispatcherMission() {
//        LOG.info("开始新的一轮任务");
//        LOG.info("任务堆栈中还有任务个数为：" + dispatcher.getMissionSize());
        if (dispatcher.getMissionSize() > 0) {
            ArrayList<BIWorkerNodeValue> mission = null;
            try {
                mission = dispatcher.dispatcherMission();
            } catch (KeeperException ex) {
                 BILoggerFactory.getLogger(BIMasterController.class).error(ex.getMessage(), ex);
            } catch (NotInitializeException ex) {
                 BILoggerFactory.getLogger(BIMasterController.class).error(ex.getMessage(), ex);

            } catch (InterruptedException ex) {
                 BILoggerFactory.getLogger(BIMasterController.class).error(ex.getMessage(), ex);

            }
            if (mission != null && !mission.isEmpty()) {
                BIWorkerNodeValue value = mission.get(0);
                long id = value.getMissionID();
                try {
                    monitor.monitorNewMission(id);
                } catch (MissionNotStopException ex) {
//                    LOG.error("上一轮任务还未终止");
                }
            } else {
//                LOG.error("未成功监视任务");
            }
        } else {

        }
    }

    @Override
    public void fireJobFinishedListener() {
        checkMission();
    }
}