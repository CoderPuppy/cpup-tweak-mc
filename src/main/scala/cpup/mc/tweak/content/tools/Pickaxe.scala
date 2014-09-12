package cpup.mc.tweak.content.tools

import cpup.mc.tweak.content.BaseRecipe
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.world.World
import cpup.mc.tweak.CPupTweak
import cpup.mc.lib.util.serializing.{SerializationRegistry, SingletonSerialization}
import net.minecraft.item.ItemStack
import net.minecraft.init.Items
import cpup.mc.lib.util.ItemUtil

case class Pickaxe(head: Part, binding: Part, handle: Part) extends Tool {
	override def parts = List(head, binding, handle)
}

object Pickaxe {
	def mod = CPupTweak

	case object Head extends Part.Shape with SingletonSerialization.TEntry { def id = s"${mod.ref.modID}:tools.pickaxe.head" }
	SingletonSerialization.register(Head)

	object Recipe extends BaseRecipe {
//		def width = 1
//		def height = 3

		override def getRecipeSize = 3

		override def getCraftingResult(inv: InventoryCrafting) = new ItemStack(Items.diamond_pickaxe)

		override def matches(inv: InventoryCrafting, world: World): Boolean = {
			var foundPattern = false
			for(x <- 0 to 2) {
				for(y <- 0 to 2) {
					val stack = inv.getStackInRowAndColumn(x, y)
					var foundHead = false
					var foundBinding = false
					var foundHandle = false
					if(stack != null) {
						// cancel if we've already found the pattern (and there's another item in the crafting grid)
						if(foundPattern) return false

						val part = SerializationRegistry.readFromNBT[Part](ItemUtil.compound(stack))
						// cancel if there's a non-part item in the crafting grid
						// TODO: handle sticks
						if(part == null) return false
						y match {
							case 0 if part.shape == Pickaxe.Head => foundHead = true
							case 1 if part.shape == GenericParts.Binding => foundBinding = true
							case 2 if part.shape == GenericParts.Handle => foundHandle = true
							case _ => return false
						}
					}

					if(foundHead && foundBinding && foundHandle) foundPattern = true
					else if(foundHead || foundBinding || foundHandle) return false // cancel if it isn't the whole pattern
				}
			}
			foundPattern
		}
	}
}