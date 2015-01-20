package cpup.mc.tweak.content.tools

class Stats {

}

object Stats {
	trait Modification {
		def modify[T](name: String, orig: T)(implicit manifest: Manifest[T]): T
		def +(mod: Modification) = Modification.Combine(this, mod)
	}
	object Modification {
		case object NOOP extends Modification {
			def modify[T](name: String, orig: T)(implicit manifest: Manifest[T]) = orig
		}

		case class Combine(a: Modification, b: Modification) extends Modification {
			def modify[T](name: String, orig: T)(implicit manifest: Manifest[T]) = b.modify(name, a.modify(name, orig))
		}

		case class HarvestLevel(tool: String, level: Int) extends Modification {
			def modify[T](name: String, orig: T)(implicit manifest: Manifest[T]) = {
				if(name == s"harvest-level:$tool" && orig.isInstanceOf[Int]) {
					level.asInstanceOf[T]
				} else {
					orig
				}
			}
		}
	}
}