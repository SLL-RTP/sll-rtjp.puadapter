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

import riv.population.residentmaster.v1.JaNejTYPE;

/**
 * Model class for holding information about the selected filter parameters from the SOAP request.
 */
public class FilterParameters {

    /** The currently active filter for 'Sekretessmarkering', or null if inactive. */
    private JaNejTYPE filterSekretessMarkering;
    /**
     * The currently active filter for 'Avregistreringsorsak',
     * "" if only non-unregistered persons should be returned or null if inactive.
     * Is currently a String since the empty value is not supported by the generated enum.
     * The wsdl does not allow "" for input, but the spec. requires handling it as a parameter.
     * So this is a temporary fix until that bug is sorted out.
     */
    private String filterAvregOrsak;
    /** The currently active filter for 'SenasteAndringFolkbokforing', or null if inactive. */
    private String filterSenasteAndringFBF;
    
    /**
     * Used when created from Mule flow from XPATH where all incoming data is strings.
     * 
     * @param filterAvregOrsak The currently active filter for 'Avregistreringsorsak', or null if inactive.
     * @param filterSekretessMarkering The currently active filter for 'Sekretessmarkering', or null if inactive.
     * @param senasteAndringFBF The currently active filter for 'SenasteAndringFolkbokforing', or null if inactive.
     */
    public FilterParameters(String filterAvregOrsak, String filterSekretessMarkering, String senasteAndringFBF) {
       this.filterSekretessMarkering = (filterSekretessMarkering != null && filterSekretessMarkering != "") ? JaNejTYPE.valueOf(filterSekretessMarkering) : null;
       this.filterAvregOrsak = (filterAvregOrsak != null) ? filterAvregOrsak : null;
       this.filterSenasteAndringFBF = senasteAndringFBF != null && !senasteAndringFBF.equals("") ? senasteAndringFBF : null;
    }
    
    /**
     * @return the filterSekretessMarkering
     */
    public JaNejTYPE getFilterSekretessMarkering() {
        return filterSekretessMarkering;
    }
    /**
     * @param filterSekretessMarkering the filterSekretessMarkering to set
     */
    public void setFilterSekretessMarkering(JaNejTYPE filterSekretessMarkering) {
        this.filterSekretessMarkering = filterSekretessMarkering;
    }
    /**
     * @return the filterAvregDatum
     */
    public String getFilterAvregOrsak() {
        return filterAvregOrsak;
    }
    /**
     * @param filterAvregOrsak the filterAvregDatum to set
     */
    public void setFilterAvregOrsak(String filterAvregOrsak) {
        this.filterAvregOrsak = filterAvregOrsak;
    }
    /**
     * @return the SenasteAndringFBF
     */
    public String getFilterSenasteAndringFBF() {
        return filterSenasteAndringFBF;
    }
    /**
     * @param senasteAndringFBF the SenasteAndringFBF to set
     */
    public void setFilterSenasteAndringFBF(String senasteAndringFBF) {
        this.filterSenasteAndringFBF = senasteAndringFBF;
    }

}
