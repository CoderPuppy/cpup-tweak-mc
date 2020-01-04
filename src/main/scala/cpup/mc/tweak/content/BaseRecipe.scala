package cpup.mc.tweak.content

import cpup.mc.lib.CPupModHolder
import cpup.mc.lib.content.CPupRecipe
import cpup.mc.tweak.CPupTweak

trait BaseRecipe extends CPupRecipe with CPupModHolder[CPupTweak.type] {
	def mod = CPupTweak
}
