/**
 * Copyright Ambud Sharma
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GrafanaOutput implements Serializable, Comparable<GrafanaOutput> {

	private static final long serialVersionUID = 1L;

	private String target;
	private List<Number[]> datapoints;

	public GrafanaOutput(String target) {
		this.target = target;
		datapoints = new ArrayList<>();
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @param target
	 *            the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @return the datapoints
	 */
	public List<Number[]> getDatapoints() {
		return datapoints;
	}

	/**
	 * @param datapoints
	 *            the datapoints to set
	 */
	public void setDatapoints(List<Number[]> datapoints) {
		this.datapoints = datapoints;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Target [target=" + target + ", datapoints=" + datapoints + "]";
	}

	public void sort() {
		Collections.sort(datapoints, new Comparator<Number[]>() {

			@Override
			public int compare(Number[] o1, Number[] o2) {
				return Long.compare(o1[1].longValue(), o2[1].longValue());
			}

		});
	}

	@Override
	public int compareTo(GrafanaOutput o) {
		return this.target.compareTo(o.target);
	}

}