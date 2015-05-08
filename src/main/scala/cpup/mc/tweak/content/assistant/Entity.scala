package cpup.mc.tweak.content.assistant

import java.util.UUID

import cofh.api.energy.IEnergyStorage
import com.mojang.authlib.GameProfile
import net.minecraft
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World

class Entity(world: World, _x: Int, _y: Int, _z: Int) extends minecraft.entity.Entity(world) with IEnergyStorage {
	setPosition(_x, _y, _z)
	dataWatcher.addObject(18, 0)

	override def entityInit {}

	override def updateFallState(dist: Double, onGround: Boolean) {}
	override def fall(dist: Float) {}

	def energy = dataWatcher.getWatchableObjectInt(18)
	def energy_=(n: Int) = { dataWatcher.updateObject(18, n); n }
	final val maxEnergy = 10000 // TODO: config option

	var owner: GameProfile = null

	override def writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setInteger("energy", energy)
		nbt.setString("ownerName", owner.getName)
		nbt.setString("ownerUUID", owner.getId.toString)
	}

	override def readEntityFromNBT(nbt: NBTTagCompound) {
		energy = nbt.getInteger("energy")
		owner = new GameProfile(UUID.fromString(nbt.getString("ownerUUID")), nbt.getString("ownerName"))
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