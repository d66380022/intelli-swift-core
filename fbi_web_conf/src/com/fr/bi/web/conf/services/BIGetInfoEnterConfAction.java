package com.fr.bi.web.conf.services;

import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.pack.data.IBusinessPackageGetterService;
import com.finebi.cube.conf.relation.relation.IRelationContainer;
import com.finebi.cube.conf.table.BIBusinessTable;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.conf.utils.BIModuleUtils;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.web.conf.AbstractBIConfigureAction;
import com.fr.fs.web.service.ServiceUtils;
import com.fr.json.JSONArray;
import com.fr.json.JSONObject;
import com.fr.web.utils.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Young's on 2016/4/11.
 */
public class BIGetInfoEnterConfAction extends AbstractBIConfigureAction {
    @Override
    protected void actionCMDPrivilegePassed(HttpServletRequest req, HttpServletResponse res) throws Exception {
        long userId = ServiceUtils.getCurrentUserID(req);
        JSONObject allFields = getAllFields(userId);
        JSONObject relations = createJSONWithTableName(userId, allFields);
        JSONObject translations = BICubeConfigureCenter.getAliasManager().getTransManager(userId).createJSON();
        JSONObject jo = new JSONObject();
        jo.put("relations", relations);
        jo.put("translations", translations);
        jo.put("fields", allFields);
        jo.put("update_settings", BIConfigureManagerCenter.getUpdateFrequencyManager().createJSON(userId));
        WebUtils.printAsJSON(res, jo);
    }

    @Override
    public String getCMD() {
        return "get_translations_relations_fields_4_conf";
    }

    private JSONObject getAllFields(long userId) {
        JSONObject fields = new JSONObject();
        Set<IBusinessPackageGetterService> packs = BICubeConfigureCenter.getPackageManager().getAllPackages(userId);
        try {
            for (IBusinessPackageGetterService p : packs) {
                for (BIBusinessTable t : (Set<BIBusinessTable>) p.getBusinessTables()) {
                    Iterator<BusinessField> iterator = t.getFields().iterator();
                    while (iterator.hasNext()) {
                        BusinessField field = iterator.next();
                        fields.put(BIModuleUtils.getBusinessFieldById(field.getFieldID()).getFieldID().getIdentity(), field.createJSON());
                    }
                }
            }
        } catch (Exception e) {
            BILoggerFactory.getLogger().error(e.getMessage(),e);
        }
        return fields;
    }

    private JSONObject createJSONWithTableName(long userId, JSONObject allFields) throws Exception {
        Set<BITableRelation> connectionSet = BICubeConfigureCenter.getTableRelationManager().getAllTableRelation(userId);
        Map<BusinessTable, IRelationContainer> primKeyMap = BICubeConfigureCenter.getTableRelationManager().getAllTable2PrimaryRelation(userId);
        Map<BusinessTable, IRelationContainer> foreignKeyMap = BICubeConfigureCenter.getTableRelationManager().getAllTable2ForeignRelation(userId);
        Iterator<Map.Entry<BusinessTable, IRelationContainer>> primIter = primKeyMap.entrySet().iterator();
        Iterator<Map.Entry<BusinessTable, IRelationContainer>> foreignIter = foreignKeyMap.entrySet().iterator();
        JSONArray setJO = new JSONArray();
        for (BITableRelation relation : connectionSet) {
            // young 2016.12.22 如果存在无用的关联(all fields里已经找不到相关字段的)
            if (allFields.has(relation.getPrimaryKey().getFieldID().getIdentityValue()) &&
                    allFields.has(relation.getForeignKey().getFieldID().getIdentityValue())) {
                setJO.put(relation.createJSON());
            }
        }
        JSONObject jo = new JSONObject();
        jo.put("connectionSet", setJO);
        jo.put("primKeyMap", getPrimKeyMap(primIter, allFields));
        jo.put("foreignKeyMap", getForKeyMap(foreignIter, allFields));
        return jo;
    }

    private JSONObject getPrimKeyMap(Iterator<Map.Entry<BusinessTable, IRelationContainer>> it, JSONObject allFields) throws Exception {
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<BusinessTable, IRelationContainer> entry = it.next();
            String primaryId = null;
            Set<BITableRelation> relations = entry.getValue().getContainer();
            Map<String, JSONArray> tableRelationMap = new HashMap<String, JSONArray>();
            for (BITableRelation relation : relations) {
                if (allFields.has(relation.getPrimaryKey().getFieldID().getIdentityValue()) &&
                        allFields.has(relation.getForeignKey().getFieldID().getIdentityValue())) {
                    JSONArray ja = new JSONArray();
                    primaryId = relation.getPrimaryField().getFieldID().getIdentityValue();
                    if (tableRelationMap.containsKey(primaryId)) {
                        ja = tableRelationMap.get(primaryId);
                    }
                    ja.put(relation.createJSON());
                    tableRelationMap.put(primaryId, ja);
                }
            }
            Set<String> tableRelationKeySet = tableRelationMap.keySet();
            for (String primaryFieldId : tableRelationKeySet) {
                if (tableRelationMap.get(primaryFieldId) != null) {
                    jo.put(primaryFieldId, tableRelationMap.get(primaryFieldId));
                }
            }
        }
        return jo;
    }

    private JSONObject getForKeyMap(Iterator<Map.Entry<BusinessTable, IRelationContainer>> it, JSONObject allFields) throws Exception {
        JSONObject jo = new JSONObject();
        while (it.hasNext()) {
            Map.Entry<BusinessTable, IRelationContainer> entry = it.next();
            String foreignId = null;
            Set<BITableRelation> relations = entry.getValue().getContainer();
            Map<String, JSONArray> tableRelationMap = new HashMap<String, JSONArray>();
            for (BITableRelation relation : relations) {
                if (allFields.has(relation.getPrimaryKey().getFieldID().getIdentityValue()) &&
                        allFields.has(relation.getForeignKey().getFieldID().getIdentityValue())) {
                    JSONArray ja = new JSONArray();
                    foreignId = relation.getForeignField().getFieldID().getIdentity();
                    if (tableRelationMap.containsKey(foreignId)) {
                        ja = tableRelationMap.get(foreignId);
                    }
                    ja.put(relation.createJSON());
                    tableRelationMap.put(foreignId, ja);
                }
            }
            Set<String> tableRelationKeySet = tableRelationMap.keySet();
            for (String foreignFieldId : tableRelationKeySet) {
                if (tableRelationMap.get(foreignFieldId) != null) {
                    jo.put(foreignFieldId, tableRelationMap.get(foreignFieldId));
                }
            }

        }
        return jo;
    }
}
