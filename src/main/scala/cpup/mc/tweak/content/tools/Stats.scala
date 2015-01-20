package cpup.mc.tweak.content.tools

import scala.reflect.runtime.universe.TypeTag

class Stats {

}

object Stats {
	trait Modification {
		def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]): T
		def +(mod: Modification) = Modification.Combine(this, mod)
	}
	object Modification {
		case object NOOP extends Modification {
			def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]) = orig
		}

		case class Combine(a: Modification, b: Modification) extends Modification {
			def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]) = b.modify(name, a.modify(name, orig))
		}

		case class NamedFunction[MT](mname: String, fun: (MT) => MT)(implicit matchTypeTag: TypeTag[MT]) extends Modification {
			override def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]): T = if(name == mname && typeTag.tpe <:< matchTypeTag.tpe) {
				fun(orig.asInstanceOf[MT]).asInstanceOf[T]
			} else {
				orig
			}
		}
		type NFun[MT] = NamedFunction[MT]
		def NFun[MT](name: String, f: (MT) => MT)(implicit matchTypeTag: TypeTag[MT]) = NamedFunction[MT](name, f)

		case class Function[MT](fun: (String, MT) => MT)(implicit matchTypeTag: TypeTag[MT]) extends Modification {
			override def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]): T = if(typeTag.tpe <:< matchTypeTag.tpe) {
				fun(name, orig.asInstanceOf[MT]).asInstanceOf[T]
			} else {
				orig
			}
		}
		type Fun[MT] = Function[MT]
		def Fun[MT](f: (String, MT) => MT)(implicit matchTypeTag: TypeTag[MT]) = Function[MT](f)

		case class NValue[MT](mname: String, value: MT)(implicit matchTypeTag: TypeTag[MT]) extends Modification {
			override def modify[T](name: String, orig: T)(implicit typeTag: TypeTag[T]): T = if(name == mname && typeTag.tpe <:< matchTypeTag.tpe) {
				value.asInstanceOf[T]
			} else {
				orig
			}
		}
		type NVal[MT] = NValue[MT]
		def NVal[MT](name: String, value: MT)(implicit matchTypeTag: TypeTag[MT]) = NValue(name, value)
	}
}