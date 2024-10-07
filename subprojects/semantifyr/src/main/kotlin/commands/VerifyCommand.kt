package hu.bme.mit.semantifyr.oxsts.semantifyr.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.types.file
import hu.bme.mit.semantifyr.oxsts.semantifyr.reader.OxstsReader
import hu.bme.mit.semantifyr.oxsts.semantifyr.reader.prepareOxsts
import hu.bme.mit.semantifyr.oxsts.semantifyr.serialization.Serializer
import hu.bme.mit.semantifyr.oxsts.semantifyr.transformation.XstsTransformer
import java.io.File

class VerifyCommand : BaseVerifyCommand("verify") {

    val libraryDirectory by argument().file(mustExist = true, canBeDir = true)
    val targetName by argument()

    override fun run() {
        prepareOxsts()

        val reader = OxstsReader(libraryDirectory.path)
        reader.readModel(model.path)

        val transformer = XstsTransformer(reader)

        val xsts = transformer.transform(targetName)
        val xstsString = Serializer.serialize(xsts)

        val output = model.path.replace(".oxsts", ".xsts")

        File(output).writeText(xstsString)

        runVerification(output)
    }

}
