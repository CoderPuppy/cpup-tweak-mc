package cpup.mc.tweak.content.tools

import cpup.mc.tweak.CPupTweak

object VanillaMaterials {
	def mod = CPupTweak

	final val iron = Part.Material("metal", Some("iron"))
	final val gold = Part.Material("metal", Some("gold"))
	final val leather = Part.Material("fabric", Some("leather"))
	final val string = Part.Material("cord", Some("string"))
	final val vine = Part.Material("cord", Some("vine"))
	final val stone = Part.Material("stone")
	final val wood = Part.Material("wood")
}