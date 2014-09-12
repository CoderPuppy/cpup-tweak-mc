package cpup.mc.tweak.content.tools

import cpup.mc.lib.util.serializing.SingletonSerialization
import cpup.mc.tweak.CPupTweak

object VanillaMaterials {
	def mod = CPupTweak

	object Iron extends Part.Material with SingletonSerialization.TEntry {
		def id = s"${mod.ref.modID}:tools.vanilla.iron"
		def canMake(shape: Part.Shape) = shape match {
			case Pickaxe.Head => true
			case _ => false
		}
	}
	SingletonSerialization.register(Iron)

	object Leather extends Part.Material with SingletonSerialization.TEntry {
		def id = s"${mod.ref.modID}:tools.vanilla.leather"
		def canMake(shape: Part.Shape) = shape match {
			case GenericParts.Binding => true
			case _ => false
		}
	}
	SingletonSerialization.register(Leather)

	object String extends Part.Material with SingletonSerialization.TEntry {
		def id = s"${mod.ref.modID}:tools.vanilla.string"
		def canMake(shape: Part.Shape) = shape match {
			case GenericParts.Binding => true
			case _ => false
		}
	}
	SingletonSerialization.register(String)
}