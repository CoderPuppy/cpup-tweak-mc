package cpup.mc.tweak.content.assistant

import scala.collection.mutable

import net.minecraft.nbt.NBTTagCompound

class Data {
  val wings = new mutable.HashMap[Class[Wing], Wing]()

  def readFromNBT(nbt: NBTTagCompound) {

  }

  def writeToNBT(nbt: NBTTagCompound) {

  }
}
