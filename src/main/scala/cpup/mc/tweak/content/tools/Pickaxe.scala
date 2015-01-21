package cpup.mc.tweak.content.tools

import cpup.mc.lib.content.CPupRecipe
import cpup.mc.lib.util.ItemUtil
import cpup.mc.lib.util.serializing.{SerializableType, SerializationRegistry, Serialized}
import cpup.mc.tweak.CPupTweak
import cpup.mc.tweak.content.BaseRecipe
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

case class Pickaxe(damage: Int, head: Part, binding: Part, handle: Part) extends Tool {
	override def parts = List(head, binding, handle)

	def damage(amt: Int) = Pickaxe(damage + amt, head, binding, handle)
	def repair(amt: Int) = Pickaxe(damage - amt, head, binding, handle)
}

object Pickaxe {
	def mod = CPupTweak

	final val head = Part.Shape("pickaxe.head")

	object Type extends SerializableType[Pickaxe, NBTTagCompound] {
		def mod = CPupTweak
		override def id = s"${mod.ref.modID}:tools.pickaxe"
		override def cla = classOf[Pickaxe]

		override def nbtClass = classOf[NBTTagCompound]
		override def writeToNBT(pickaxe: Pickaxe) = {
			val nbt = new NBTTagCompound
			nbt.setInteger("damage", pickaxe.damage)
			nbt.setTag("head", Serialized(pickaxe.head))
			nbt.setTag("binding", Serialized(pickaxe.binding))
			nbt.setTag("handle", Serialized(pickaxe.handle))
			nbt
		}
		override def readFromNBT(nbt: NBTTagCompound) = (
			Some(nbt.getInteger("damage")),
			Serialized.un[Part](nbt.getCompoundTag("head")),
			Serialized.un[Part](nbt.getCompoundTag("binding")),
			Serialized.un[Part](nbt.getCompoundTag("handle"))
		) match {
			case (Some(damage), Some(head), Some(binding), Some(handle)) => Pickaxe(damage, head, binding, handle)
			case r =>
				mod.logger.warn("got {} when parsing a pickaxe", r)
				null
		}
	}
	SerializationRegistry.registerType(Type)

	object Recipe extends BaseRecipe with CPupRecipe.Shaped[Pickaxe] {
		def width = 1
		def height = 3

		override def parse(inv: InventoryCrafting, ox: Int, oy: Int, data: Array[Array[Option[ItemStack]]]): Option[Pickaxe] = (
			data(0)(0).flatMap(Serialized.un[Part]),
			data(0)(1).flatMap(Serialized.un[Part]),
			data(0)(2).flatMap(Serialized.un[Part])
		) match {
			case (Some(head), Some(binding), Some(handle))
				if head.shape == Pickaxe.head && binding.shape == GenericParts.binding && handle.shape == GenericParts.handle =>
				Some(Pickaxe(0, head, binding, handle))
			case _ => None
		}
		override def result(pick: Pickaxe): ItemStack = ItemUtil.withNBT(new ItemStack(Tool.Item), Serialized(pick))
	}
}