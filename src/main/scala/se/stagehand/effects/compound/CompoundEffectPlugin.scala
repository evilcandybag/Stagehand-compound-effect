package se.stagehand.effects.compound

import se.stagehand.plugins.EffectPlugin
import se.stagehand.lib.scripting.Effect
import se.stagehand.plugins.ComponentGUI

class CompoundEffectPlugin extends EffectPlugin {

  val name = "Stagehand-compound-effect"
  
  val guis: List[ComponentGUI] = List(CompoundGUI)
  val effects: Array[Effect] = Array(new CompoundEffect)
  
}