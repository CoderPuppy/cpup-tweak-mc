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

trait Tool {
	def parts: Seq[Part]
}

object Tool {
	object Item extends BaseItem {
		name = "tool"

		override def canHarvestBlock(block: Block, stack: ItemStack) = true

		override def onBlockStartBreak(stack: ItemStack, x:  'Int, y: Int, z: Int, player: EntityPlayer) = {
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