package cpup.mc.tweak.content.assistant

import com.typesafe.config.Config
import cpup.lib.module.{ModuleID, ModuleLoader}
import cpup.mc.lib.ModLifecycleHandler
import cpup.mc.lib.util.Side
import cpup.mc.tweak.CPupTweak
import cpw.mods.fml.client.registry.RenderingRegistry
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.registry.{GameRegistry, EntityRegistry}
import org.slf4j.Logger

@ModuleID(id = "assistant")
class Assistant(config: Config, logger: Logger) extends ModLifecycleHandler {
	override def preInit(e: FMLPreInitializationEvent) {
		logger.info("preinit")
		val entityID = EntityRegistry.findGlobalUniqueEntityId
		val entityName = s"${ModuleLoader.modulesByInst(this).id}:assistant"
		EntityRegistry.registerModEntity(classOf[Entity], entityName, entityID, CPupTweak, 64, 1, true)
		if(Side.effective == Side.CLIENT) {
			RenderingRegistry.registerEntityRenderingHandler(classOf[Entity], Render)
		}
		GameRegistry.registerItem(Item, Item.name)
	}
}
