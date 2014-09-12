package cpup.mc.tweak.content.tools

import cpup.mc.tweak.CPupTweak
import cpup.mc.lib.util.serializing.SingletonSerialization

object GenericParts {
	def mod = CPupTweak

	case object Binding extends Part.Shape with SingletonSerialization.TEntry { def id = s"${mod.ref.modID}:tools.generic.binding" }
	SingletonSerialization.register(Binding)

	case object Handle extends Part.Shape with SingletonSerialization.TEntry { def id = s"${mod.ref.modID}:tools.generic.handle" }
	SingletonSerialization.register(Handle)
}