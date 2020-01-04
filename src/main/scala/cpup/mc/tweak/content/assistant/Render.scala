package cpup.mc.tweak.content.assistant

import cpup.mc.tweak.CPupTweak
import net.minecraft.client.model.{ModelRenderer, ModelBase}
import net.minecraft.client.renderer.{Tessellator, entity}
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
		val scale = 1 / 4f

		val body = new ModelRenderer(this, "body")

		setTextureOffset("body.middle", 0, 0)
		body.addBox("middle", -2, -2, -2, 4, 4, 4).rotateAngleX = math.toRadians(45).toFloat

		override def render(entity: minecraft.entity.Entity, f1: Float, f2: Float, f3: Float, f4: Float, f5: Float, f6: Float) {
			val assistant = entity.asInstanceOf[Entity]

			val scale = 1/2f

			GL11.glPushMatrix
				GL11.glScalef(scale, scale, scale)
				val T = Tessellator.instance
				T.startDrawing(GL11.GL_TRIANGLES)
					// Top
					T.addVertexWithUV( 0,  1,  0, .5, 0)
					T.addVertexWithUV( 1,  0,  1,  0, 1)
					T.addVertexWithUV( 1,  0, -1,  1, 1)

					T.addVertexWithUV( 0,  1,  0, .5, 0)
					T.addVertexWithUV(-1,  0, -1,  1, 1)
					T.addVertexWithUV(-1,  0,  1,  0, 1)

					T.addVertexWithUV( 0,  1,  0, .5, 0)
					T.addVertexWithUV(-1,  0,  1,  1, 1)
					T.addVertexWithUV( 1,  0,  1,  0, 1)

					T.addVertexWithUV( 0,  1,  0, .5, 0)
					T.addVertexWithUV( 1,  0, -1,  0, 1)
					T.addVertexWithUV(-1,  0, -1,  1, 1)

					// Bottom
					T.addVertexWithUV( 0, -1,  0, .5, 0)
					T.addVertexWithUV( 1,  0, -1,  1, 1)
					T.addVertexWithUV( 1,  0,  1,  0, 1)

					T.addVertexWithUV( 0, -1,  0, .5, 0)
					T.addVertexWithUV(-1,  0,  1,  0, 1)
					T.addVertexWithUV(-1,  0, -1,  1, 1)

					T.addVertexWithUV( 0, -1,  0, .5, 0)
					T.addVertexWithUV( 1,  0,  1,  0, 1)
					T.addVertexWithUV(-1,  0,  1,  1, 1)

					T.addVertexWithUV( 0, -1,  0, .5, 0)
					T.addVertexWithUV(-1,  0, -1,  1, 1)
					T.addVertexWithUV( 1,  0, -1,  0, 1)
				T.draw()

				GL11.glScalef(5, 5, 5)
				GL11.glTranslated(0, 0, 0.5)
				wing(new CraftingWing, 0, 0)
			GL11.glPopMatrix
		}
	}

	// render a wing that is facing in towards -Z
	// the very front of the wing fill be at Z=0
	// it will be 1 tall (centered around 0)
	def wing(wing: Wing, u: Int, v: Int) {
		val rad = 0.11 // radius but not a circle
		val endRad = rad * 0.75
		val endLength = 0.20
		val endInset = 0.05
		val inset = 0.20
		val depth = 0.10
		val T = Tessellator.instance
		T.startDrawingQuads
			for(y <- List(-1, 1)) {
				// top front
				T.addVertexWithUV(-endRad, y * (0.5 - endInset)            , 0, u, v)
				T.addVertexWithUV( endRad, y * (0.5 - endInset)            , 0, u + 1, v)
				T.addVertexWithUV( rad   , y * (0.5 - endLength - endInset), inset, u + 1, v + 1)
				T.addVertexWithUV(-rad   , y * (0.5 - endLength - endInset), inset, u, v + 1)

				// top back
				T.addVertexWithUV(-endRad, y * (0.5), depth, u, v)
				T.addVertexWithUV(-rad, y * (0.5 - endLength - endInset), depth + inset, u, v + 1)
				T.addVertexWithUV(rad, y * (0.5 - endLength - endInset), depth + inset, u + 1, v + 1)
				T.addVertexWithUV(endRad, y * (0.5), depth, u + 1, v)

				// top top
				T.addVertexWithUV(-endRad, y * (0.5), depth, u, v + 1)
				T.addVertexWithUV(endRad, y * (0.5), depth, u + 1, v + 1)
				T.addVertexWithUV(endRad, y * (0.5 - endInset), 0, u + 1, v)
				T.addVertexWithUV(-endRad, y * (0.5 - endInset), 0, u, v)

				// top right (from the wing's perspective)
				T.addVertexWithUV(endRad, y * (0.5 - endInset)            , 0, u, v + 1)
				T.addVertexWithUV(endRad, y * (0.5)                       , depth, u + 1, v + 1)
				T.addVertexWithUV(   rad, y * (0.5 - endLength)           , depth + inset, u + 1, v)
				T.addVertexWithUV(   rad, y * (0.5 - endLength - endInset), inset, u, v)

				// top left (from the wing's perspective)
				T.addVertexWithUV(-endRad, y * (0.5 - endInset)            , 0, u, v + 1)
				T.addVertexWithUV(-rad   , y * (0.5 - endLength - endInset), inset, u, v)
				T.addVertexWithUV(-rad   , y * (0.5 - endLength)           , depth + inset, u + 1, v)
				T.addVertexWithUV(-endRad, y * (0.5)                       , depth, u + 1, v + 1)

				GL11.glScalef(0, -1, 0)
			}

			// middle front
			T.addVertexWithUV(-rad,  0.5 - endLength - endInset, inset, u    , v    )
			T.addVertexWithUV( rad,  0.5 - endLength - endInset, inset, u + 1, v    )
			T.addVertexWithUV( rad, -0.5 + endLength + endInset, inset, u + 1, v + 1)
			T.addVertexWithUV(-rad, -0.5 + endLength + endInset, inset, u    , v + 1)

			// middle back
			T.addVertexWithUV(-rad,  0.5 - endLength - endInset, depth + inset, u    , v    )
			T.addVertexWithUV(-rad, -0.5 + endLength + endInset, depth + inset, u + 1, v    )
			T.addVertexWithUV( rad, -0.5 + endLength + endInset, depth + inset, u + 1, v + 1)
			T.addVertexWithUV( rad,  0.5 - endLength - endInset, depth + inset, u    , v + 1)

			// top right (from the wing's perspective)
			T.addVertexWithUV(endRad, 0.5 - endInset            , 0            , u    , v + 1)
			T.addVertexWithUV(endRad, 0.5                       , depth        , u + 1, v + 1)
			T.addVertexWithUV(rad   , 0.5 - endLength           , depth + inset, u + 1, v    )
			T.addVertexWithUV(rad   , 0.5 - endLength - endInset, inset        , u    , v    )

			// top left (from the wing's perspective)
			T.addVertexWithUV(-endRad, 0.5 - endInset            , 0            , u    , v + 1)
			T.addVertexWithUV(-rad   , 0.5 - endLength - endInset, inset        , u    , v    )
			T.addVertexWithUV(-rad   , 0.5 - endLength           , depth + inset, u + 1, v    )
			T.addVertexWithUV(-endRad, 0.5                       , depth        , u + 1, v + 1)

			GL11.glScalef(0, -1, 0)
		T.draw
	}
}
