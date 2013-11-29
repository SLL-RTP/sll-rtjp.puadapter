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
package se.sll.rtjp.puadapter.extractor;

/**
 * <p>SNOD field specification for the "PKNODPLUS" PU service. Used by {@link ResidentExtractor}
 * to extract required fields from a generic SNOD response.</p>
 * 
 * <p>All the positions from the response used here is according to the specification
 * "Användning av nodtjänster i PU" (version 0.3 2010-11-19) with the difference that the
 * spec is starts the index at 1 and java at 0 so every position below is minus 1.</p>
 * 
 * <p>Since the domain model of the source system is in swedish I will refrain from translating those here,
 * and I will stay with swedish documenation for the field values taken from the specification.</p>
 */
public enum PKNODPLUS implements SnodFieldsInterface {

    // <FÄLTNAMN>                   (<Startindex>, <Slutindex>),
    /** Den eftersökta personidentiteten enligt anropet */
    SÖKT_PERSONNUMMER               (8,20),
    /** Födelsedatum på 8 siffror, ex "19010101" */
    FÖDELESDATUM                    (20, 28),
    /**
     *  Aktuell personidentitet (Personnummer, Samordningsnummer eller reservnummer)
     *  på 12 siffor, ex "191212121212". Kan vara annat än identiteten i anropet.
     */
    AKTUELLT_PERSONNUMMER           (28, 40),
    /**
     * Typ av personnummer
     * 1 = Uppgift från RSV, ordinarie personnummer
     * 2 = Utomlänspatient, ordinarie personnummer
     * 3 = Reservnummer med namnuppgift
     * 4 = Reservnummer utan namnuppgift
     * 5 = (ny kod) Uppgift från RSV, samordningsnummer 
     * 6 = (ny kod) Utomlänspatient, samordningsnummer
     */
    PERSONNUMMER_TYP                (40, 41),

    /** 
     * Sammanslaget namn begränsat till 35 tecken utifrån centralt regelverk.
     * efternamn och ev. mellannamn, samtliga förnamn (tilltalsnamnet inom // om sådant finns anmält).
     */
    AVISERINGSNAMN                  (41, 77),
    /** Med snedstreck (/) runt tilltalsnamn */
    FÖRNAMN                         (704, 784),
    /** Mellannamn. (vid vanliga dubbla efternamn ligger det enda som mellannamn). */ 
    MELLANNAMN                      (784, 824),
    /** Efternamn. */
    EFTERNAMN                       (824, 884),

    /**
     * A = Ogift
     * B = Gift
     * C = Änka eller änkling
     * D = Skild
     * E = Registrerad partner
     * F = Skild partner
     * G = Efterlevande partner
     * 6 = Avliden person
     * (RSV ser inte ”avliden” som ett särskilt civilstånd)
     */
    CIVILSTÅND                      (144, 145),
    /** Datum då civilstånd enligt ovan inträtt (dvs dödsdatum för avlidna) */
    CIVILSTÅNDSDATUM                (145, 153),

    /** Personnummer för sammanhörande person */
    PNR_SAMHÖRIG                    (153, 165),
    /**
     *  Blank = Svensk medborgare
     *  Numerisk = Årtal för svenskt medborgarskap
     *  Alfabetisk = Utländsk medborgare
     */
    NATIONALITET                    (165, 169),

    /** Årtal då person blev eller kommer att bli folkbokförd på län/kommun/församling nedan. */
    FOLKF_ÅR_FASTIGHET              (169, 173),
    /** Länskod på två siffror. */
    LÄN                             (173, 175),
    /** Kommunkod på två siffror. */
    KOMMUN                          (175, 177),
    /** Församlingskod på två siffror. */
    FÖRSAMLING                      (177, 179),

    /** Senaste aviseringsvecka på formen SSÅÅVV. */
    AVISERINGSVECKA                 (179, 185),
    /**
     * Blank = ej avregistrerad
     * 1 = Avliden
     * 2 = Utvandrad eller (för utomlänare) avregistrerad av annat skäl
     * 3 = Överförd till obefintlig-register eller (för utomlänare) avregistrerad av annat skäl
     * 4 = Tekniskt avregistrerad
     * 5 = Personnummerändrad
     * 6 = Utflyttad till annat län
     */
    AVGÅNGSKOD                      (185, 186),
    /**
     * Senaste insättning i registret
     * Blank = Ej insättning (dvs uppgift saknas?)
     * 1 = Född
     * 2 = Invandrad
     * 3 = Överförd från obefintlig-register
     * 4 = Tekniskt insatt
     * 5 = Personnummerändrad
     * 6 = Inflyttad från annat län
     */
    INSÄTTNINGSKOD                  (186, 187),
    /** Senaste datum för ändring av personuppgifterna. */
    SENASTE_REG_DATUM               (187, 195),
    /** Ansvarig enhet för personuppgifterna. EXTERN om hämtade från RSV = utomlänare. */
    ANSVARIG_ENHET                  (211, 219),

    /** Tidigare känd identitet för samma fysiska person */
    TIDIGARE_IDENTITET_1            (219, 231),
    /** Tidigare känd identitet för samma fysiska person */
    TIDIGARE_IDENTITET_2            (231, 243),
    /** Tidigare känd identitet för samma fysiska person */
    TIDIGARE_IDENTITET_3            (243, 255),
    /** Tidigare känd identitet för samma fysiska person */
    TIDIGARE_IDENTITET_4            (255, 267),
    /** Tidigare känd identitet för samma fysiska person */
    TIDIGARE_IDENTITET_5            (267, 279),
    /** Fastighetsbeteckning */
    FASTIGHETSBETECKNING            (351, 391),
    /** Fastighetens X-koordinat enligt RT90. Två inledande nollor och åtta siffror för koordinaten. */
    FASTIGHET_KOORD_X               (391, 401),
    /** Fastighetens Y-koordinat enligt RT90. Två inledande nollor och åtta siffror för koordinaten. */
    FASTIGHET_KOORD_Y               (401, 411),

    /** Betjäningsområdeskod utgående från fastighet. */
    BETJ_OMRÅDE_FASTIGHET           (411, 419),
    /** Betjäningsområdeskod utgående från församlingskod. */
    BETJ_OMRÅDE_FÖRSAMLING          (419, 427),
    /** Beställaravdelning i första hand utgående från fastighet, i andra hand utgående från län/kommun/församling. */
    BESTÄLLARAVD_FAST_FÖRS          (427, 429),
    /** Betjäningsområdeskod i första hand utgående från fastighet, i andra hand utgående från län/kommun/församling. */
    BETJ_OMRÅDE_FAST_FÖRS           (427, 429),
    /** Betjäningsområdesnamn. */
    BETJ_OMRÅDE_NAMN                (437, 467),

    /** Vårdinrättning (från KOMBIKA) */
    INRÄTTNING                      (467, 472),
    /** Vårdinrättning (från KOMBIKA) */
    KLINIK                          (472, 475),
    /** Vårdinrättning (från KOMBIKA) */
    AVDELNING                       (475, 478),

    /** Vårdcentralsnamn. */
    NAMN_VÅRDCENTRAL                (478, 498),
    /** Klartext till beställaravdelning i pos 428-429 [alltså BESTÄLLARAVD_FAST_FÖRS här /red.] */
    NAMN_BESTÄLLARAVDELNING         (528, 548),
    /** Områdeskod för psyksektor. */
    PSYKSEKTOR_KOD                  (548, 556),
    /** Psyksektornamn. */
    PSYKSEKTOR_NAMN                 (556, 586),
    /** Basområde från fastighet eller i andra hand församling. */
    BASOMRÅDE_FAST_FÖRS             (586, 594),

    /** 7 om områdeskoderna i pos 428-594 hämtats från fastigheten, 8 om uppgifterna hämtats från församlingen. */
    KÄLLA_TILL_OMRÅDESKODER         (640, 641),

    /** Folkbokförings-C/O-adress. */
    FBF_CO_ADRESS                   (884, 919),
    /** Gårdsnamn, populärnamn etc */
    FBF_UTDEL_ADRESS_1              (919, 954),
    /** Gatuadress. */
    FBF_UTDEL_ADRESS_2              (954, 989),
    /** Postnummer i folkbokföringen. */
    FBF_POSTNUMMER                  (989, 994),
    /** Postort i folkbokföringen. */
    FBF_POSTORT                     (994, 1021),

    /** Särskild C/O-adress. */
    SÄRSKILD_CO_ADRESS              (1021, 1056),
    /** Gårdsnamn, populärnamn etc */
    SÄRSKILD_UTDEL_ADRESS_1         (1056, 1091),
    /** Gatuadress. */
    SÄRSKILD_UTDEL_ADRESS_2         (1091, 1126),
    /** Särskild adress postnummer. */
    SÄRSKILD_POSTNUMMER             (1126, 1131),
    /** Särskild adress postort. */
    SÄRSKILD_POSTORT                (1131, 1158),

    /** Utlandsadress rad 1. */
    UTLAND_ADRESS_RAD_1             (1158, 1193),
    /** Utlandsadress rad 2. */
    UTLAND_ADRESS_RAD_2             (1193, 1228),
    /** Utlandsadress rad 3. */
    UTLAND_ADRESS_RAD_3             (1228, 1263),
    /** Utlandsadress land. */
    UTLAND_ADRESS_LAND              (1263, 1298),

    /** Personnummer för relation 1. */
    RELATION_1_PNR                  (1298, 1310),
    /** Relationstyp för relation 1. */
    RELATION_1_TYP                  (1310, 1312),
    /** Personnummer för relation 2. */
    RELATION_2_PNR                  (1298, 1310),
    /** Relationstyp för relation 2. */
    RELATION_2_TYP                  (1310, 1312);


    /** The start index for a substring. */
    private final int startIndex;
    /** The end index for a substring. */
    private final int endIndex;

    PKNODPLUS (int startIndex, int endIndex) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    /* (non-Javadoc)
     * @see se.sll.rtjp.puadapter.extractor.SnodFieldsInterface#startIndex()
     */
    @Override
    public int startIndex() {
        return this.startIndex;
    }

    /* (non-Javadoc)
     * @see se.sll.rtjp.puadapter.extractor.SnodFieldsInterface#endIndex()
     */
    @Override
    public int endIndex() {
        return this.endIndex;
    }
}
