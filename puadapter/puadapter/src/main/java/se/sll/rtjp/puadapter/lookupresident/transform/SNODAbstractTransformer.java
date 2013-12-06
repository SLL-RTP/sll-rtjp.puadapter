package se.sll.rtjp.puadapter.lookupresident.transform;

import org.mule.transformer.AbstractTransformer;

import riv.population.residentmaster.v1.AvregistreringTYPE;
import riv.population.residentmaster.v1.AvregistreringsorsakKodKomplettTYPE;
import riv.population.residentmaster.v1.CivilstandKodTYPE;
import se.sll.rtjp.puadapter.extractor.ResidentExtractor;
import se.sll.rtjp.puadapter.extractor.SnodFieldsInterface;

public abstract class SNODAbstractTransformer extends AbstractTransformer {
    
    private ResidentExtractor re;
    
    void setResidentExtractor(ResidentExtractor re) {
        this.re = re;
    }
    
    ResidentExtractor getResidentExtractor() {
        return this.re;
    }
    
    String getValue(SnodFieldsInterface field) {
        return re.getField(field);
    }
    
    String getValueOrNull(SnodFieldsInterface field) {
        return "".equals(re.getField(field)) ? null : re.getField(field);
    }
    
    CivilstandKodTYPE getCivilstandKodTYPEFromKod(String civstandKod) {
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

    AvregistreringTYPE getAvregistreringTypeFromSnodResponse(String avrDatum, String avrOrsak) {
        AvregistreringTYPE avreg = new AvregistreringTYPE();
        avreg.setAvregistreringsorsakKodKomplett(getAvregistreringsorsakKodFromAvrOrsak(avrOrsak));
        avreg.setAvregistreringsdatum(avrDatum);

        return avreg;
    }

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
