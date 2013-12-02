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

import static org.junit.Assert.*;

import org.junit.Test;

public class ResidentExtractorTest {

    @Test
    public void testGetField() {
        String snodResponse = "0\n0\n1327\n13270000197912309296197912301979123092961Apptest, Testperson 3               TESTGATAN 3                31233TESTSTADEN                          00000000000000000000    1979018601200340  200801162008011600000000                                                                                                                                            RIBBAN 5                                00658536100016345710118606  118699  01118606  Centrala Liding?              11338648M05Centrala Liding?                                  Norr                1140    Liding? psykiatriska sektor   3411520                                               7                                                               Testperson 3                                                                                                            Apptest                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   000000000000  000000000000  _";
        ResidentExtractor re = new ResidentExtractor(snodResponse);
        assertEquals("Aviseringsnamn", "Apptest, Testperson 3", re.getField(PKNODPLUS.AVISERINGSNAMN));
        assertEquals("Förnamn", "Stephen", re.getField(PKNODPLUS.FÖRNAMN));
    }

}
