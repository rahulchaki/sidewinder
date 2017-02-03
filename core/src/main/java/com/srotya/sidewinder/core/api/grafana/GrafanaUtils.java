/**
 * Copyright 2017 Ambud Sharma
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.srotya.sidewinder.core.api.grafana;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.ws.rs.NotFoundException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.srotya.sidewinder.core.filters.AndFilter;
import com.srotya.sidewinder.core.filters.ComplexFilter;
import com.srotya.sidewinder.core.filters.ContainsFilter;
import com.srotya.sidewinder.core.filters.Filter;
import com.srotya.sidewinder.core.filters.OrFilter;
import com.srotya.sidewinder.core.storage.DataPoint;
import com.srotya.sidewinder.core.storage.ItemNotFoundException;
import com.srotya.sidewinder.core.storage.StorageEngine;

/**
 * @author ambud
 */
public class GrafanaUtils {

	private GrafanaUtils() {
	}

	public static void queryAndGetData(StorageEngine engine, String dbName, long startTs, long endTs,
			List<Target> output, TargetSeries targetSeriesEntry) {
		Map<String, List<DataPoint>> points;
		try {
			// TODO fix query point
			points = engine.queryDataPoints(dbName, targetSeriesEntry.getMeasurementName(),
					targetSeriesEntry.getFieldName(), startTs, endTs, targetSeriesEntry.getTagList(),
					targetSeriesEntry.getTagFilter(), null);
		} catch (ItemNotFoundException e) {
			throw new NotFoundException(e.getMessage());
		}
		for (Entry<String, List<DataPoint>> entry : points.entrySet()) {
			List<DataPoint> dataPoints = entry.getValue();
			Target tar = new Target(entry.getKey());
			for (DataPoint dataPoint : dataPoints) {
				if (!dataPoint.isFp()) {
					tar.getDatapoints().add(new Number[] { dataPoint.getLongValue(), dataPoint.getTimestamp() });
				} else {
					tar.getDatapoints().add(new Number[] { dataPoint.getValue(), dataPoint.getTimestamp() });
				}
			}
			output.add(tar);
		}
	}

	public static void extractTargetsFromJson(JsonObject json, List<TargetSeries> targetSeries) {
		JsonArray targets = json.get("targets").getAsJsonArray();
		for (int i = 0; i < targets.size(); i++) {
			JsonObject jsonElement = targets.get(i).getAsJsonObject();
			if (jsonElement != null) {
				List<String> filterElements = new ArrayList<>();
				Filter<List<String>> filter = extractGrafanaFilter(jsonElement, filterElements);
				targetSeries.add(new TargetSeries(jsonElement.get("target").getAsString(),
						jsonElement.get("field").getAsString(), filterElements, filter,
						jsonElement.get("correlate").getAsBoolean()));
			}
			// ByteUtils.jsonArrayToStringList()
		}
	}

	public static Filter<List<String>> extractGrafanaFilter(JsonObject element, List<String> filterElements) {
		Stack<Filter<List<String>>> predicateStack = new Stack<>();
		JsonArray array = element.get("filters").getAsJsonArray();
		for (int i = 0; i < array.size(); i++) {
			JsonObject obj = array.get(i).getAsJsonObject();
			String val = obj.get("value").getAsString();
			if (obj.has("type")) {
				if (predicateStack.isEmpty()) {
					// error
					System.err.println("Error empty stack");
				} else {
					Filter<List<String>> pop = predicateStack.pop();
					if (val.equalsIgnoreCase("and")) {
						AndFilter<List<String>> andFilter = new AndFilter<>();
						andFilter.addFilter(pop);
						predicateStack.push(andFilter);
					} else if (val.equalsIgnoreCase("or")) {
						OrFilter<List<String>> andFilter = new OrFilter<>();
						andFilter.addFilter(pop);
						predicateStack.push(andFilter);
					} else {
						// error
						System.err.println("Error stack is not empty");
					}
				}
			} else {
				filterElements.add(val);
				ContainsFilter<String, List<String>> filter = new ContainsFilter<String, List<String>>(val);
				if (predicateStack.isEmpty()) {
					predicateStack.push(filter);
				} else {
					if (predicateStack.peek() instanceof ContainsFilter) {
						// error
						System.err.println("Stack contains bad filter");
					} else {
						((ComplexFilter<List<String>>) predicateStack.peek()).addFilter(filter);
					}
				}
			}
		}
		return predicateStack.pop();
	}

}
