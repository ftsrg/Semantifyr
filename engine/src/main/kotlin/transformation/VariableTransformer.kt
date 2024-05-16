package hu.bme.mit.gamma.oxsts.engine.transformation

import hu.bme.mit.gamma.oxsts.engine.utils.asChainReferenceExpression
import hu.bme.mit.gamma.oxsts.engine.utils.copy
import hu.bme.mit.gamma.oxsts.engine.utils.dropLast
import hu.bme.mit.gamma.oxsts.engine.utils.referencedElement
import hu.bme.mit.gamma.oxsts.engine.utils.isFeatureTyped
import hu.bme.mit.gamma.oxsts.model.oxsts.Enum
import hu.bme.mit.gamma.oxsts.model.oxsts.EnumLiteral
import hu.bme.mit.gamma.oxsts.model.oxsts.Expression
import hu.bme.mit.gamma.oxsts.model.oxsts.Feature
import hu.bme.mit.gamma.oxsts.model.oxsts.Instance
import hu.bme.mit.gamma.oxsts.model.oxsts.ReferenceExpression
import hu.bme.mit.gamma.oxsts.model.oxsts.ReferenceTyping
import hu.bme.mit.gamma.oxsts.model.oxsts.Variable

class EnumMapping(
    val enum: Enum,
    val literalMapping: Map<Instance, EnumLiteral>
)

class VariableTransformer(
    private val instance: Instance
) {
    private val variableMap = mutableMapOf<Variable, Variable>()
    private val featureEnumMap = mutableMapOf<Feature, EnumMapping>()

    val allTransformedVariables
        get() = variableMap.values.toList()

    fun findTransformedVariable(oldVariable: Variable): Variable {
        return variableMap[oldVariable] ?:
            instance.parent?.variableTransformer?.findTransformedVariable(oldVariable) ?:
            error("$oldVariable could not be found in the containment hierarchy!")
    }

    fun transform(variable: Variable) {
        val newVariable = variable.copy()

        newVariable.name = "${instance.fullyQualifiedName}__${variable.name}"

        if (variable.isFeatureTyped) {
            val typing = variable.typing
            if (typing is ReferenceTyping) {
                newVariable.typing = typing.transform(variable.isOptional)
                if (variable.expression != null) {
                    newVariable.expression = transformExpression(OxstsFactory.createChainReferenceExpression(variable), variable.expression as ReferenceExpression, variable)
                }
            }
        }

        variableMap[variable] = newVariable
    }

    fun transformExpression(variableExpression: ReferenceExpression, expression: ReferenceExpression, typedVariable: Variable): Expression {
        require(typedVariable.isFeatureTyped) {
            "$typedVariable is not a feature typed variable!"
        }

        val typing = typedVariable.typing as ReferenceTyping

        val feature = typing.referencedElement as Feature

        val contextInstance = instance.expressionEvaluator.evaluateInstance(variableExpression.asChainReferenceExpression().dropLast(1))
        val featureHolder = contextInstance.expressionEvaluator.evaluateInstance(typing.reference.dropLast(1))
        val enumMapping = featureHolder.variableTransformer.featureEnumMap[feature] ?: error("There is no enum mapping for $feature in $featureHolder")

        val instance = instance.expressionEvaluator.evaluateInstance(expression)
        val literal = enumMapping.literalMapping[instance] ?: error("Referenced instance object $instance is not in referenced feature $feature in $contextInstance")

        return OxstsFactory.createChainReferenceExpression(literal)
    }

    private fun ReferenceTyping.transform(isOptional: Boolean): ReferenceTyping {
        if (referencedElement !is Feature) {
            return this
        }

        val enum = (referencedElement as Feature).transform(isOptional)

        return OxstsFactory.createReferenceTyping().also {
            it.reference = OxstsFactory.createChainReferenceExpression(enum)
        }
    }

    private fun Feature.transform(isOptional: Boolean): Enum {
        if (featureEnumMap.containsKey(this)) {
            return featureEnumMap[this]!!.enum
        }

        val enum = OxstsFactory.createEnum()
        val literalMapping = mutableMapOf<Instance, EnumLiteral>()

        featureEnumMap[this] = EnumMapping(enum, literalMapping)

        val instances = mutableListOf<Instance>()
        instances += instance.featureContainer[this]

        if (isOptional) {
            instances += NothingInstance
        }

        for (instance in instances) {
            val literal = OxstsFactory.createEnumLiteral(instance.enumLiteralName)
            literalMapping[instance] = literal
        }

        enum.name = "${instance.fullyQualifiedName}__${this.name}__type"
        enum.literals += literalMapping.values

        return enum
    }

    private val Instance.enumLiteralName: String
        get() = "${fullyQualifiedName}__literal"

}
