package cpup.mc.tweak.content.tools

import cpup.mc.tweak.content.BaseItem
import net.minecraft.item.ItemStack
import net.minecraft.entity.player.EntityPlayer
import cpup.mc.lib.util.serializing.{SerializableType, Serialize, SerializationRegistry}
import cpup.mc.lib.util.{NBTUtil, ItemUtil}
import scala.collection.mutable
import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import cpup.mc.tweak.CPupTweak

case class Part(shape: Part.Shape, material: Part.Material, modifications: Part.Modification*)

object Part {
	trait Shape
	trait Material
	trait Modification

	private var _materials = Map[(Material, Shape), Any]()
	def register(material: Material, shape: Shape, stats: Any) {
		_materials += (((material, shape), stats))
	}
	def unregister(material: Material, shape: Shape) {
		_materials -= ((material, shape))
	}

	private var _modifications = Map[(Modification, Material, Shape), Any]()
	def modifications = _modifications
	def register(mod: Modification, material: Material, shape: Shape, stats: Any) {
		_modifications += (((mod, material, shape), stats))
	}
	def unregister(mod: Modification, material: Material, shape: Shape) {
		_modifications -= ((mod, material, shape))
	}

	object Type extends SerializableType[Part, NBTTagCompound] {
		def mod = CPupTweak
		override def id = s"${mod.ref.modID}:tools.part"
		override def cla = classOf[Part]

		override def nbtClass = classOf[NBTTagCompound]
		override def writeToNBT(part: Part) = {
			val nbt = new NBTTagCompound
			nbt.setTag("shape", Serialize(part.shape))
			nbt.setTag("material", Serialize(part.material))
			nbt.setTag("modifications", Serialize(part.modifications))
			nbt
		}
		override def readFromNBT(nbt: NBTTagCompound) = (nbt.getCompoundTag("shape"), nbt.getCompoundTag("material"), nbt.getCompoundTag("modifications")) match {
			case (Serialize(shape: Part.Shape), Serialize(material: Part.Material), Serialize(modifications: List[Modification])) =>
				Part(shape, material, modifications)

			case _ => null
		}
	}
	SerializationRegistry.registerType(Type)

	object Item extends BaseItem {
		name = "part"

		override def addLore(stack: ItemStack, player: EntityPlayer, lore: mutable.Buffer[String], advanced: Boolean) {
			super.addLore(stack, player, lore, advanced)
			if(advanced) {
				lore += (SerializationRegistry.readFromNBT[Part](ItemUtil.compound(stack)) match {
					case part: Part => part.toString
					case null => "Not a Part: null"
				})
			}
		}
	}
}
