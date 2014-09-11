package cpup.mc.tweak.content.tools

import cpup.mc.tweak.content.BaseItem

case class Part(typ: Part.Pos, material: Part.Material, modifications: Part.Modification*)

object Part {
	trait Material
	trait Modification
	trait Pos

	object Item extends BaseItem {
		name = "part"
	}
}