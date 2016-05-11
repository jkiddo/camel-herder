/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.example.guice;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.openehealth.ipf.platform.camel.ihe.mllp.PixPdqCamelValidators;

import ca.uhn.hl7v2.hoh.api.IAuthorizationClientCallback;
import ca.uhn.hl7v2.hoh.api.IReceivable;
import ca.uhn.hl7v2.hoh.api.ISendable;
import ca.uhn.hl7v2.hoh.api.MessageMetadataKeys;
import ca.uhn.hl7v2.hoh.auth.SingleCredentialClientCallback;
import ca.uhn.hl7v2.hoh.hapi.api.MessageSendable;
import ca.uhn.hl7v2.hoh.hapi.client.HohClientSimple;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

/**
 * @version
 */
public class PDQRouteBuilder extends RouteBuilder {

	public PDQRouteBuilder() {
		client.setAuthorizationCallback(authCalback);
	}

	String host = "localhost";
	int port = 8080;
	String uri = "/AppContext";

	Parser parser = PipeParser.getInstanceWithNoValidation();
	HohClientSimple client = new HohClientSimple(host, port, uri, parser);
	IAuthorizationClientCallback authCalback = new SingleCredentialClientCallback("ausername", "somepassword");

	public void configure() {

		from("pdq-iti21://0.0.0.0:8777?audit=false&secure=false&sync=true")
				.process(PixPdqCamelValidators.itiValidator()).process(new Processor() {

					@Override
					public void process(Exchange exchange) throws Exception {

						Message mm = exchange.getIn().getBody(Message.class);
						exchange.getOut().setBody(mm.generateACK().toString());
					}
				}).process(PixPdqCamelValidators.itiValidator());

		from("netty4:tcp://localhost:2575?sync=true&encoder=#hl7encoder&decoder=#hl7decoder").process(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				ISendable<Message> sendable = new MessageSendable(exchange.getIn().getBody(Message.class));

				try {
					IReceivable<Message> receivable = client.sendAndReceiveMessage(sendable);
					Message message = receivable.getMessage();
					String remoteHostIp = (String) receivable.getMetadata()
							.get(MessageMetadataKeys.REMOTE_HOST_ADDRESS);
				} catch (Exception e) {
					// exchange.setException(e);
					exchange.getOut().setBody(sendable.getMessage().generateACK(), Message.class);
				}
			}
		}).log("Message received for transformation").onException(Exception.class).onExceptionOccurred(new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				// TODO Auto-generated method stub
				System.out.println("ldskjfldksj");
			}
		});
	}
}