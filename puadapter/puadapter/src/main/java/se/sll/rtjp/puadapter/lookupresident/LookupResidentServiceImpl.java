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
package se.sll.rtjp.puadapter.lookupresident;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import org.apache.log4j.Logger;

import riv.population.residentmaster.lookupresidentforfullprofile.v1.rivtabp21.LookupResidentForFullProfileResponderInterface;
import riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileResponseType;
import riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.LookupResidentForFullProfileType;
import riv.population.residentmaster.lookupresidentforfullprofileresponder.v1.ObjectFactory;

/**
 * Implementation of the LookupResidentServiceImpl that just produces an empty LookupResidentForFullProfileResponseType
 * for the response, that will be populated with information later in the flow.
 * 
 * The filtering according to the request parameters have been handled already earlier in the flow.
 */
public class LookupResidentServiceImpl implements LookupResidentForFullProfileResponderInterface {

    @Override
    @WebResult(name = "LookupResidentForFullProfileResponse", targetNamespace = "urn:riv:population:residentmaster:LookupResidentForFullProfileResponder:1", partName = "parameters")
    @WebMethod(operationName = "LookupResidentForFullProfile", action = "urn:riv:population:residentmaster:LookupResidentForFullProfileResponder:1:LookupResidentForFullProfile")
    public LookupResidentForFullProfileResponseType lookupResidentForFullProfile(
            @WebParam(partName = "LogicalAddress", name = "LogicalAddress", targetNamespace = "urn:riv:itintegration:registry:1", header = true) String arg0,
            @WebParam(partName = "parameters", name = "LookupResidentForFullProfile", targetNamespace = "urn:riv:population:residentmaster:LookupResidentForFullProfileResponder:1") LookupResidentForFullProfileType arg1) {
        ObjectFactory of = new ObjectFactory();
        LookupResidentForFullProfileResponseType ret = of.createLookupResidentForFullProfileResponseType();
        return ret;
    }

}
