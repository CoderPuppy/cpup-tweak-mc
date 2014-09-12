package cpup.mc.tweak.content.tools

import cpup.mc.lib.util.serializing.SingletonSerialization
import cpup.mc.tweak.CPupTweak

object VanillaMaterials {
	def mod = CPupTweak

	object Iron extends Part.Material
	SingletonSerialization.register(Iron, s"${mod.ref.modID}:tools.vanilla.iron")

	object Leather extends Part.Material
	SingletonSerialization.register(Leather, s"${mod.ref.modID}:tools.vanilla.leather")

	object String extends Part.Material
	SingletonSerialization.register(String, s"${mod.ref.modID}:tools.vanilla.string")
}