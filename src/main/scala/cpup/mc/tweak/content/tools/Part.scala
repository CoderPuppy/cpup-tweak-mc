package cpup.mc.tweak.content.tools

import cpup.mc.tweak.content.BaseItem
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import cpup.mc.lib.util.serializing.{SerializableType, Serialized, SerializationRegistry}
import cpup.mc.lib.util.ItemUtil
import scala.collection.mutable
import net.minecraft.nbt.NBTTagCompound
import cpup.mc.tweak.CPupTweak

case class Part(shape: Part.Shape, material: Part.Material, modifications: Part.Modification*) extends Stats.Modification {
	override def modify[T](name: String, orig: T)(implicit manifest: Manifest[T]): T = {
		var curr = orig
		curr = Part.getModification(material, shape).modify(name, curr)
		for(mod <- modifications) {
			curr = Part.getModification(mod, material, shape).modify(name, curr)
		}
		curr
	}
}

object Part {
	case class Shape(id: String, mod: Option[String] = None)

	// currently the categories are metal, fabric, cord (string, vines, etc...) and wood
	// if the material can't be separated from your mod include the mod part (eg. metal.thaumcraft:thaumium)
	// otherwise don't include it (unless you have a really good reason to) (eg. metal.copper, metal.iron, metal.tin, metal.aluminum, metal.bronze)
	// also register zinc as tin and brass as bronze (unless you have a really good reason to)
	case class Material(category: String, mod: Option[String] = None, inst: Option[String] = None) {
		override def toString: String = (mod match {
			case Some(mod) => s"$mod:"
			case None => ""
		}) + category + (inst match {
			case Some(inst) => s".$inst"
			case None => ""
		})
	}

	case class Modification(id: String)

	private var _materials = Map[(Material, Shape), Stats.Modification]()
	def register(material: Material, shape: Shape, stats: Stats.Modification) {
		_materials += (((material, shape), stats))
	}
	def unregister(material: Material, shape: Shape) {
		_materials -= ((material, shape))
	}
	def getModification(material: Material, shape: Shape) = _materials((material, shape))

	private var _modifications = Map[(Modification, Option[Material], Option[Shape]), Stats.Modification]()
	def modifications = _modifications
	def register(mod: Modification, material: Option[Material], shape: Option[Shape], stats: Stats.Modification) {
		_modifications += (((mod, material, shape), stats))
	}
	def unregister(mod: Modification, material: Option[Material], shape: Option[Shape]) {
		_modifications -= ((mod, material, shape))
	}
	def getModification(mod: Modification, material: Material, shape: Shape) = {
		_modifications.get((mod, Some(material), Some(shape))).getOrElse(Stats.Modification.NOOP) +
		_modifications.get((mod, Some(material), None)).getOrElse(Stats.Modification.NOOP) +
		_modifications.get((mod, None, Some(shape))).getOrElse(Stats.Modification.NOOP) +
		_modifications.get((mod, None, None)).getOrElse(Stats.Modification.NOOP)
	}

	object Type extends SerializableType[Part, NBTTagCompound] {
		def mod = CPupTweak
		override def id = s"${mod.ref.modID}:tools.part"
		override def cla = classOf[Part]

		override def nbtClass = classOf[NBTTagCompound]
		override def writeToNBT(part: Part) = {
			val nbt = new NBTTagCompound
			nbt.setTag("shape", Serialized(part.shape))
			nbt.setTag("material", Serialized(part.material))
			nbt.setTag("modifications", Serialized(part.modifications))
			nbt
		}
		override def readFromNBT(nbt: NBTTagCompound) = (
			Serialized.un[Part.Shape](nbt.getCompoundTag("shape")),
			Serialized.un[Part.Material](nbt.getCompoundTag("material")),
			Serialized.un[List[Part.Modification]](nbt.getCompoundTag("modifications"))
		) match {
			case (shape: Part.Shape, material: Part.Material, modifications: List[Part.Modification]) =>
				Part(shape, material, modifications: _*)

			case r =>
				mod.logger.info("got {}", r)
				null
		}

	}
	SerializationRegistry.registerType(Type)

	object Item extends BaseItem {
		name = "part"

		override def addLore(stack: ItemStack, player: EntityPlayer, lore: mutable.Buffer[String], advanced: Boolean) {
			super.addLore(stack, player, lore, advanced)
			if(advanced) {
				lore += ItemUtil.compound(stack).toString
				lore += (SerializationRegistry.readFromNBT[Part](ItemUtil.compound(stack)) match {
					case part: Part => part.toString
					case null => "Not a Part: null"
				})
			}
		}
	}
}