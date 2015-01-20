package cpup.mc.tweak.content.tools

import java.util

import com.google.common.collect.Multimap
import cpup.mc.lib.content.CPupItem
import cpup.mc.lib.util.ItemUtil
import cpup.mc.lib.util.serializing.SerializationRegistry
import cpup.mc.tweak.content.BaseItem
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import scala.reflect.runtime.universe.TypeTag

import scala.collection.{JavaConversions, mutable}

trait Tool extends Stats.Modification {
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
	def getStat[T](stack: ItemStack, name: String, orig: T)(implicit typeTag: TypeTag[T]) = SerializationRegistry.read[Tool](stack) match {
		case tool: Tool =>
			tool.modify[T](name, orig)

		case null => orig
	}

	object Item extends BaseItem {
		name = "tool"

		final val toolClasses = Set("pickaxe", "axe", "shovel")

//		override def canHarvestBlock(block: Block, stack: ItemStack) = true

		override def getHarvestLevel(stack: ItemStack, toolClass: String): Int = getStat[Int](stack, s"harvest-level:$toolClass", 0)

		override def onBlockStartBreak(stack: ItemStack, x: Int, y: Int, z: Int, player: EntityPlayer) = {
			false
		}

		override def getToolClasses(stack: ItemStack): util.Set[String] = {
			val tool = SerializationRegistry.read[Tool](stack)
			mod.logger.info("tool classes: {}", toolClasses.filter((name) =>
				tool.modify[Int](s"harvest-level:$name", -1) >= 0
			))
			JavaConversions.setAsJavaSet(toolClasses.filter((name) =>
				tool.modify[Int](s"harvest-level:$name", -1) >= 0
			))
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

		override def getAttributeModifiers(stack: ItemStack): Multimap[_, _] = {
			val multimap = super.getAttributeModifiers(stack).asInstanceOf[Multimap[String, AttributeModifier]]
			multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName, new AttributeModifier(CPupItem.itemUUID, "Weapon modifier",
				getStat[Int](stack, "damage", 0), 0))
			return multimap
		}
	}
}