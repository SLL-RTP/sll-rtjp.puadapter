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

import se.sll.rtjp.puadapter.extractor.fields.SnodFieldsInterface;

/**
 * Helperclass that performs substring exctractions from SNOD responses
 * using positions from a {@link SnodFieldsInterface}.
 */
public class ResidentExtractor {

    /** The point at which the proper data starts in a SNOD response string. */
    private static final int SNOD_DATA_START_POSITION = 9;

    /** The SNOD response string to extract from. */
    private String snodResponse;

    public ResidentExtractor(String snodResponse) {
        this.snodResponse = snodResponse.substring(SNOD_DATA_START_POSITION);
    }

    /**
     * Fetches the value for a specific {@link SnodFieldsInterface} from
     * the stored snodResponse.
     * @param field The {@link SnodFieldsInterface} to extract.
     * @return The extracted value for the desired field.
     */
    public String getField(SnodFieldsInterface field) {
        return snodResponse.substring(field.startIndex(), field.endIndex()).trim();
    }
}
