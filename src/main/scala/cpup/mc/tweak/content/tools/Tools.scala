package cpup.mc.tweak.content.tools

import cpup.mc.lib.content.ContentRegistrar
import cpup.mc.tweak.CPupTweak

object Tools {
	def register(registrar: ContentRegistrar[CPupTweak.type]) {
		VanillaMaterials
		GenericParts
		Pickaxe

		registrar.registerItem(Tool.Item)
		registrar.registerItem(Part.Item)

		registrar.registerRecipe(Pickaxe.Recipe)

		Part.register(VanillaMaterials.Leather, GenericParts.Binding, null)
		Part.register(VanillaMaterials.String, GenericParts.Binding, null)
		Part.register(VanillaMaterials.Iron, Pickaxe.Head, null)
	}
}