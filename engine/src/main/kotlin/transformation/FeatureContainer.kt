package hu.bme.mit.gamma.oxsts.engine.transformation

import hu.bme.mit.gamma.oxsts.engine.utils.allFeatures
import hu.bme.mit.gamma.oxsts.engine.utils.allSubsets
import hu.bme.mit.gamma.oxsts.model.oxsts.Feature
import hu.bme.mit.gamma.oxsts.model.oxsts.Instance

class FeatureContainer(
    private val holder: Instance
) {

    private val associationMap = holder.type.allFeatures.associateWith {
        OxstsFactory.createAssociation(it).also {
            holder.associations += it
        }
    }

    fun place(feature: Feature, instance: Instance) {
        val association = associationMap[feature] ?: error("Feature $feature can not be found on instance $holder")

        association.instances += instance

        for (subsetFeature in feature.allSubsets) {
            place(subsetFeature, instance)
        }
    }

    fun place(feature: Feature, instances: Collection<Instance>) {
        for (instance in instances) {
            place(feature, instance)
        }
    }

    operator fun get(feature: Feature): Set<Instance> {
        return associationMap[feature]?.instances?.toSet() ?: error("")
    }

}

