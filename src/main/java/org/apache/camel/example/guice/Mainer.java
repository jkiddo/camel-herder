package org.apache.camel.example.guice;

import org.apache.camel.guice.ext.RegistryModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Mainer {

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new RegistryModule(),
				/* new CamelModuleWithRouteTypes(PDQRouteBuilder.class) */ new CamelGuiceApplicationModule());
		// if required you can lookup the CamelContext
//		CamelContext camelContext = injector.getInstance(CamelContext.class);
	}
}
