package cpup.mc.tweak

import cpup.mc.lib.CPupMod
import cpup.mc.tweak.content.Content
import cpup.mc.tweak.content.assistant.Assistant
import cpw.mods.fml.common.Mod

@Mod(modid = Ref.modID, modLanguage = "scala", dependencies = "required-after:cpup-mc")
object CPupTweak extends CPupMod[Ref.type] {
	override def ref = Ref
	override final val content = Content

	loadModule[Assistant](classOf[Assistant])
}