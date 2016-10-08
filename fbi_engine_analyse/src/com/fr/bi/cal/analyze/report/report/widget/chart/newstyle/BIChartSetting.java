package com.fr.bi.cal.analyze.report.report.widget.chart.newstyle;

import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by User on 2016/8/31.
 */
public interface BIChartSetting{

    JSONArray formatTypes(JSONArray data, JSONArray types) throws JSONException;

    JSONObject formatItems(JSONArray data, JSONArray types, JSONObject options) throws JSONException;

    JSONObject formatConfig(JSONObject options, JSONArray data) throws JSONException;

    JSONObject getConvertedDataAndSettings(JSONArray data, JSONArray types, JSONObject options) throws JSONException;
}
