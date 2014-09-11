package cpup.mc.tweak.content.tools

import cpup.mc.tweak.content.BaseRecipe
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.world.World
import cpup.mc.tweak.CPupTweak
import cpup.mc.lib.util.serializing.SingletonSerialization

object Pickaxe {
	def mod = CPupTweak

	object Head extends Part.Pos with SingletonSerialization.TEntry { def id = s"${mod.ref.modID}:tools.pickaxe.head" }
	SingletonSerialization.register(Head)

	object Recipe extends BaseRecipe {
		override def getRecipeSize = 9

		override def getCraftingResult(inv: InventoryCrafting) = ???

		override def matches(inv: InventoryCrafting, world: World) = ???
	}
}