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

import org.apache.camel.component.hl7.HL7MLLPCodec;
import org.apache.camel.component.hl7.HL7MLLPNettyDecoderFactory;
import org.apache.camel.component.hl7.HL7MLLPNettyEncoderFactory;
import org.apache.camel.guice.CamelModuleWithMatchingRoutes;
import org.apache.camel.guice.ext.Binds;
import org.apache.mina.filter.codec.ProtocolCodecFactory;

import io.netty.channel.ChannelHandler;

/**
 * Configures the CamelContext, RouteBuilder, Component and Endpoint instances
 * using Guice
 *
 */
public class CamelGuiceApplicationModule extends CamelModuleWithMatchingRoutes {

	@Override
	protected void configure() {
		super.configure();

		// netty4
		bind(ChannelHandler.class).annotatedWith(Binds.camelBind("hl7decoder")).to(HL7MLLPNettyDecoderFactory.class);
		bind(ChannelHandler.class).annotatedWith(Binds.camelBind("hl7encoder")).to(HL7MLLPNettyEncoderFactory.class);

		// mina2
		bind(ProtocolCodecFactory.class).annotatedWith(Binds.camelBind("hl7codec")).to(HL7MLLPCodec.class)
				.asEagerSingleton();
		bind(PDQRouteBuilder.class);
	}
}
