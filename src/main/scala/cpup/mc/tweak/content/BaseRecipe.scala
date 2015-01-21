package cpup.mc.tweak.content

import cpup.mc.lib.CPupModHolder
import cpup.mc.tweak.CPupTweak
import cpup.mc.lib.content.CPupRecipe

trait BaseRecipe extends CPupRecipe with CPupModHolder[CPupTweak.type] {
	def mod = CPupTweak
}