package com.fr.swift.util;

import com.fr.swift.log.SwiftLoggers;

/**
 * This class created on 2018/5/26
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public class MonitorUtil {

    private static ThreadLocal<Long> tl = new ThreadLocal<Long>();

    public static void start() {
        tl.set(System.currentTimeMillis());
    }

    public static void finish(String methodName) {
        long finishTime = System.currentTimeMillis();
        SwiftLoggers.getLogger().debug("Invoke {} costs {} ms", methodName, finishTime - tl.get());
        tl.remove();
    }
}
