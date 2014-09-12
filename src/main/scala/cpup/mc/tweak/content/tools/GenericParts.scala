package cpup.mc.tweak.content.tools

import cpup.mc.tweak.CPupTweak
import cpup.mc.lib.util.serializing.SingletonSerialization

object GenericParts {
	def mod = CPupTweak

	case object Binding extends Part.Shape
	SingletonSerialization.register(Binding, s"${mod.ref.modID}:tools.generic.binding")

	case object Handle extends Part.Shape
	SingletonSerialization.register(Handle, s"${mod.ref.modID}:tools.generic.handle")
}