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

import java.nio.charset.Charset;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transformer.types.DataTypeFactory;

import riv.population.residentmaster.v1.AvregistreringTYPE;
import riv.population.residentmaster.v1.AvregistreringsorsakKodKomplettTYPE;
import riv.population.residentmaster.v1.CivilstandKodTYPE;
import riv.population.residentmaster.v1.CivilstandTYPE;
import riv.population.residentmaster.v1.JaNejTYPE;
import riv.population.residentmaster.v1.KonTYPE;
import riv.population.residentmaster.v1.NamnTYPE;
import riv.population.residentmaster.v1.ObjectFactory;
import riv.population.residentmaster.v1.PersonpostTYPE;
import riv.population.residentmaster.v1.RelationPersonIdTYPE;
import riv.population.residentmaster.v1.RelationerTYPE;
import riv.population.residentmaster.v1.UtlandsadressTYPE;
import riv.population.residentmaster.v1.RelationerTYPE.Relation;
import riv.population.residentmaster.v1.ResidentType;
import riv.population.residentmaster.v1.SvenskAdressTYPE;
import se.sll.rtjp.puadapter.extractor.PKNODPLUS;
import se.sll.rtjp.puadapter.extractor.ResidentExtractor;

/**
 * <p>
 * Transformer class that handles transformation between a string with a SNOD response and a {@link ResidentType} from
 * the RIV response specification.
 * </p>
 */
public class SnodResponseToResidentTypeTransformer extends AbstractTransformer {

    /**
     * Constructor according to Mule standard that registers the transformer with Mule properly and defines what source
     * and target types it handles.
     */
    public SnodResponseToResidentTypeTransformer() {
        super();
        this.registerSourceType(DataTypeFactory.STRING);
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

        System.out.println("line: <" + src + ">");

        // Create helper objects
        ResidentExtractor re = new ResidentExtractor((String) src);
        ObjectFactory objectFactory = new ObjectFactory();

        // Create and populate ResidentType root object
        ResidentType transformedResident = createAndPopulateResidentType(re, objectFactory);

        // Create and populate PersonpostTYPE subobject
        transformedResident.setPersonpost(createPersonTagFromRE(re, objectFactory));

        return transformedResident;
    }

    private ResidentType createAndPopulateResidentType(ResidentExtractor re, ObjectFactory objectFactory) {
        ResidentType transformedResident = objectFactory.createResidentType();
        transformedResident.setSenasteAndringFolkbokforing(re.getField(PKNODPLUS.SENASTE_REG_DATUM));
        transformedResident.setSekretessmarkering(JaNejTYPE.N);
        return transformedResident;
    }

    private PersonpostTYPE createPersonTagFromRE(ResidentExtractor re, ObjectFactory objectFactory) {
        PersonpostTYPE personPost = objectFactory.createPersonpostTYPE();
        String avregOrsak = re.getField(PKNODPLUS.AVGÅNGSKOD);
        if (!avregOrsak.equals("") && !avregOrsak.equals("6")) {
            if (re.getField(PKNODPLUS.CIVILSTÅND).equals("6")) {
                personPost.setAvregistrering(getAvregistreringTypeFromSnodResponse(
                        re.getField(PKNODPLUS.CIVILSTÅNDSDATUM), "1"));
            } else {
                personPost.setAvregistrering(getAvregistreringTypeFromSnodResponse(
                    re.getField(PKNODPLUS.SENASTE_REG_DATUM), avregOrsak));
            }
        }

        // Create and populate the CivilstandTYPE element.
        if (!re.getField(PKNODPLUS.CIVILSTÅND).equals("6")) {
            CivilstandTYPE civstand = objectFactory.createCivilstandTYPE();
            civstand.setCivilstandsdatum(re.getField(PKNODPLUS.CIVILSTÅNDSDATUM));
            civstand.setCivilstandKod(getCivilstandKodTYPEFromKod(re.getField(PKNODPLUS.CIVILSTÅND)));
            personPost.setCivilstand(civstand);
        }

        // TODO - PU does not expose any birth info except for the date?
        /*FodelseTYPE fodelse = objectFactory.createFodelseTYPE(); 
        HemortSverigeTYPE hemortSverige = objectFactory.createHemortSverigeTYPE();
        // hemortSverige.setFodelseforsamling(value) ????
        // hemortSverige.setFodelselanKod(); ???
        fodelse.setHemortSverige(hemortSverige);

        OrtUtlandetTYPE ortUtlandet = objectFactory.createOrtUtlandetTYPE();
        // ortUtlandet.setFodelseland(value);
        // ortUtlandet.setFodelseortUtland(value);
        // ortUtlandet.setStyrkt(value);
        fodelse.setOrtUtlandet(ortUtlandet);*/
        //personPost.setFodelse(fodelse);

        personPost.setFodelsetid(re.getField(PKNODPLUS.FÖDELESDATUM));

        SvenskAdressTYPE svenskAdress = objectFactory.createSvenskAdressTYPE();
        svenskAdress.setFastighetsbeteckning(re.getField(PKNODPLUS.FASTIGHETSBETECKNING));
        svenskAdress.setFolkbokforingsdatum(re.getField(PKNODPLUS.FOLKF_ÅR_FASTIGHET));
        svenskAdress.setForsamlingKod(re.getField(PKNODPLUS.FÖRSAMLING));
        svenskAdress.setKommunKod(re.getField(PKNODPLUS.KOMMUN));
        svenskAdress.setLanKod(re.getField(PKNODPLUS.LÄN));
        svenskAdress.setPostNr(re.getField(PKNODPLUS.FBF_POSTNUMMER));
        svenskAdress.setPostort(re.getField(PKNODPLUS.FBF_POSTORT));
        svenskAdress.setUtdelningsadress1(re.getField(PKNODPLUS.FBF_UTDEL_ADRESS_1));
        svenskAdress.setUtdelningsadress2(re.getField(PKNODPLUS.FBF_UTDEL_ADRESS_2));
        svenskAdress.setCareOf(re.getField(PKNODPLUS.FBF_CO_ADRESS));
        personPost.setFolkbokforingsadress(svenskAdress);

        // PU automatically follows links for Hanvisningsnummer and returns information for the most current identity.
        // personPost.setHanvisningsPersonNr();

        // personPost.setInvandring(value) // TODO!
        
        int konSiffra = (Integer.valueOf(re.getField(PKNODPLUS.AKTUELLT_PERSONNUMMER).substring(10, 11)));
        personPost.setKon(konSiffra % 2 == 0 ? KonTYPE.K : KonTYPE.M);

        NamnTYPE namnType = objectFactory.createNamnTYPE();
        namnType.setAviseringsnamn(new String(re.getField(PKNODPLUS.AVISERINGSNAMN).getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8")));
        namnType.setEfternamn(re.getField(PKNODPLUS.EFTERNAMN));
        namnType.setFornamn(re.getField(PKNODPLUS.FÖRNAMN));
        namnType.setMellannamn(re.getField(PKNODPLUS.MELLANNAMN));
        // Tilltalsnamn is already indicated with '/' around the name(/s) to use.
        personPost.setNamn(namnType);

        personPost.setPersonId(re.getField(PKNODPLUS.AKTUELLT_PERSONNUMMER));

        RelationerTYPE relationer = objectFactory.createRelationerTYPE();

        Relation relationEtt = objectFactory.createRelationerTYPERelation();
        System.out.println("Relation1_TYP: " + re.getField(PKNODPLUS.RELATION_1_TYP));
        System.out.println("Relation2_TYP: " + re.getField(PKNODPLUS.RELATION_2_TYP));
        // relationEtt.setRelationstyp(RelationstypTYPE.fromValue(re.getField(PKNODPLUS.RELATION_1_TYP)));  //TODO
        RelationPersonIdTYPE relationEttPnr = new RelationPersonIdTYPE();
        relationEttPnr.setPersonNr(re.getField(PKNODPLUS.RELATION_1_PNR));
        relationEtt.setRelationId(relationEttPnr);
        relationer.getRelation().add(relationEtt);

        Relation relationTvå = objectFactory.createRelationerTYPERelation();
        // relationTvå.setRelationstyp(RelationstypTYPE.fromValue(re.getField(PKNODPLUS.RELATION_2_TYP)));   //TODO
        RelationPersonIdTYPE relationTvåPnr = new RelationPersonIdTYPE();
        relationTvåPnr.setPersonNr(re.getField(PKNODPLUS.RELATION_2_PNR));
        relationTvå.setRelationId(relationTvåPnr);
        relationer.getRelation().add(relationTvå);

        personPost.setRelationer(relationer);

        SvenskAdressTYPE särskildAdress = objectFactory.createSvenskAdressTYPE();
        särskildAdress.setCareOf(re.getField(PKNODPLUS.SÄRSKILD_CO_ADRESS));
        särskildAdress.setPostNr(re.getField(PKNODPLUS.SÄRSKILD_POSTNUMMER));
        särskildAdress.setPostort(re.getField(PKNODPLUS.SÄRSKILD_POSTORT));
        särskildAdress.setUtdelningsadress1(re.getField(PKNODPLUS.SÄRSKILD_UTDEL_ADRESS_1));
        särskildAdress.setUtdelningsadress2(re.getField(PKNODPLUS.SÄRSKILD_UTDEL_ADRESS_2));
        personPost.setSarskildPostadress(särskildAdress);

        UtlandsadressTYPE utlandsAdress = objectFactory.createUtlandsadressTYPE();
        utlandsAdress.setLand(re.getField(PKNODPLUS.UTLAND_ADRESS_LAND));
        utlandsAdress.setUtdelningsadress1(re.getField(PKNODPLUS.UTLAND_ADRESS_RAD_1));
        utlandsAdress.setUtdelningsadress2(re.getField(PKNODPLUS.UTLAND_ADRESS_RAD_2));
        utlandsAdress.setUtdelningsadress3(re.getField(PKNODPLUS.UTLAND_ADRESS_RAD_3));
        personPost.setUtlandsadress(utlandsAdress);
        return personPost;
    }

    private CivilstandKodTYPE getCivilstandKodTYPEFromKod(String civstandKod) {
        if (!"6".equals(civstandKod)) {
            // 6 is a code for deceased which is not handled here in the contract.
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

    private AvregistreringTYPE getAvregistreringTypeFromSnodResponse(String avrDatum, String avrOrsak) {
        AvregistreringTYPE avreg = new AvregistreringTYPE();
        avreg.setAvregistreringsorsakKodKomplett(getAvregistreringsorsakKodFromAvrOrsak(avrOrsak));
        avreg.setAvregistreringsdatum(avrDatum);

        return avreg;
    }

    private AvregistreringsorsakKodKomplettTYPE getAvregistreringsorsakKodFromAvrOrsak(String avrOrsak) {
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
