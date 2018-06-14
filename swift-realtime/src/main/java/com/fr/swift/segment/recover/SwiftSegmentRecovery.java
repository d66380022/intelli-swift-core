package com.fr.swift.segment.recover;

import com.fr.swift.context.SwiftContext;
import com.fr.swift.cube.io.Types.StoreType;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.db.Table;
import com.fr.swift.db.impl.SwiftDatabase;
import com.fr.swift.db.impl.SwiftDatabase.Schema;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.HistorySegmentImpl;
import com.fr.swift.segment.RealTimeSegmentImpl;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.SwiftDataOperatorProvider;
import com.fr.swift.segment.SwiftSegmentManager;
import com.fr.swift.segment.operator.Inserter;
import com.fr.swift.source.SourceKey;
import com.fr.swift.source.SwiftMetaData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anchore
 * @date 2018/5/23
 */
public class SwiftSegmentRecovery implements SegmentRecovery {
    private SwiftDataOperatorProvider operators = SwiftContext.getInstance().getBean(SwiftDataOperatorProvider.class);

    private SwiftSegmentManager manager = SwiftContext.getInstance().getBean(SwiftSegmentManager.class);

    @Override
    public void recover(SourceKey tableKey) {
        List<Segment> segs = getUnstoredSegments(tableKey);
        for (Segment seg : segs) {
            try {
                Table table = SwiftDatabase.getInstance().getTable(tableKey);
                Inserter insert = operators.getInserter(table, newRealtimeSegment(seg));
                insert.insertData(new BackupResultSet(getBackupSegment(seg)));
            } catch (Exception e) {
                SwiftLoggers.getLogger().error(e);
            }
        }
    }

    private Segment getBackupSegment(Segment realtimeSeg) {
        SwiftMetaData meta = realtimeSeg.getMetaData();
        String realtimeSegPath = realtimeSeg.getLocation().getPath();
        return new HistorySegmentImpl(new ResourceLocation(realtimeSegPath.replace(meta.getSwiftSchema().dir, Schema.BACKUP_CUBE.dir), StoreType.FINE_IO), meta);
    }

    private Segment newRealtimeSegment(Segment realtimeSeg) {
        return new RealTimeSegmentImpl(realtimeSeg.getLocation(), realtimeSeg.getMetaData());
    }

    private List<Segment> getUnstoredSegments(SourceKey tableKey) {
        List<Segment> segs = manager.getSegment(tableKey),
                unstoredSegs = new ArrayList<Segment>();
        for (Segment seg : segs) {
            if (seg.getLocation().getStoreType() == StoreType.MEMORY) {
                unstoredSegs.add(seg);
            }
        }
        return unstoredSegs;
    }

    @Override
    public void recoverAll() {
        List<Table> tables;
        try {
            tables = SwiftDatabase.getInstance().getAllTables();
            for (Table table : tables) {
                recover(table.getSourceKey());
            }
        } catch (SQLException e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    private static final SegmentRecovery INSTANCE = new SwiftSegmentRecovery();

    private SwiftSegmentRecovery() {
    }

    public static SegmentRecovery getInstance() {
        return INSTANCE;
    }
}