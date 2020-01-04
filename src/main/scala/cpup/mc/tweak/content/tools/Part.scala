package cpup.mc.tweak.content.tools

import cpup.mc.lib.util.{NBTUtil, ItemUtil}
import cpup.mc.lib.util.serialization.{SerializationError, Serialization, SerializationRegistry, Serialized}
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
	object Shape extends Serialization[Shape, NBTTagCompound] {
		def mod = CPupTweak

		override def id: String = s"${mod.ref.modID}:tools.part.shape"
		override def nbtClass = classOf[NBTTagCompound]
		override def cla = classOf[Shape]

		override def write(data: Shape): NBTTagCompound = {
			val nbt = new NBTTagCompound
			nbt.setTag("id", Serialized(data.id))
			for(mod <- data.mod) nbt.setTag("mod", Serialized(mod))
			return nbt
		}

		override def read(nbt: NBTTagCompound) = Serialized.un[String](nbt.getCompoundTag("id")) match {
			case Left(id) =>
				Left(Shape(id, Serialized.un[String](nbt.getCompoundTag("mod")).left.toOption))
			case Right(err) => Right(err)
		}
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
	object Material extends Serialization[Material, NBTTagCompound] {
		def mod = CPupTweak

		override def id: String = s"${mod.ref.modID}:tools.part.material"

		override def nbtClass = classOf[NBTTagCompound]
		override def cla = classOf[Material]

		override def write(data: Material): NBTTagCompound = {
			val nbt = new NBTTagCompound
			nbt.setTag("category", Serialized(data.category))
			for(mod <- data.mod) nbt.setTag("mod", Serialized(mod))
			for(id <- data.id) nbt.setTag("id", Serialized(id))
			return nbt
		}

		override def read(nbt: NBTTagCompound) = Serialized.un[String](nbt.getCompoundTag("category")) match {
			case Left(category) =>
				Left(Material(
					category,
					Serialized.un[String](nbt.getCompoundTag("id")).left.toOption,
					Serialized.un[String](nbt.getCompoundTag("mod")).left.toOption
				))
			case Right(err) => Right(err)
		}
	}
	SerializationRegistry.registerType(Material)

	case class Modifier(id: String)
	object Modifier extends Serialization[Modifier, NBTTagString] {
		def mod = CPupTweak

		override def id: String = s"${mod.ref.modID}:tools.part.modification"

		override def nbtClass = classOf[NBTTagString]
		override def cla = classOf[Modifier]

		override def write(data: Modifier): NBTTagString = new NBTTagString(data.id)

		override def read(nbt: NBTTagString) = Left(Modifier(nbt.func_150285_a_))
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

	object Type extends Serialization[Part, NBTTagCompound] {
		def mod = CPupTweak
		override def id = s"${mod.ref.modID}:tools.part"
		override def cla = classOf[Part]

		override def nbtClass = classOf[NBTTagCompound]
		override def write(part: Part) = {
			val nbt = new NBTTagCompound
			nbt.setTag("shape", Serialized(part.shape))
			nbt.setTag("material", Serialized(part.material))
			nbt.setTag("modifications", Serialized(part.modifications))
			nbt
		}
		override def read(nbt: NBTTagCompound) = (
			Serialized.un[Part.Shape](nbt.getCompoundTag("shape")),
			Serialized.un[Part.Material](nbt.getCompoundTag("material")),
			Serialized.un[List[Part.Modifier]](nbt.getCompoundTag("modifications"))
		) match {
			case (Left(shape), Left(material), Left(modifications)) =>
				Left(Part(shape, material, modifications: _*))

			case (shape, material, modifications) =>
				Right(
					List[Either[_, SerializationError]](shape, material, modifications)
						.filter(_.isRight).map(_.right.get)
						.fold(SerializationError())(_ + _)
				)
		}

	}
	SerializationRegistry.registerType(Type)

	object Item extends BaseItem {
		name = "part"

		override def addLore(stack: ItemStack, player: EntityPlayer, lore: mutable.Buffer[String], advanced: Boolean) {
			super.addLore(stack, player, lore, advanced)
			if(advanced) {
				lore += ItemUtil.compound(stack).toString
				lore += (Serialized.un[Part](stack) match {
					case Left(part) => part.toString
					case Right(e) => e.toString
				})
			}
		}
	}
}
