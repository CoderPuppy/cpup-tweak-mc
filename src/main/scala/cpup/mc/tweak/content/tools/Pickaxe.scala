package cpup.mc.tweak.content.tools

import cpup.mc.tweak.content.BaseRecipe
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.world.World
import cpup.mc.tweak.CPupTweak
import cpup.mc.lib.util.serializing.{Serialized, SerializableType, SerializationRegistry, SingletonSerialization}
import net.minecraft.item.ItemStack
import net.minecraft.init.Items
import cpup.mc.lib.util.ItemUtil
import net.minecraft.nbt.NBTTagCompound

case class Pickaxe(head: Part, binding: Part, handle: Part) extends Tool {
	override def parts = List(head, binding, handle)
}

object Pickaxe {
	def mod = CPupTweak

	case object Head extends Part.Shape
	SingletonSerialization.register(Head, s"${mod.ref.modID}:tools.pickaxe.head")

	object Type extends SerializableType[Pickaxe, NBTTagCompound] {
		def mod = CPupTweak
		override def id = s"${mod.ref.modID}:tools.pickaxe"
		override def cla = classOf[Pickaxe]

		override def nbtClass = classOf[NBTTagCompound]
		override def writeToNBT(pickaxe: Pickaxe) = {
			val nbt = new NBTTagCompound
			nbt.setTag("head", Serialized(pickaxe.head))
			nbt.setTag("binding", Serialized(pickaxe.binding))
			nbt.setTag("handle", Serialized(pickaxe.handle))
			nbt
		}
		override def readFromNBT(nbt: NBTTagCompound) = (
			Serialized.un[Part](nbt.getCompoundTag("head")),
			Serialized.un[Part](nbt.getCompoundTag("binding")),
			Serialized.un[Part](nbt.getCompoundTag("handle"))
		) match {
			case (head: Part, binding: Part, handle: Part) => Pickaxe(head, binding, handle)
			case r =>
				mod.logger.warn("got {} when parsing a pickaxe", r)
				null
		}
	}
	SerializationRegistry.registerType(Type)

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
				}

				if(foundHead && foundBinding && foundHandle) foundPattern = true
				else if(foundHead || foundBinding || foundHandle) return false // cancel if it isn't the whole pattern
			}
			foundPattern
		}
	}
}