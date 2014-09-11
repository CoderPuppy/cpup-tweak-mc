package cpup.mc.tweak.content

import cpup.mc.lib.content.CPupContent
import cpup.mc.tweak.CPupTweak
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpup.mc.tweak.content.tools.Tools

object Content extends CPupContent[CPupTweak.type] {
	override def mod = CPupTweak

	override def preInit(e: FMLPreInitializationEvent) {
		super.preInit(e)

		Tools.register(this)
	}
}