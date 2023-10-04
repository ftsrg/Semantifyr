/*
 * generated by Xtext 2.31.0
 */
package hu.bme.mit.gamma.oxsts.lang.ide;

import com.google.inject.Guice;
import com.google.inject.Injector;
import hu.bme.mit.gamma.oxsts.lang.OxstsRuntimeModule;
import hu.bme.mit.gamma.oxsts.lang.OxstsStandaloneSetup;
import org.eclipse.xtext.util.Modules2;

/**
 * Initialization support for running Xtext languages as language servers.
 */
public class OxstsIdeSetup extends OxstsStandaloneSetup {

	@Override
	public Injector createInjector() {
		return Guice.createInjector(Modules2.mixin(new OxstsRuntimeModule(), new OxstsIdeModule()));
	}
	
}
