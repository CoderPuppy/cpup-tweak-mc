package cpup.mc.tweak.content.assistant

import cpup.mc.tweak.CPupTweak
import net.minecraft.client.model.{ModelRenderer, ModelBase}
import net.minecraft.client.renderer.entity
import net.minecraft
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

object Render extends entity.Render {
	override def doRender(entity: minecraft.entity.Entity, x: Double, y: Double, z: Double, yaw: Float, dt: Float) {
		bindEntityTexture(entity)
		GL11.glPushMatrix()
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)

		GL11.glTranslated(x, y, z)

		model.render(entity, 0, 0, 0, 0, 0, dt)

		GL11.glPopAttrib()
		GL11.glPopMatrix()
	}

	final val resourceLocation = new ResourceLocation(s"${CPupTweak.ref.modID}:textures/models/assistant.png")
	override def getEntityTexture(entity: minecraft.entity.Entity): ResourceLocation = resourceLocation

	val model = new Model

	class Model extends ModelBase {
		val scale = 1 / 16f

		val body = new ModelRenderer(this, "body")

		setTextureOffset("body.middle", 0, 0)
		body.addBox("middle", -2, -2, -2, 4, 4, 4).rotateAngleX = math.toRadians(45).toFloat

		val smeltingWing = new ModelRenderer(this, "smelting-wing")

		val craftingWing = new ModelRenderer(this, "crafting-wing")

		val storageWing = new ModelRenderer(this, "storage-wing")

		override def render(entity: minecraft.entity.Entity, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float) {
			val assistant = entity.asInstanceOf[Entity]

			body.render(scale)

			smeltingWing.render(scale)
			craftingWing.render(scale)
			storageWing.render(scale)
		}
	}
}
