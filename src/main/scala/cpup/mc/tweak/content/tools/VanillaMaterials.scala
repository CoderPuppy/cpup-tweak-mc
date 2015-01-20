package cpup.mc.tweak.content.tools

import cpup.mc.lib.util.serializing.SingletonSerialization
import cpup.mc.tweak.CPupTweak

object VanillaMaterials {
	def mod = CPupTweak

	final val iron = Part.Material("metal.iron")
	final val gold = Part.Material("metal.gold")
	final val leather = Part.Material("fabric.leather")
	final val string = Part.Material("cord.string")
	final val vine = Part.Material("cord.vine")
	final val stone = Part.Material("stone")
	final val wood = Part.Material("wood")
}