package cpup.mc.tweak.content.tools

import cpup.mc.lib.content.ContentRegistrar
import cpup.mc.tweak.CPupTweak

object Tools {
	def register(registrar: ContentRegistrar[CPupTweak.type]) {
		registrar.registerItem(Tool.Item)
		registrar.registerItem(Part.Item)

		registrar.registerRecipe(Pickaxe.Recipe)
	}
}