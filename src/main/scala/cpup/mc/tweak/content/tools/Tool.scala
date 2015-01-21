package cpup.mc.tweak.content.tools

import java.util

import com.google.common.collect.Multimap
import cpup.mc.lib.content.CPupItem
import cpup.mc.lib.util.ItemUtil
import cpup.mc.lib.util.serializing.{Serialized, SerializationRegistry}
import cpup.mc.tweak.content.BaseItem
import net.minecraft.block.Block
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import scala.reflect.runtime.universe.TypeTag

import scala.collection.{JavaConversions, mutable}

trait Tool extends Stats.Modifier {
	def parts: Seq[Part]

	def stats: Stats

	def damage: Int
	def damage(amt: Int): Tool
	def repair(amt: Int): Tool

	def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]) = {
		var curr = orig
		for(part <- parts) {
			curr = part.modify(name, curr)
		}
		curr
	}
}

object Tool {
	def getModifier(stack: ItemStack) = Serialized.un[Tool](stack).getOrElse(Stats.Modifier.NOOP)

	object Item extends BaseItem {
		name = "tool"

		final val toolClasses = Set("pickaxe", "axe", "shovel")

//		override def canHarvestBlock(block: Block, stack: ItemStack) = true

		override def getHarvestLevel(stack: ItemStack, toolClass: String): Int = getModifier(stack).modify[Int](s"harvest-level:$toolClass", -1)

		override def getDigSpeed(stack: ItemStack, block: Block, meta: Int): Float = {
			val modifier = getModifier(stack)
			val toolClass = block.getHarvestTool(meta)
			if(modifier.modify[Int](s"harvest-level:$toolClass", -1) >= block.getHarvestLevel(meta)) {
				modifier.modify[Float](s"dig-speed:$toolClass", 1)
			} else {
				super.getDigSpeed(stack, block, meta)
			}
		}

		override def onBlockStartBreak(stack: ItemStack, x: Int, y: Int, z: Int, player: EntityPlayer) = {
			false
		}

		override def getToolClasses(stack: ItemStack): util.Set[String] = Serialized.un[Tool](stack).map(tool => {
			JavaConversions.setAsJavaSet(toolClasses.filter(name => {
				tool.modify[Int](s"harvest-level:$name", -1) >= 0
			}))
		}).getOrElse(new util.HashSet[String])

		override def addLore(stack: ItemStack, player: EntityPlayer, lore: mutable.Buffer[String], advanced: Boolean) {
			super.addLore(stack, player, lore, advanced)
			if(advanced) {
				lore += (Serialized.un[Tool](stack) match {
					case Some(tool) => tool.toString
					case None => "Not a Tool"
				})
			}
		}

		override def getAttributeModifiers(stack: ItemStack): Multimap[_, _] = {
			val multimap = super.getAttributeModifiers(stack).asInstanceOf[Multimap[String, AttributeModifier]]
			multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName, new AttributeModifier(CPupItem.itemUUID, "Weapon modifier",
				getModifier(stack).modify[Int]("damage", 0), 0))
			return multimap
		}
	}
}