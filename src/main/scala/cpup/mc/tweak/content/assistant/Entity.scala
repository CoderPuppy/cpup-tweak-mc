package cpup.mc.tweak.content.assistant

import cofh.api.energy.IEnergyStorage
import net.minecraft.entity.EntityAgeable
import net.minecraft.entity.ai.EntityAIFollowOwner
import net.minecraft.entity.passive.EntityTameable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraft

class Entity(world: World) extends minecraft.entity.Entity(world) with IEnergyStorage {
	dataWatcher.addObject(18, 0)

	override def entityInit {}

	override def updateFallState(dist: Double, onGround: Boolean) {}
	override def fall(dist: Float) {}

	def energy = dataWatcher.getWatchableObjectInt(18)
	def energy_=(n: Int) = { dataWatcher.updateObject(18, n); n }
	final val maxEnergy = 10000 // TODO: config option

	override def writeEntityToNBT(nbt: NBTTagCompound) {
		super.writeEntityToNBT(nbt)
		nbt.setInteger("energy", energy)
	}

	override def readEntityFromNBT(nbt: NBTTagCompound) {
		super.readFromNBT(nbt)
		energy = nbt.getInteger("energy")
	}

	override def receiveEnergy(maxReceive: Int, simulate: Boolean): Int = {
		val accepted = Math.min(maxReceive, maxEnergy - energy)
		if(!simulate)
			energy += accepted
		accepted
	}

	override def extractEnergy(maxExtract: Int, simulate: Boolean) = 0

	override def getEnergyStored = energy

	override def getMaxEnergyStored = maxEnergy
}