package cpup.mc.tweak.content.tools

import cpup.mc.lib.content.CPupRecipe
import cpup.mc.lib.util.ItemUtil
import cpup.mc.lib.util.serialization.{SerializationError, Serialization, SerializationRegistry, Serialized}
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

	object Type extends Serialization[Pickaxe, NBTTagCompound] {
		def mod = CPupTweak
		override def id = s"${mod.ref.modID}:tools.pickaxe"
		override def cla = classOf[Pickaxe]

		override def nbtClass = classOf[NBTTagCompound]
		override def write(pickaxe: Pickaxe) = {
			val nbt = new NBTTagCompound
			nbt.setTag("damage", Serialized(pickaxe.damage))
			nbt.setTag("head", Serialized(pickaxe.head))
			nbt.setTag("binding", Serialized(pickaxe.binding))
			nbt.setTag("handle", Serialized(pickaxe.handle))
			nbt
		}
		override def read(nbt: NBTTagCompound) = (
			Serialized.un[Int](nbt.getCompoundTag("damage")),
			Serialized.un[Part](nbt.getCompoundTag("head")),
			Serialized.un[Part](nbt.getCompoundTag("binding")),
			Serialized.un[Part](nbt.getCompoundTag("handle"))
		) match {
			case (Left(damage), Left(head), Left(binding), Left(handle)) => Left(Pickaxe(damage, head, binding, handle))
			case (damage, head, binding, handle) =>
				Right(
					List[Either[_, SerializationError]](damage, head, binding, handle)
						.filter(_.isRight)
						.map(_.right.get)
						.fold(SerializationError())(_ + _)
				)
		}
	}
	SerializationRegistry.registerType(Type)

	object Recipe extends BaseRecipe with CPupRecipe.Shaped[Pickaxe] {
		def width = 1
		def height = 3

		override def parse(inv: InventoryCrafting, ox: Int, oy: Int, data: Array[Array[Option[ItemStack]]]): Option[Pickaxe] = (
			Serialization.fromOption(data(0)(0)).left.flatMap(Serialized.un[Part]).left.flatMap(Serialization.validate[Part, Part.Shape]("part_shape", _.shape, Pickaxe.head)),
			data(0)(1).map(Serialized.un[Part]).getOrElse(Right(SerializationError())).left.flatMap(Serialization.validate[Part, Part.Shape]("part_shape", _.shape, GenericParts.binding)),
			data(0)(2).map(Serialized.un[Part]).getOrElse(Right(SerializationError())).left.flatMap(Serialization.validate[Part, Part.Shape]("part_shape", _.shape, GenericParts.handle))
		) match {
			case (Left(head), Left(binding), Left(handle))
				if head.shape == Pickaxe.head && binding.shape == GenericParts.binding && handle.shape == GenericParts.handle =>
				Some(Pickaxe(0, head, binding, handle))
			case _ => None
		}
		override def result(pick: Pickaxe): ItemStack = ItemUtil.withNBT(new ItemStack(Tool.Item), Serialized(pick))
	}
}
