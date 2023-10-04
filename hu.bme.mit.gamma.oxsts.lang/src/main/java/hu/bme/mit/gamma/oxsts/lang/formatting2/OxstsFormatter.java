/*
 * generated by Xtext 2.31.0
 */
package hu.bme.mit.gamma.oxsts.lang.formatting2;

import hu.bme.mit.gamma.oxsts.model.oxsts.Expression;
import hu.bme.mit.gamma.oxsts.model.oxsts.Feature;
import hu.bme.mit.gamma.oxsts.model.oxsts.Transition;
import hu.bme.mit.gamma.oxsts.model.oxsts.Type;
import hu.bme.mit.gamma.oxsts.model.oxsts.Variable;
import org.eclipse.xtext.formatting2.AbstractJavaFormatter;
import org.eclipse.xtext.formatting2.IFormattableDocument;

public class OxstsFormatter extends AbstractJavaFormatter {

	protected void format(hu.bme.mit.gamma.oxsts.model.oxsts.Package _package, IFormattableDocument doc) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		for (Type type : _package.getTypes()) {
			doc.format(type);
		}
	}

	protected void format(Type type, IFormattableDocument doc) {
		// TODO: format HiddenRegions around keywords, attributes, cross references, etc. 
		for (Variable variable : type.getVariables()) {
			doc.format(variable);
		}
		for (Feature feature : type.getFeatures()) {
			doc.format(feature);
		}
		for (Transition transition : type.getInitTransition()) {
			doc.format(transition);
		}
		for (Transition transition : type.getTransitions()) {
			doc.format(transition);
		}
		for (Transition transition : type.getMainTransition()) {
			doc.format(transition);
		}
		for (Transition transition : type.getHavocTransition()) {
			doc.format(transition);
		}
		for (Expression expression : type.getInvariant()) {
			doc.format(expression);
		}
	}
	
	// TODO: implement for Transition, SequenceOperation, HavocOperation, ChoiceOperation, AssumptionOperation, InlineCall, AssignmentOperation, Variable, OrOperator, AndOperator, PlusOperator, MinusOperator, EqualityOperator, InequalityOperator, NotOperator, ChainReferenceExpression
}
