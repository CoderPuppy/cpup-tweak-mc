package cpup.mc.tweak.content.tools

import cpup.mc.lib.content.ContentRegistrar
import cpup.mc.tweak.CPupTweak
import scala.reflect.runtime.universe.TypeTag

object Tools {
	def register(registrar: ContentRegistrar[CPupTweak.type]) {
		VanillaMaterials
		GenericParts
		Pickaxe
		Part

		registrar.registerItem(Tool.Item)
		registrar.registerItem(Part.Item)

		registrar.registerRecipe(Pickaxe.Recipe)

		Part.register(VanillaMaterials.leather, GenericParts.binding, Stats.Modification.NOOP)
		Part.register(VanillaMaterials.string, GenericParts.binding, Stats.Modification.NOOP)
		Part.register(VanillaMaterials.iron, Pickaxe.head,
			Stats.Modification.NVal("harvest-level:pickaxe", 2) +
			Stats.Modification.NFun[Int]("damage", _ + 3)
		)
		Part.register(VanillaMaterials.stone, Pickaxe.head,
			Stats.Modification.NVal("harvest-level:pickaxe", 1) +
			Stats.Modification.NFun[Int]("damage", _ + 2)
		)
		Part.register(VanillaMaterials.wood, GenericParts.handle,
			Stats.Modification.NFun[Int]("damage", _ + 1)
		)

		// Wrap the handle in leather and use half the amount of food (theoretically)
		Part.register(GenericModifications.wrapping(VanillaMaterials.leather), None, Some(GenericParts.handle), new Stats.Modification {
			override def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]): T = (name match {
				case "exhaustion" if orig.isInstanceOf[Double] =>
					orig.asInstanceOf[Double] * 0.5

				case _ => orig
			}).asInstanceOf[T]
		})
	}
}