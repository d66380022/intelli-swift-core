package com.finebi.cube.structure.table.property;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.data.input.ICubeIntegerReaderWrapper;
import com.finebi.cube.data.input.ICubeLongReaderWrapper;
import com.finebi.cube.data.input.ICubeStringReader;
import com.finebi.cube.data.output.ICubeIntegerWriterWrapper;
import com.finebi.cube.data.output.ICubeLongWriterWrapper;
import com.finebi.cube.data.output.ICubeStringWriter;
import com.finebi.cube.exception.BIResourceInvalidException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.ICubeTablePropertyService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.property.BICubeProperty;
import com.finebi.cube.structure.property.BICubeVersion;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.structure.collection.list.IntList;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.utils.program.BINonValueUtils;
import com.fr.json.JSONObject;

import java.util.*;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeTableProperty implements ICubeTablePropertyService {
    private ICubeResourceLocation currentLocation;
    private static String MAIN_DATA = "field";
    private static String ROW_COUNT_DATA = "count";

    private static String LAST_EXECUTE_TIME = "lastExecuteTime";
    private static String CURRENT_EXECUTE_TIME = "currentExecuteTime";
    private static String SUPER_TABLES = "st";

    private static String REMOVED_LIST = "removedList";

    private List<ICubeFieldSource> tableFields = null;
    private List<ITableKey> parentTable = null;

    private ICubeResourceDiscovery discovery;

    private ICubeStringWriter fieldInfoWriter;
    private ICubeStringReader fieldInfoReader;
    private ICubeStringWriter parentsWriter;
    private ICubeStringReader parentsReader;
    private BICubeVersion version;

    private ICubeLongWriterWrapper rowCountWriter;
    private ICubeLongReaderWrapper rowCountReader;

    private ICubeLongWriterWrapper lastExecuteTimeWriter;
    private ICubeLongReaderWrapper lastExecuteTimeReader;

    private ICubeLongWriterWrapper currentExecuteTimeWriter;
    private ICubeLongReaderWrapper currentExecuteTimeReader;

    private ParentFieldProperty parentFieldProperty;
    private ICubeIntegerWriterWrapper removeListWriter;
    private ICubeIntegerReaderWrapper removeListReader;

    public BICubeTableProperty(ICubeResourceLocation currentLocation, ICubeResourceDiscovery discovery) {
        this.currentLocation = currentLocation.copy();
        this.discovery = discovery;
        parentFieldProperty = new ParentFieldProperty(currentLocation, discovery);
        version = new BICubeVersion(currentLocation, discovery);
    }

    protected boolean isFieldWriterAvailable() {
        return fieldInfoWriter != null;
    }

    protected void resetFiledWriter() {
        if (isFieldWriterAvailable()) {
            fieldInfoWriter.clear();
            fieldInfoWriter = null;
        }
    }


    protected boolean isFieldReaderAvailable() {
        return fieldInfoReader != null;
    }


    protected boolean isRowCountWriterAvailable() {
        return rowCountWriter != null;
    }

    protected boolean isRowCountReaderAvailable() {
        return rowCountReader != null;
    }

    protected boolean isRemoveListReaderAvailable() {
        return removeListReader != null;
    }

    protected boolean isRemoveListWriterAvailable() {
        return removeListWriter != null;
    }

    protected boolean isLastExecuteTimeWriterAvailable() {
        return lastExecuteTimeWriter != null;
    }

    protected boolean isLastExecuteTimeReaderAvailable() {
        return lastExecuteTimeReader != null;
    }

    protected boolean isCurrentExecuteTimeWriterAvailable() {
        return currentExecuteTimeWriter != null;
    }

    protected boolean isCurrentExecuteTimeReaderAvailable() {
        return currentExecuteTimeReader != null;
    }

    protected boolean isParentWriterAvailable() {
        return parentsWriter != null;
    }

    protected boolean isParentReaderAvailable() {
        return parentsReader != null;
    }

    protected boolean isFieldInit() {
        return tableFields != null;
    }

    protected boolean isParentTableInit() {
        return parentTable != null;
    }

    private void initialFieldInfoReader() throws Exception {
        ICubeResourceLocation mainLocation = this.currentLocation.buildChildLocation(MAIN_DATA);
        mainLocation.setStringType();
        mainLocation.setReaderSourceLocation();
        fieldInfoReader = (ICubeStringReader) discovery.getCubeReader(mainLocation);

    }

    private void initialFieldInfoWriter() throws Exception {
        ICubeResourceLocation mainLocation = this.currentLocation.buildChildLocation(MAIN_DATA);
        mainLocation.setStringType();
        mainLocation.setWriterSourceLocation();
        fieldInfoWriter = (ICubeStringWriter) discovery.getCubeWriter(mainLocation);
    }

    private void initialRowCountWriter() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(ROW_COUNT_DATA);
        rowCountLocation.setLongTypeWrapper();
        rowCountLocation.setWriterSourceLocation();
        rowCountWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(rowCountLocation);

    }

    private void initialRowCountReader() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(ROW_COUNT_DATA);
        rowCountLocation.setLongTypeWrapper();
        rowCountLocation.setReaderSourceLocation();
        rowCountReader = (ICubeLongReaderWrapper) discovery.getCubeReader(rowCountLocation);

    }

    private void initialRemovedListWriter() throws Exception {
        ICubeResourceLocation location = this.currentLocation.buildChildLocation(REMOVED_LIST);
        location.setIntegerTypeWrapper();
        location.setWriterSourceLocation();
        removeListWriter = (ICubeIntegerWriterWrapper) discovery.getCubeWriter(location);

    }

    private void initialRemovedListReader() throws Exception {
        ICubeResourceLocation location = this.currentLocation.buildChildLocation(REMOVED_LIST);
        location.setIntegerTypeWrapper();
        location.setReaderSourceLocation();
        removeListReader = (ICubeIntegerReaderWrapper) discovery.getCubeReader(location);

    }

    private void initialLastExecuteTimeReader() throws Exception {
        ICubeResourceLocation lastExecuteTimeLocation = this.currentLocation.buildChildLocation(LAST_EXECUTE_TIME);
        lastExecuteTimeLocation.setLongTypeWrapper();
        lastExecuteTimeLocation.setReaderSourceLocation();
        lastExecuteTimeReader = (ICubeLongReaderWrapper) discovery.getCubeReader(lastExecuteTimeLocation);

    }

    private void initialLastExecuteTimeWriter() throws Exception {
        ICubeResourceLocation lastExecuteTimeLocation = this.currentLocation.buildChildLocation(LAST_EXECUTE_TIME);
        lastExecuteTimeLocation.setLongTypeWrapper();
        lastExecuteTimeLocation.setWriterSourceLocation();
        lastExecuteTimeWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(lastExecuteTimeLocation);
    }

    private void initialCurrentExecuteTimeReader() throws Exception {
        ICubeResourceLocation currentExecuteTimeLocation = this.currentLocation.buildChildLocation(CURRENT_EXECUTE_TIME);
        currentExecuteTimeLocation.setLongTypeWrapper();
        currentExecuteTimeLocation.setReaderSourceLocation();
        currentExecuteTimeReader = (ICubeLongReaderWrapper) discovery.getCubeReader(currentExecuteTimeLocation);

    }

    private void initialCurrentExecuteTimeWriter() throws Exception {
        ICubeResourceLocation currentExecuteTimeLocation = this.currentLocation.buildChildLocation(CURRENT_EXECUTE_TIME);
        currentExecuteTimeLocation.setLongTypeWrapper();
        currentExecuteTimeLocation.setWriterSourceLocation();
        currentExecuteTimeWriter = (ICubeLongWriterWrapper) discovery.getCubeWriter(currentExecuteTimeLocation);
    }


    private void initialParentTableReader() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(SUPER_TABLES);
        rowCountLocation.setStringType();
        rowCountLocation.setReaderSourceLocation();
        parentsReader = (ICubeStringReader) discovery.getCubeReader(rowCountLocation);

    }

    private void initialParentTableWriter() throws Exception {
        ICubeResourceLocation rowCountLocation = this.currentLocation.buildChildLocation(SUPER_TABLES);
        rowCountLocation.setStringType();
        rowCountLocation.setWriterSourceLocation();
        parentsWriter = (ICubeStringWriter) discovery.getCubeWriter(rowCountLocation);
    }

    public ICubeStringWriter getFieldInfoWriter() {
        try {
            if (!isFieldWriterAvailable()) {
                initialFieldInfoWriter();
            }
            return fieldInfoWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeStringReader getFieldInfoReader() {
        try {
            if (!isFieldReaderAvailable()) {
                initialFieldInfoReader();
            }
            return fieldInfoReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }


    public ICubeLongWriterWrapper getRowCountWriter() {
        try {
            if (!isRowCountWriterAvailable()) {
                initialRowCountWriter();
            }
            return rowCountWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongReaderWrapper getRowCountReader() {
        try {
            if (!isRowCountReaderAvailable()) {
                initialRowCountReader();
            }
            return rowCountReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongWriterWrapper getLastExecuteTimeWriter() {
        try {
            if (!isLastExecuteTimeWriterAvailable()) {
                initialLastExecuteTimeWriter();
            }
            return lastExecuteTimeWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongReaderWrapper getLastExecuteTimeReader() {
        try {
            if (!isLastExecuteTimeReaderAvailable()) {
                initialLastExecuteTimeReader();
            }
            return lastExecuteTimeReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongWriterWrapper getCurrentExecuteTimeWriter() {
        try {
            if (!isCurrentExecuteTimeWriterAvailable()) {
                initialCurrentExecuteTimeWriter();
            }
            return currentExecuteTimeWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeLongReaderWrapper getCurrentExecuteTimeReader() {
        try {
            if (!isCurrentExecuteTimeReaderAvailable()) {
                initialCurrentExecuteTimeReader();
            }
            return currentExecuteTimeReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeStringWriter getParentsWriter() {
        try {
            if (!isParentWriterAvailable()) {
                initialParentTableWriter();
            }
            return parentsWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeStringReader getParentsReader() {
        try {
            if (!isParentReaderAvailable()) {
                initialParentTableReader();
            }
            return parentsReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    private void initialField() {
        if (!isFieldInit()) {
            synchronized (this) {
                if (!isFieldInit()) {
                    if (getFieldInfoReader().canRead()) {
                        tableFields = new ArrayList<ICubeFieldSource>();
                        try {
                            int columnSize = Integer.parseInt(getFieldInfoReader().getSpecificValue(0));
                            for (int pos = 1; pos <= columnSize; pos++) {
                                JSONObject jo = new JSONObject(getFieldInfoReader().getSpecificValue(pos));
                                BICubeFieldSource field = new BICubeFieldSource(null, null, 0, 0);
                                field.parseJSON(jo);
                                tableFields.add(field);
                            }
                        } catch (BIResourceInvalidException e) {
                            BILoggerFactory.getLogger().error(e.getMessage(), e);
                            BINonValueUtils.beyondControl(e.getMessage(), e);
                        } catch (Exception e) {
                            BILoggerFactory.getLogger().error(e.getMessage(), e);
                            BINonValueUtils.beyondControl(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }

    private void initialParentsTable() {
        if (getParentsReader().canRead() && !isParentTableInit()) {
            parentTable = new ArrayList<ITableKey>();
            ICubeStringReader parentReader = getParentsReader();
            try {
                int columnSize = Integer.parseInt(parentReader.getSpecificValue(0));
                for (int pos = 1; pos <= columnSize; pos++) {
                    parentTable.add(new BITableKey(parentReader.getSpecificValue(pos)));
                }
            } catch (BIResourceInvalidException e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                BINonValueUtils.beyondControl(e.getMessage(), e);
            } catch (Exception e) {
                BILoggerFactory.getLogger().error(e.getMessage(), e);
                BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    public ICubeIntegerWriterWrapper getRemovedListWriter() {
        try {
            if (!isRemoveListWriterAvailable()) {
                initialRemovedListWriter();
            }
            return removeListWriter;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    public ICubeIntegerReaderWrapper getRemovedListReader() {
        try {
            if (!isRemoveListReaderAvailable()) {
                initialRemovedListReader();
            }
            return removeListReader;
        } catch (Exception e) {
            throw BINonValueUtils.beyondControl(e);
        }
    }

    @Override
    public void recordTableStructure(List<ICubeFieldSource> fields) {
        /**
         * 即便是空，也要记录是空数组0的长度。
         */
        if (fields == null) {
            fields = new ArrayList<ICubeFieldSource>();
        }
        Iterator<ICubeFieldSource> fieldIterator = fields.iterator();
        int position = 0;
        getFieldInfoWriter().recordSpecificValue(position, String.valueOf(fields.size()));//First position records size of columns.
        position++;
        while (fieldIterator.hasNext()) {
            ICubeFieldSource field = fieldIterator.next();
            try {
                getFieldInfoWriter().recordSpecificValue(position, field.createJSON().toString());
                position++;
            } catch (Exception e) {
                BINonValueUtils.beyondControl("the field:" + field.toString() + " createJson method has problem.");
            }
        }
        getFieldInfoWriter().forceRelease();
    }


    @Override
    public void recordRowCount(long rowCount) {
        getRowCountWriter().recordSpecificValue(0, rowCount);

    }

    @Override
    public void recordRemovedList(int position, int value) {
        getRemovedListWriter().recordSpecificValue(position, value);
    }

    @Override
    public void recordLastExecuteTime(long time) {
        getLastExecuteTimeWriter().recordSpecificValue(0,time);
    }

    @Override
    public void recordCurrentExecuteTime() {
        getCurrentExecuteTimeWriter().recordSpecificValue(0,System.currentTimeMillis());
    }

    @Override
    public void recordParentsTable(List<ITableKey> parentTables) {
        ICubeStringWriter writer = getParentsWriter();
        writer.recordSpecificValue(0, Integer.toString(parentTables.size()));
        int count = 1;
        for (ITableKey key : parentTables) {
            writer.recordSpecificValue(count++, key.getSourceID());
        }
    }

    @Override
    public List<ITableKey> getParentsTable() {
        initialParentsTable();
        return parentTable;
    }

    @Override
    public int getRowCount() {
        try {
            return Integer.parseInt(String.valueOf(getRowCountReader().getSpecificValue(0)));
        } catch (BIResourceInvalidException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return -1;
    }

    @Override
    public IntList getRemovedList() {
        ICubeIntegerReaderWrapper removedListReader = getRemovedListReader();
        IntList removedList = new IntList();
        int i = 0;
        try {
            /*removeList.size=1*/
            if (removedListReader.getSpecificValue(0) >= 0 && removedListReader.getSpecificValue(1) < 0) {
                removedList.add(removedListReader.getSpecificValue(0));
            } else {
                while (removedListReader.getSpecificValue(i) < removedListReader.getSpecificValue(i + 1)) {
                    removedList.add(removedListReader.getSpecificValue(i));
                    i++;
                }
                removedList.add(removedListReader.getSpecificValue(i));
            }
        } catch (BIResourceInvalidException e) {
            BILoggerFactory.getLogger().error(e.getMessage());
        }
        return removedList;
    }

    @Override
    public Date getLastExecuteTime() {
        try {
            return new Date(getLastExecuteTimeReader().getSpecificValue(0));
        } catch (BIResourceInvalidException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Date getCurrentExecuteTime() {
        try {
            return new Date(getCurrentExecuteTimeReader().getSpecificValue(0));
        } catch (BIResourceInvalidException e) {
            BILoggerFactory.getLogger().error(e.getMessage(), e);
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
        return null;
    }

    public long getCubeVersion() {
        return version.getCubeVersion();
    }

    @Override
    public void addVersion(long version) {
        this.version.addVersion(version);
    }

    @Override
    public List<ICubeFieldSource> getFieldInfo() {
        initialField();
        return tableFields;
    }

    @Override
    public Boolean isPropertyExist() {
        return getFieldInfoReader().canRead();
    }

    @Override
    public Boolean isRowCountAvailable() {
        return getRowCountReader().canRead();
    }

    @Override
    public Boolean isVersionAvailable() {
        return version.isVersionAvailable();
    }

    protected void resetFieldReader() {
        if (isFieldReaderAvailable()) {
            fieldInfoReader.clear();
            fieldInfoReader = null;
        }
    }

    @Override
    public void recordFieldNamesFromParent(Set<String> fieldNames) {
        parentFieldProperty.recordParentFields(fieldNames);
    }

    @Override
    public Set<String> getFieldNamesFromParent() {
        return parentFieldProperty.getParentFields();
    }


    protected void resetRowCountWriter() {
        if (isRowCountWriterAvailable()) {
            rowCountWriter.clear();
            rowCountWriter = null;
        }
    }

    protected void resetRowCountReader() {
        if (isRowCountReaderAvailable()) {
            rowCountReader.clear();
            rowCountReader = null;
        }
    }

    protected void resetLastExecuteTimeWriter() {
        if (isLastExecuteTimeWriterAvailable()) {
            lastExecuteTimeWriter.clear();
            lastExecuteTimeWriter = null;
        }
    }

    protected void resetLastExecuteTimeReader() {
        if (isLastExecuteTimeReaderAvailable()) {
            lastExecuteTimeReader.clear();
            lastExecuteTimeReader = null;
        }
    }

    protected void resetCurrentExecuteTimeWriter() {
        if (isCurrentExecuteTimeWriterAvailable()) {
            currentExecuteTimeWriter.clear();
            currentExecuteTimeWriter = null;
        }
    }

    protected void resetCurrentExecuteTimeReader() {
        if (isLastExecuteTimeReaderAvailable()) {
            currentExecuteTimeReader.clear();
            currentExecuteTimeReader = null;
        }
    }

    protected void resetParentWriter() {
        if (isParentWriterAvailable()) {
            parentsWriter.clear();
            parentsWriter = null;
        }
    }

    protected void resetParentReader() {
        if (isParentReaderAvailable()) {
            parentsReader.clear();
            parentsReader = null;
        }
    }

    @Override
    public void clear() {
        resetFiledWriter();
        resetFieldReader();
        resetRowCountWriter();
        resetRowCountReader();
        resetCurrentExecuteTimeReader();
        resetCurrentExecuteTimeWriter();
        resetLastExecuteTimeReader();
        resetLastExecuteTimeWriter();
        resetParentReader();
        resetParentWriter();
        if (parentFieldProperty != null) {
            parentFieldProperty.clear();
        }
        if (version != null) {
            version.clear();
        }
    }

    public void forceRelease() {
        if (isFieldWriterAvailable()) {
            fieldInfoWriter.forceRelease();
        }
        if (isFieldReaderAvailable()) {
            fieldInfoReader.forceRelease();
        }

        if (isRowCountReaderAvailable()) {
            rowCountReader.forceRelease();
        }
        if (isRowCountWriterAvailable()) {
            rowCountWriter.forceRelease();
        }
        if (isLastExecuteTimeReaderAvailable()) {
            lastExecuteTimeReader.forceRelease();
        }
        if (isLastExecuteTimeWriterAvailable()) {
            lastExecuteTimeWriter.forceRelease();
        }
        if (isCurrentExecuteTimeReaderAvailable()) {
            currentExecuteTimeReader.forceRelease();
        }
        if (isCurrentExecuteTimeWriterAvailable()) {
            currentExecuteTimeWriter.forceRelease();
        }
        if (isParentWriterAvailable()) {
            parentsWriter.forceRelease();
        }
        if (isParentReaderAvailable()) {
            parentsReader.forceRelease();
        }
        parentFieldProperty.forceRelease();
        ((BICubeProperty) version).forceRelease();
        clear();
    }

    @Override
    public void forceReleaseWriter() {
        if (isFieldWriterAvailable()) {
            fieldInfoWriter.forceRelease();
            fieldInfoWriter = null;
        }
        if (isRowCountWriterAvailable()) {
            rowCountWriter.forceRelease();
            rowCountWriter = null;
        }
        if (isLastExecuteTimeWriterAvailable()) {
            lastExecuteTimeWriter.forceRelease();
            lastExecuteTimeWriter = null;
        }
        if (isCurrentExecuteTimeWriterAvailable()) {
            currentExecuteTimeWriter.forceRelease();
            currentExecuteTimeWriter = null;
        }
        if (isParentWriterAvailable()) {
            parentsWriter.forceRelease();
            parentsWriter = null;
        }
        if (parentFieldProperty != null) {
            parentFieldProperty.forceReleaseWriter();
            parentFieldProperty = null;
        }
        if (version != null) {
            (version).forceReleaseWriter();
            version = null;
        }
    }

    @Override
    public boolean isRemovedListAvailable() {
        return getRemovedListReader().canRead();
    }

    @Override
    public boolean isLastExecuteTimeAvailable() {
        return getLastExecuteTimeReader().canRead();
    }

    @Override
    public boolean isCurrentExecuteTimeAvailable() {
        return getCurrentExecuteTimeReader().canRead();
    }

    @Override
    public void buildStructure() {
        try {
            initialRemovedListWriter();
        } catch (Exception e) {
            BINonValueUtils.beyondControl(e.getMessage(), e);
        }
    }
}

