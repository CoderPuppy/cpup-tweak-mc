package cpup.mc.tweak.content.assistant

import cpup.mc.lib.util.{ItemUtil, Direction}
import cpup.mc.tweak.content.BaseItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object Item extends BaseItem {
	setName("assistant")

	def parseData(stack: ItemStack, player: EntityPlayer) = {
		val data = new Data(player)
		data.readFromNBT(stack)
		data
	}

	override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
		super.onItemRightClick(stack, world, player)
	}

	override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		val dir = Direction.fromSide(side)
		val data = parseData(stack, player)
		val entity = new Entity(world, x + dir.x, y + dir.y, z + dir.z, data)
		world.spawnEntityInWorld(entity)
		true
	}
}
