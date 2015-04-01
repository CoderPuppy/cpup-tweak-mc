package cpup.mc.tweak.content.assistant

import java.util

import com.typesafe.config.Config
import cpup.lib.module.{ModuleLoader, ModuleID}
import cpup.mc.lib.ModLifecycleHandler
import cpup.mc.lib.util.Side
import cpup.mc.tweak.CPupTweak
import cpw.mods.fml.client.registry.RenderingRegistry
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.registry.EntityRegistry
import net.minecraft.entity.EntityList
import net.minecraft.entity.EntityList.EntityEggInfo
import org.slf4j.Logger

@ModuleID(id = "assistant")
class Assistant(config: Config, logger: Logger) extends ModLifecycleHandler {
	override def preInit(e: FMLPreInitializationEvent) {
		logger.info("preinit")
		val entityID = EntityRegistry.findGlobalUniqueEntityId
		val entityName = s"${ModuleLoader.modulesByInst(this).id}:assistant"
		EntityRegistry.registerGlobalEntityID(classOf[Entity], entityName, entityID)
		EntityRegistry.registerModEntity(classOf[Entity], entityName, entityID, CPupTweak, 64, 1, true)
		EntityList.entityEggs.asInstanceOf[util.HashMap[Integer, EntityList.EntityEggInfo]].put(entityID, new EntityEggInfo(entityID, 0, 16777215))
		if(Side.effective == Side.CLIENT) {
			RenderingRegistry.registerEntityRenderingHandler(classOf[Entity], Render)
		}
	}
}
