package cpup.mc.tweak.content.tools

import cpup.mc.lib.util.ItemUtil
import cpup.mc.lib.util.serializing.{SerializableType, SerializationRegistry, Serialized}
import cpup.mc.tweak.CPupTweak
import cpup.mc.tweak.content.BaseItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagString, NBTTagCompound}
import net.minecraftforge.common.util.Constants.NBT
import scala.collection.mutable
import scala.reflect.runtime.universe.TypeTag

case class Part(shape: Part.Shape, material: Part.Material, modifications: Part.Modifier*) extends Stats.Modifier {
	override def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]): T = {
		var curr = orig
		curr = Part.getModification(material, shape).modify(name, curr)
		for(mod <- modifications) {
			curr = Part.getModification(mod, material, shape).modify(name, curr)
		}
		curr
	}
}

object Part {
	case class Shape(id: String, mod: Option[String] = None) {
		override def toString: String = {
			(mod match {
				case Some(mod) => s"$mod:"
				case None => ""
			}) + id
		}
	}
	object Shape extends SerializableType[Shape, NBTTagCompound] {
		def mod = CPupTweak

		override def id: String = s"${mod.ref.modID}:tools.part.shape"
		override def nbtClass: Class[_ <: NBTTagCompound] = classOf[NBTTagCompound]
		override def cla: Class[_ <: Shape] = classOf[Shape]

		override def writeToNBT(data: Shape): NBTTagCompound = {
			val nbt = new NBTTagCompound
			nbt.setString("id", data.id)
			for(mod <- data.mod) nbt.setString("mod", mod)
			return nbt
		}

		override def readFromNBT(nbt: NBTTagCompound): Shape = Shape(
			nbt.getString("id"),
			if(nbt.hasKey("mod", NBT.TAG_STRING)) {
				Some(nbt.getString("mod"))
			} else {
				None
			}
		)
	}
	SerializationRegistry.registerType(Shape)

	// currently the categories are metal, fabric, cord (string, vines, etc...) and wood
	// if the material can't be separated from your mod include the mod part (eg. metal.thaumcraft:thaumium)
	// otherwise don't include it (unless you have a really good reason to) (eg. metal.copper, metal.iron, metal.tin, metal.aluminum, metal.bronze)
	// also register zinc as tin and brass as bronze (unless you have a really good reason to)
	case class Material(category: String, id: Option[String] = None, mod: Option[String] = None) {
		override def toString: String = (mod match {
			case Some(mod) => s"$mod:"
			case None => ""
		}) + category + (id match {
			case Some(inst) => s".$inst"
			case None => ""
		})
	}
	object Material extends SerializableType[Material, NBTTagCompound] {
		def mod = CPupTweak

		override def id: String = s"${mod.ref.modID}:tools.part.material"

		override def nbtClass: Class[_ <: NBTTagCompound] = classOf[NBTTagCompound]
		override def cla: Class[_ <: Material] = classOf[Material]

		override def writeToNBT(data: Material): NBTTagCompound = {
			val nbt = new NBTTagCompound
			nbt.setString("category", data.category)
			for(mod <- data.mod) nbt.setString("mod", mod)
			for(id <- data.id) nbt.setString("id", id)
			return nbt
		}

		override def readFromNBT(nbt: NBTTagCompound): Material = Material(
			nbt.getString("category"),
			if(nbt.hasKey("id", NBT.TAG_STRING)) {
				Some(nbt.getString("id"))
			} else {
				None
			},
			if(nbt.hasKey("mod", NBT.TAG_STRING)) {
				Some(nbt.getString("mod"))
			} else {
				None
			}
		)
	}
	SerializationRegistry.registerType(Material)

	case class Modifier(id: String)
	object Modifier extends SerializableType[Modifier, NBTTagString] {
		def mod = CPupTweak

		override def id: String = s"${mod.ref.modID}:tools.part.modification"

		override def nbtClass: Class[_ <: NBTTagString] = classOf[NBTTagString]
		override def cla: Class[_ <: Modifier] = classOf[Modifier]

		override def writeToNBT(data: Modifier): NBTTagString = new NBTTagString(data.id)

		override def readFromNBT(nbt: NBTTagString): Modifier = Modifier(nbt.func_150285_a_)
	}
	SerializationRegistry.registerType(Modifier)

	private var _materials = Map[(Material, Shape), Stats.Modifier]()
	def register(material: Material, shape: Shape, stats: Stats.Modifier) {
		_materials += (((material, shape), stats))
	}
	def unregister(material: Material, shape: Shape) {
		_materials -= ((material, shape))
	}
	def getModification(material: Material, shape: Shape) = _materials.getOrElse((material, shape), Stats.Modifier.NOOP)

	private var _modifications = Map[(Modifier, Option[Material], Option[Shape]), Stats.Modifier]()
	def modifications = _modifications
	def register(mod: Modifier, material: Option[Material], shape: Option[Shape], stats: Stats.Modifier) {
		_modifications += (((mod, material, shape), stats))
	}
	def unregister(mod: Modifier, material: Option[Material], shape: Option[Shape]) {
		_modifications -= ((mod, material, shape))
	}
	def getModification(mod: Modifier, material: Material, shape: Shape) = {
		_modifications.getOrElse((mod, Some(material), Some(shape)), Stats.Modifier.NOOP) +
		_modifications.getOrElse((mod, Some(material), None), Stats.Modifier.NOOP) +
		_modifications.getOrElse((mod, None, Some(shape)), Stats.Modifier.NOOP) +
		_modifications.getOrElse((mod, None, None), Stats.Modifier.NOOP)
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
			Serialized.un[List[Part.Modifier]](nbt.getCompoundTag("modifications"))
		) match {
			case (shape: Part.Shape, material: Part.Material, modifications: List[Part.Modifier]) =>
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