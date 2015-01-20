package cpup.mc.tweak.content.tools

import cpup.mc.tweak.content.BaseItem
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import java.util
import cpup.mc.lib.util.serializing.{SerializableType, SerializationRegistry}
import cpup.mc.lib.util.ItemUtil
import net.minecraft.nbt.NBTTagCompound
import scala.collection.mutable

trait Tool extends Stats.Modification {
	def parts: Seq[Part]

	def stats: Stats

	def damage: Int
	def damage(amt: Int): Tool
	def repair(amt: Int): Tool

	def modify[T](name: String, orig: T)(implicit manifest: Manifest[T]) = {
		var curr = orig
		for(part <- parts) {
			curr = part.modify(name, curr)
		}
		curr
	}
}

object Tool {
	def getStat[T](stack: ItemStack, name: String, orig: T)(implicit manifest: Manifest[T]) = SerializationRegistry.read[Tool](stack) match {
		case tool: Tool =>
			tool.modify[T](name, orig)

		case null => orig
	}

	object Item extends BaseItem {
		name = "tool"

//		override def canHarvestBlock(block: Block, stack: ItemStack) = true

		override def getHarvestLevel(stack: ItemStack, toolClass: String): Int = getStat[Int](stack, s"harvest-level:$toolClass", 0)

		override def onBlockStartBreak(stack: ItemStack, x: Int, y: Int, z: Int, player: EntityPlayer) = {
			false
		}

		override def addLore(stack: ItemStack, player: EntityPlayer, lore: mutable.Buffer[String], advanced: Boolean) {
			super.addLore(stack, player, lore, advanced)
			if(advanced) {
				lore += (SerializationRegistry.readFromNBT[Tool](ItemUtil.compound(stack)) match {
					case tool: Tool => tool.toString
					case null => "Not a Tool: null"
				})
			}
		}
	}
}