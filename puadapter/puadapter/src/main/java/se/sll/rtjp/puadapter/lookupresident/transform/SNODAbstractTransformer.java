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
package se.sll.rtjp.puadapter.lookupresident.transform;

import org.mule.transformer.AbstractTransformer;

import riv.population.residentmaster.v1.AvregistreringTYPE;
import riv.population.residentmaster.v1.AvregistreringsorsakKodKomplettTYPE;
import riv.population.residentmaster.v1.CivilstandKodTYPE;
import se.sll.rtjp.puadapter.extractor.ResidentExtractor;
import se.sll.rtjp.puadapter.extractor.fields.PKNODPLUS;
import se.sll.rtjp.puadapter.extractor.fields.SnodFieldsInterface;

/**
 * Abstract Mule Transformer for the different kinds of SNOD responses. Holds common logic for simplifying the transformation
 * logic.
 */
public abstract class SNODAbstractTransformer extends AbstractTransformer {
    
    /** The currently initialized ResidentExtractor from which information will be extracted. */
    private ResidentExtractor re;
    
    /**
     * Set the currently initialized ResidentExtractor from which information will be extracted.
     * @param re the current {@link ResidentExtractor} object.
     */
    void setResidentExtractor(ResidentExtractor re) {
        this.re = re;
    }

    /**
     * Fetches the currently initialized ResidentExtractor from which information will be extracted.
     * @return the current {@link ResidentExtractor} object.
     */
    ResidentExtractor getResidentExtractor() {
        return this.re;
    }

    /**
     * Extracts a value for a specific SnodFieldsInterface field from the currently initialized ResidentExtractor.
     * 
     * @param field The SnodFieldsInterface field to extract.
     * @return a String with the field value from the ResidentExtractor.
     */
    String getValue(SnodFieldsInterface field) {
        return re.getField(field);
    }

    /**
     * Convenience method that extracts a value for a specific SnodFieldsInterface field from the currently
     * initialized ResidentExtractor. Returns null when empty as oppsed to getValue().
     * @param field
     * @return
     */
    String getValueOrNull(SnodFieldsInterface field) {
        return "".equals(re.getField(field)) ? null : re.getField(field);
    }
    
    /**
     * Maps the civilstandKod from the SNOD Response to the CivilstandKodTYPE that ResidentMaster requires.
     * Values taken from the PKNODPLUS specification.
     *
     * @param civstandKod The civilstandKod to map. See {@link PKNODPLUS#CIVILSTÅND} for possible codes and
     * their names.
     * @return The mapped {@link CivilstandKodTYPE} enum value.
     */
    CivilstandKodTYPE getCivilstandKodTYPEFromKod(String civstandKod) {
        if (!"6".equals(civstandKod)) {
            // 6 is a code for deceased which is not handled in this tag of the contract.
            if ("A".equals(civstandKod)) {
                return CivilstandKodTYPE.OG;
            } else if ("B".equals(civstandKod)) {
                return CivilstandKodTYPE.G;
            } else if ("C".equals(civstandKod)) {
                return CivilstandKodTYPE.A;
            } else if ("D".equals(civstandKod)) {
                return CivilstandKodTYPE.S;
            } else if ("E".equals(civstandKod)) {
                return CivilstandKodTYPE.RP;
            } else if ("F".equals(civstandKod)) {
                return CivilstandKodTYPE.SP;
            } else if ("G".equals(civstandKod)) {
                return CivilstandKodTYPE.EP;
            }
        }

        return null;
    }

    /**
     * Maps the avrOrsak and avrDatum from the SNOD Response to the AvregistreringTYPE tag for Resident Master.
     *
     * @param avrOrsak The avregOrsak code from SNOD to use as source.
     * @param avrDatum The avrDatum date from SNOD to user as source.
     * @return The mapped {@link AvregistreringTYPE} tag.
     */
    AvregistreringTYPE getAvregistreringTypeFromSnodResponse(String avrDatum, String avrOrsak) {
        AvregistreringTYPE avreg = new AvregistreringTYPE();
        avreg.setAvregistreringsorsakKodKomplett(getAvregistreringsorsakKodFromAvrOrsak(avrOrsak));
        avreg.setAvregistreringsdatum(avrDatum);

        return avreg;
    }

    /**
     * Maps the avrOrsak from the SNOD Response to the AvregistreringsorsakKodKomplettTYPE that ResidentMaster requires.
     * Values taken from the PKNODPLUS specification.
     * 
     * @param avrOrsak The avregOrsak-code from PU to map. See {@link PKNODPLUS#AVGÅNGSKOD} for possible values.
     * @return The mapped AvregistreringsorsakKodKomplettTYPE enum value.
     */
    AvregistreringsorsakKodKomplettTYPE getAvregistreringsorsakKodFromAvrOrsak(String avrOrsak) {
        switch (Integer.valueOf(avrOrsak)) {
        case 1:
            return AvregistreringsorsakKodKomplettTYPE.AV;
        case 2:
            return AvregistreringsorsakKodKomplettTYPE.UV;
        case 3:
            return AvregistreringsorsakKodKomplettTYPE.OB;
        case 4:
            return AvregistreringsorsakKodKomplettTYPE.TA;
        case 5:
            return AvregistreringsorsakKodKomplettTYPE.GN;
            /*
             * case "6": return AvregistreringsorsakKodKomplettTYPE.AV;
             */
            // Is this supposed to be a national service? PU is Stockholm/Gotland centric
            // and indicates moves to other counties as '6'. But as far as the national
            // contract is concerned, the person is still living i Sweden, no?
        default:
            return null;
        }
    }
}
