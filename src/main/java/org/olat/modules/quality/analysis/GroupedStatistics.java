/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.modules.quality.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Initial date: 12.09.2018<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
public class GroupedStatistics {
	
	private final Map<String, Map<Long, GroupedStatistic>> statistics = new HashMap<>();
	
	public GroupedStatistics() {
		//
	}
	
	public GroupedStatistics(Collection<GroupedStatistic> collection) {
		for (GroupedStatistic statistic : collection) {
			putStatistic(statistic);
		}
	}

	public GroupedStatistic getStatistic(String identifier, Long groupKey) {
		Map<Long, GroupedStatistic> grouped = statistics.get(identifier);
		if (grouped != null) {
			return grouped.get(groupKey);
		}
		return null;
	}
	
	public void putStatistic(GroupedStatistic statistic) {
		String identifier = statistic.getIdentifier();
		Long groupKey = statistic.getGroupKey();
		Map<Long, GroupedStatistic> grouped = statistics.get(identifier);
		if (grouped == null) {
			grouped = new HashMap<>();
			statistics.put(identifier, grouped);
		}
		grouped.put(groupKey, statistic);
	}
	
	public Map<Long, GroupedStatistic> getStatistics(String identifier) {
		return statistics.get(identifier);
	}
	
	public Map<String, GroupedStatistic> getStatistics(Long groupKey) {
		Map<String, GroupedStatistic> extracted = new HashMap<>();
		for (Map.Entry<String, Map<Long, GroupedStatistic>> entry : statistics.entrySet()) {
			String identifier = entry.getKey();
			Map<Long, GroupedStatistic> grouped = entry.getValue();
			GroupedStatistic statistic = grouped.get(groupKey);
			extracted.put(identifier, statistic);
		}
		return extracted;
	}

	public Collection<GroupedStatistic> getStatistics() {
		Collection<GroupedStatistic> all = new ArrayList<>();
		for (Map<Long, GroupedStatistic> grouped : statistics.values()) {
			all.addAll(grouped.values());
		}
		return all;
	}
}
