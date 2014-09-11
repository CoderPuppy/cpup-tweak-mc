package cpup.mc.tweak.content

import cpup.mc.tweak.CPupTweak
import cpup.mc.lib.content.CPupRecipe

trait BaseRecipe extends CPupRecipe[CPupTweak.type] {
	def mod = CPupTweak
}