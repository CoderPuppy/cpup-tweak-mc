package cpup.mc.tweak.content.tools

import cpup.mc.tweak.content.BaseItem
import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer

trait Tool {

}

object Tool {
	object Item extends BaseItem {
		name = "tool"

		override def canHarvestBlock(block: Block, stack: ItemStack) = true

		override def onBlockStartBreak(stack: ItemStack, x: Int, y: Int, z: Int, player: EntityPlayer) = {
			false
		}
	}
}