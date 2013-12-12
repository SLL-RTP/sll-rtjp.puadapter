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

import java.io.InputStream;
import java.util.Scanner;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.types.DataTypeFactory;

import riv.population.residentmaster.v1.CivilstandTYPE;
import riv.population.residentmaster.v1.JaNejTYPE;
import riv.population.residentmaster.v1.KonTYPE;
import riv.population.residentmaster.v1.NamnTYPE;
import riv.population.residentmaster.v1.ObjectFactory;
import riv.population.residentmaster.v1.PersonpostTYPE;
import riv.population.residentmaster.v1.RelationPersonIdTYPE;
import riv.population.residentmaster.v1.RelationerTYPE;
import riv.population.residentmaster.v1.RelationstypTYPE;
import riv.population.residentmaster.v1.UtlandsadressTYPE;
import riv.population.residentmaster.v1.RelationerTYPE.Relation;
import riv.population.residentmaster.v1.ResidentType;
import riv.population.residentmaster.v1.SvenskAdressTYPE;
import se.sll.rtjp.puadapter.extractor.ResidentExtractor;
import se.sll.rtjp.puadapter.extractor.fields.PKNODPLUS;

/**
 * <p>
 * Transformer class that handles transformation between a string with a SNOD response and a {@link ResidentType} from
 * the RIV response specification.
 * </p>
 */
public class PKNODPLUSToResidentTypeTransformer extends SNODAbstractTransformer {

    /**
     * Constructor according to Mule standard that registers the transformer with Mule properly and defines what source
     * and target types it handles.
     */
    public PKNODPLUSToResidentTypeTransformer() {
        super();
        this.registerSourceType(DataTypeFactory.create(InputStream.class));
        this.setReturnDataType(DataTypeFactory.create(ResidentType.class));
    }

    /**
     * Handles the transformation itself. Uses regular java substrings to extract the required information.
     * 
     * @param src
     *            a String with a PKNOD* service response.
     * @param enc
     *            The encoding of the string in question.
     * @return A {@link ResidentType}-object populated with all the data in src.
     */
    @Override
    protected Object doTransform(Object src, String enc) throws TransformerException {
        InputStream test = (InputStream) src;
        Scanner streamScanner = new Scanner(test, "ISO-8859-15");
        streamScanner.useDelimiter("\\A");
        String inputStreamString = streamScanner.next();
        streamScanner.close();
        
        System.out.println("line: <" + inputStreamString + ">");
        
        // Create helper objects
        setResidentExtractor(new ResidentExtractor(inputStreamString));
        ObjectFactory objectFactory = new ObjectFactory();

        // Create and populate ResidentType root object
        ResidentType transformedResident = createAndPopulateResidentType(objectFactory);

        // Create and populate PersonpostTYPE subobject
        transformedResident.setPersonpost(createPersonTagFromRE(objectFactory));

        return transformedResident;
    }

    private ResidentType createAndPopulateResidentType(ObjectFactory objectFactory) {
        ResidentType transformedResident = objectFactory.createResidentType();
        if (!"00000000".equals(getValueOrNull(PKNODPLUS.SENASTE_REG_DATUM))) {
            transformedResident.setSenasteAndringFolkbokforing(getValueOrNull(PKNODPLUS.SENASTE_REG_DATUM));
        }
        transformedResident.setSekretessmarkering(getValue(PKNODPLUS.RETURKOD).equals("0002") ? JaNejTYPE.J : JaNejTYPE.N);
        return transformedResident;
    }

    private PersonpostTYPE createPersonTagFromRE(ObjectFactory objectFactory) {
        PersonpostTYPE personPost = objectFactory.createPersonpostTYPE();
        String avregOrsak = getValue(PKNODPLUS.AVGÅNGSKOD);
        if (!avregOrsak.equals("") && !avregOrsak.equals("6")) {
            if (getValue(PKNODPLUS.CIVILSTÅND).equals("6")) {
                personPost.setAvregistrering(getAvregistreringTypeFromSnodResponse(
                        getValue(PKNODPLUS.CIVILSTÅNDSDATUM), "1"));
            } else {
                personPost.setAvregistrering(getAvregistreringTypeFromSnodResponse(
                        getValue(PKNODPLUS.SENASTE_REG_DATUM), avregOrsak));
            }
        }

        // Create and populate the CivilstandTYPE element.
        if (getValue(PKNODPLUS.CIVILSTÅND).equals("6")) {
            CivilstandTYPE civstand = objectFactory.createCivilstandTYPE();
            civstand.setCivilstandsdatum(getValueOrNull(PKNODPLUS.CIVILSTÅNDSDATUM));
            civstand.setCivilstandKod(getCivilstandKodTYPEFromKod(getValue(PKNODPLUS.CIVILSTÅND)));
            personPost.setCivilstand(civstand);
        }

        // PU does not expose any birth info except for the date
        // FodelseTYPE fodelse = objectFactory.createFodelseTYPE();

        personPost.setFodelsetid(getValueOrNull(PKNODPLUS.FÖDELESDATUM));

        SvenskAdressTYPE svenskAdress = objectFactory.createSvenskAdressTYPE();
        svenskAdress.setFastighetsbeteckning(getValueOrNull(PKNODPLUS.FASTIGHETSBETECKNING));
        svenskAdress.setFolkbokforingsdatum(getValueOrNull(PKNODPLUS.FOLKF_ÅR_FASTIGHET));
        svenskAdress.setForsamlingKod(getValueOrNull(PKNODPLUS.FÖRSAMLING));
        svenskAdress.setKommunKod(getValueOrNull(PKNODPLUS.KOMMUN));
        svenskAdress.setLanKod(getValueOrNull(PKNODPLUS.LÄN));
        String fbfPostNr = getValueOrNull(PKNODPLUS.FBF_POSTNUMMER);
        if (fbfPostNr != null) {
            // The postnummer must be 3+2 with a space in the middle to fit the schema, and PU serves it with no space.
            svenskAdress.setPostNr(fbfPostNr.substring(0, 3) + " " + fbfPostNr.substring(3));
        }
        svenskAdress.setPostort(getValueOrNull(PKNODPLUS.FBF_POSTORT));
        svenskAdress.setUtdelningsadress1(getValueOrNull(PKNODPLUS.FBF_UTDEL_ADRESS_1));
        svenskAdress.setUtdelningsadress2(getValueOrNull(PKNODPLUS.FBF_UTDEL_ADRESS_2));
        svenskAdress.setCareOf(getValueOrNull(PKNODPLUS.FBF_CO_ADRESS));
        personPost.setFolkbokforingsadress(svenskAdress);

        // PU automatically follows links for Hanvisningsnummer and returns information for the most current identity.
        // personPost.setHanvisningsPersonNr();

        int konSiffra = (Integer.valueOf(getValue(PKNODPLUS.AKTUELLT_PERSONNUMMER).substring(10, 11)));
        personPost.setKon(konSiffra % 2 == 0 ? KonTYPE.K : KonTYPE.M);

        NamnTYPE namnType = objectFactory.createNamnTYPE();
        namnType.setAviseringsnamn(getValueOrNull(PKNODPLUS.AVISERINGSNAMN));
        namnType.setEfternamn(getValueOrNull(PKNODPLUS.EFTERNAMN));
        namnType.setFornamn(getValueOrNull(PKNODPLUS.FÖRNAMN));
        namnType.setMellannamn(getValueOrNull(PKNODPLUS.MELLANNAMN));
        // Tilltalsnamn is already indicated with '/' around the name(/s) to use.
        personPost.setNamn(namnType);

        personPost.setPersonId(getValueOrNull(PKNODPLUS.AKTUELLT_PERSONNUMMER));

        RelationerTYPE relationer = objectFactory.createRelationerTYPE();
        if (!getValueOrNull(PKNODPLUS.RELATION_1_PNR).equals("000000000000")) {
            Relation relationEtt = objectFactory.createRelationerTYPERelation();
            relationEtt.setRelationstyp(RelationstypTYPE.fromValue(getValueOrNull(PKNODPLUS.RELATION_1_TYP)));
            RelationPersonIdTYPE relationEttPnr = new RelationPersonIdTYPE();
            relationEttPnr.setPersonNr(getValueOrNull(PKNODPLUS.RELATION_1_PNR));
            relationEtt.setRelationId(relationEttPnr);
            relationer.getRelation().add(relationEtt);
        }

        if (!getValueOrNull(PKNODPLUS.RELATION_2_PNR).equals("000000000000")) {
            Relation relationTvå = objectFactory.createRelationerTYPERelation();
            relationTvå.setRelationstyp(RelationstypTYPE.fromValue(getValueOrNull(PKNODPLUS.RELATION_1_TYP)));
            RelationPersonIdTYPE relationTvåPnr = new RelationPersonIdTYPE();
            relationTvåPnr.setPersonNr(getValueOrNull(PKNODPLUS.RELATION_2_PNR));
            relationTvå.setRelationId(relationTvåPnr);
            relationer.getRelation().add(relationTvå);
        }

        personPost.setRelationer(relationer);

        SvenskAdressTYPE särskildAdress = objectFactory.createSvenskAdressTYPE();
        särskildAdress.setCareOf(getValueOrNull(PKNODPLUS.SÄRSKILD_CO_ADRESS));
        String sarskildPnr = getValueOrNull(PKNODPLUS.SÄRSKILD_POSTNUMMER);
        if (sarskildPnr != null) {
            // The postnummer must be 3+2 with a space in the middle to fit the schema, and PU serves it with no space.
            särskildAdress.setPostNr(sarskildPnr.substring(0, 3) + " " + sarskildPnr.substring(3));
        }
        särskildAdress.setPostort(getValueOrNull(PKNODPLUS.SÄRSKILD_POSTORT));
        särskildAdress.setUtdelningsadress1(getValueOrNull(PKNODPLUS.SÄRSKILD_UTDEL_ADRESS_1));
        särskildAdress.setUtdelningsadress2(getValueOrNull(PKNODPLUS.SÄRSKILD_UTDEL_ADRESS_2));
        
        personPost.setSarskildPostadress(särskildAdress);

        UtlandsadressTYPE utlandsAdress = objectFactory.createUtlandsadressTYPE();
        utlandsAdress.setLand(getValueOrNull(PKNODPLUS.UTLAND_ADRESS_LAND));
        utlandsAdress.setUtdelningsadress1(getValueOrNull(PKNODPLUS.UTLAND_ADRESS_RAD_1));
        utlandsAdress.setUtdelningsadress2(getValueOrNull(PKNODPLUS.UTLAND_ADRESS_RAD_2));
        utlandsAdress.setUtdelningsadress3(getValueOrNull(PKNODPLUS.UTLAND_ADRESS_RAD_3));
        personPost.setUtlandsadress(utlandsAdress);
        return personPost;
    }
    
}
