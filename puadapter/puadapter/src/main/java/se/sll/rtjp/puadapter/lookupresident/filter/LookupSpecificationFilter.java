/**
 *  Copyright (c) 2013 SLL <http://sll.se/>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package se.sll.rtjp.puadapter.lookupresident.filter;

import java.util.Iterator;
import java.util.List;

import riv.population.residentmaster.v1.ResidentType;

/**
 * Handles the filter logic for the 'lookUpSpecification'-parameter list in the SOAP Request. Filters both individual
 * {@link ResidentType} objects and Lists of the same.
 */
public class LookupSpecificationFilter {

    /**
     * Filters a single {@link ResidentType} object 'resident' according to the filter logic in 'fp'. If a filter type
     * is NULL the filter is inactive, then all the residents fit this type. If a filter is not null the filter is
     * active and is matched against the corresponding information in the incoming 'resident'. According to
     * specifications if multiple filters are active at the same time, treat these as AND-blocks.
     * 
     * @param resident
     *            The {@link ResidentType} to be filtered.
     * @param fp
     *            The {@link FilterParameters} to use as rules.
     * @return true if the ResidentType fits within the search parameters (should be returned).
     */
    public static boolean isResidentInFilters(ResidentType resident, FilterParameters fp) {
        boolean isInAvregFilter = false, isInSekrFilter = false, isInRegDatumFilter = false;
        
        // Filter Avregistreringsorsak
        try {
            if (fp.getFilterAvregOrsak() != null) {
                boolean isResidentAvregNullOrEmpty = resident.getPersonpost().getAvregistrering() == null
                        || resident.getPersonpost().getAvregistrering().getAvregistreringsorsakKodKomplett() == null
                        || resident.getPersonpost().getAvregistrering().getAvregistreringsorsakKodKomplett().equals("");
                if (fp.getFilterAvregOrsak().equals("  ")) {
                    // For everyone that is not Avregistrerad (two spaces since the schema has a minLen="2")
                    if (isResidentAvregNullOrEmpty) {
                        isInAvregFilter = true;
                    }
                } else if (fp.getFilterAvregOrsak().equals("OO")) {
                    // For everyone that has AvregKod != "AV” or ”GN” 
                    if (!isResidentAvregNullOrEmpty && !"AV".equals(resident.getPersonpost().getAvregistrering().getAvregistreringsorsakKodKomplett().toString()) &&
                            !"GN".equals(resident.getPersonpost().getAvregistrering().getAvregistreringsorsakKodKomplett().toString())) {
                        isInAvregFilter = true;
                    }
                } else if (!isResidentAvregNullOrEmpty && fp.getFilterAvregOrsak().equals(resident.getPersonpost().getAvregistrering().getAvregistreringsorsakKodKomplett().toString())) {
                    // For everyone when the incoming AvregOrsak matches the AvregOrsak from the resident
                    isInAvregFilter = true;
                }
            } else {
                isInAvregFilter = true;
            }
        } catch (NullPointerException e) {
        }

        // Filter Sekteressmarkering
        if (fp.getFilterSekretessMarkering() != null) {
            if (fp.getFilterSekretessMarkering().equals(resident.getSekretessmarkering())) {
                isInSekrFilter = true;
            }
        } else {
            isInSekrFilter = true;
        }

        // Filter SenasteAndringFolkbokforing
        try {
            if (fp.getFilterSenasteAndringFBF() != null) {
                if (Long.valueOf(resident.getSenasteAndringFolkbokforing()) > Long.valueOf(fp.getFilterSenasteAndringFBF())) {
                    isInRegDatumFilter = true;
                }
            } else {
                isInRegDatumFilter = true;
            }
        } catch (NumberFormatException e) {

        }

        return (isInAvregFilter && isInRegDatumFilter && isInSekrFilter);
    }

    /**
     * Filters a list of {@link ResidentType} objects. See isResidentInFilters() for the actual filter implementation.
     * 
     * @param residentList
     *            A list of {@link ResidentType} objects to be filtered.
     * @param filterParams
     *            The {@link FilterParameters} to use as rules.
     * @return The same list as 'residentList' but with all the residents not fitting the search terms removed.
     */
    public static List<ResidentType> filterResidentList(List<ResidentType> residentList, FilterParameters filterParams) {
        Iterator<ResidentType> iter = residentList.iterator();
        while (iter.hasNext()) {
            ResidentType currentResident = iter.next();
            if (!LookupSpecificationFilter.isResidentInFilters(currentResident, filterParams)) {
                iter.remove();
            }
        }
        return residentList;
    }

}
