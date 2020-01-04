package cpup.mc.tweak.content.assistant

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

// these are required to be serializable
trait Wing {
	def texture: ResourceLocation
	def click(player: EntityPlayer)
}
