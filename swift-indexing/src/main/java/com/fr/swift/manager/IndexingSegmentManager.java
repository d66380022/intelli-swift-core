package com.fr.swift.manager;

import com.fr.swift.config.entity.SwiftTablePathEntity;
import com.fr.swift.config.service.SwiftTablePathService;
import com.fr.swift.segment.container.SegmentContainer;
import com.fr.swift.source.SourceKey;
import com.fr.third.springframework.stereotype.Service;

/**
 * @author yee
 * @date 2018/7/19
 */
@Service("indexingSegmentManager")
public class IndexingSegmentManager extends LineSegmentManager {
    public IndexingSegmentManager() {
        super(SegmentContainer.INDEXING);
    }

    @Override
    protected Integer getCurrentFolder(SwiftTablePathService service, SourceKey sourceKey) {
        SwiftTablePathEntity entity = service.get(sourceKey.getId());
        if (null == entity) {
            entity = new SwiftTablePathEntity(sourceKey.getId(), 0);
            service.saveOrUpdate(entity);
        }
        return entity.getTmpDir();
    }
}
