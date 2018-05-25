package com.fr.swift.config.dao;

import com.fr.swift.config.bean.SwiftMetaDataBean;
import com.fr.swift.config.entity.SwiftMetaDataEntity;

import java.util.List;

/**
 * @author yee
 * @date 2018/5/25
 */
public interface SwiftMetaDataDAO extends SwiftConfigDAO<SwiftMetaDataEntity> {
    SwiftMetaDataBean findBySourceKey(String sourceKey);

    SwiftMetaDataBean findByTableName(String tableName);

    boolean addOrUpdateSwiftMetaData(SwiftMetaDataBean metaDataBean);

    boolean deleteSwiftMetaDataBean(String sourceKey);

    List<SwiftMetaDataBean> findAll();
}
