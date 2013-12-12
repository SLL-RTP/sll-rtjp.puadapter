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
package se.sll.rtjp.puadapter.lookupresident.exception;

import org.mule.api.ExceptionPayload;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageTransformer;

/**
 * Transformer that creates a SOAP Exception string from a flow exception.
 */
public class SOAPFaultExceptionTransformer extends AbstractMessageTransformer {

        public SOAPFaultExceptionTransformer() {
            setName("SOAPFaultExceptionTransformer");
        }

        @Override
        public Object transformMessage(MuleMessage message, String outputEncoding)
                throws TransformerException {
            ExceptionPayload exceptionMessage = message.getExceptionPayload();
            String outputMessage = "<soap:Fault xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">        "
                    + " <faultcode>soap:Server</faultcode> "
                    + "<faultstring>"
                    + "An unexpected error has occured. Please contact your service desk and quote this error \""  + exceptionMessage.getException() +  "\""+ "</faultstring>      " + "</soap:Fault>";
            return outputMessage;
        }
}

