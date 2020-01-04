package cpup.mc.tweak.content.assistant

import java.util.UUID

import cofh.api.energy.IEnergyStorage
import com.mojang.authlib.GameProfile
import net.minecraft
import net.minecraft.nbt.{NBTBase, NBTTagCompound}
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants.NBT

class Entity(world: World, _x: Int, _y: Int, _z: Int, var owner: GameProfile, data: Data) extends minecraft.entity.Entity(world) with IEnergyStorage {
	def this(world: World) = this(world, 0, 0, 0, null, new Data)

	setPosition(_x + .5, _y + .5, _z + .5)
	dataWatcher.addObject(18, 0)

	override def entityInit {}

	override def updateFallState(dist: Double, onGround: Boolean) {}
	override def fall(dist: Float) {}

	def energy = dataWatcher.getWatchableObjectInt(18)
	def energy_=(n: Int) = { dataWatcher.updateObject(18, n); n }
	final val maxEnergy = 10000 // TODO: config option

	override def writeEntityToNBT(nbt: NBTTagCompound) {
		nbt.setTag("pos", newDoubleNBTList(posX, posY, posZ))
		nbt.setInteger("energy", energy)
		nbt.setString("ownerName", owner.getName)
		nbt.setString("ownerUUID", owner.getId.toString)
		val d = new NBTTagCompound
		data.writeToNBT(d)
		nbt.setTag("data", d)
	}

	override def readEntityFromNBT(nbt: NBTTagCompound) {
		val pos = nbt.getTagList("pos", NBT.TAG_DOUBLE)
		val x = pos.func_150309_d(0); prevPosX = x; lastTickPosX = x; posX = x
		val y = pos.func_150309_d(1); prevPosY = y; lastTickPosY = y; posY = y
		val z = pos.func_150309_d(2); prevPosZ = z; lastTickPosZ = z; posZ = z
		energy = nbt.getInteger("energy")
		data.readFromNBT(nbt.getCompoundTag("data"))
		owner = new GameProfile(UUID.fromString(nbt.getString("ownerUUID")), nbt.getString("ownerName"))
	}

	override def receiveEnergy(maxReceive: Int, simulate: Boolean) = {
		val accepted = Math.min(maxReceive, maxEnergy - energy)
		if(!simulate)
			energy += accepted
		accepted
	}

	override def extractEnergy(maxExtract: Int, simulate: Boolean) = {
		val sent = Math.min(maxExtract, energy)
		if(!simulate)
			energy -= sent
		sent
	}

	override def getEnergyStored = energy

	override def getMaxEnergyStored = maxEnergy
}
