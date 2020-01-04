package cpup.mc.tweak.content.assistant

import cpup.mc.lib.util.{ItemUtil, Direction}
import cpup.mc.tweak.content.BaseItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.world.World

object Item extends BaseItem {
	setName("assistant")

	override def onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
		super.onItemRightClick(stack, world, player)
	}

	override def onItemUse(stack: ItemStack, player: EntityPlayer, world: World, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		val dir = Direction.fromSide(side)
		val data = new Data
		data.readFromNBT(ItemUtil.compound(stack))
		val entity = new Entity(world, x + dir.x, y + dir.y, z + dir.z, player.getGameProfile, data)
		world.spawnEntityInWorld(entity)
		true
	}
}
