package cpup.mc.tweak

import cpup.mc.lib.CPupMod
import cpw.mods.fml.common.Mod
import cpup.mc.tweak.content.Content

@Mod(modid = Ref.modID, modLanguage = "scala", dependencies = "required-after:cpup-mc")
object CPupTweak extends CPupMod[Ref.type] {
	override def ref = Ref
	override final val content = Content
}