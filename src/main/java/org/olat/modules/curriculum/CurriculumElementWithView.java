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
package org.olat.modules.curriculum;

import java.util.List;

import org.olat.repository.RepositoryEntryMyView;

/**
 * 
 * Initial date: 8 Oct 2018<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public interface CurriculumElementWithView extends CurriculumElementRef {
	
	public boolean isCurriculumMember();
	
	public CurriculumElementWithView getParent();
	
	public List<RepositoryEntryMyView> getEntries();
	
	public default boolean isParentOrSelf(CurriculumElementRef parentRef) {
		boolean hasParent = false;
		
		if(getKey().equals(parentRef.getKey())) {
			hasParent = true;
		} else {
			for(CurriculumElementWithView parentRow=getParent(); parentRow != null; parentRow=parentRow.getParent()) {
				if(parentRow.getKey().equals(parentRef.getKey())) {
					hasParent = true;
				}
			}
		}
		return hasParent;
	}
}
