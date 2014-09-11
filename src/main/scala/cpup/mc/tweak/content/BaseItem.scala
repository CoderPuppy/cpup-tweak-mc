package cpup.mc.tweak.content

import cpup.mc.lib.content.CPupItem
import cpup.mc.tweak.CPupTweak

trait BaseItem extends CPupItem[CPupTweak.type] {
	final val mod = CPupTweak
}