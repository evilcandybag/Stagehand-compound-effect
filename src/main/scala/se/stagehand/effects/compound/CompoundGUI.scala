package se.stagehand.effects.compound

import se.stagehand.swing.lib.EffectGUI
import se.stagehand.lib.scripting.Effect
import se.stagehand.swing.lib.TargetedPlayerEffectItem
import scala.swing.BoxPanel
import scala.swing.Orientation
import se.stagehand.swing.lib.EditorEffectItem
import scala.swing.BorderPanel
import se.stagehand.swing.lib.AddEffectsButton
import scala.swing.Button
import se.stagehand.lib.scripting.Targets
import scala.swing.Swing
import scala.swing.FlowPanel
import se.stagehand.swing.lib.GUIManager
import se.stagehand.swing.lib.PlayerEffectItem
import se.stagehand.swing.gui.Resizable

object CompoundGUI extends EffectGUI {
  val peer = classOf[CompoundEffect]
  
  def editorItem(effect: Effect) = new CompoundEditorItem(checkEffect[CompoundEffect](effect))
  def playerItem(effect: Effect) = new CompoundPlayerItem(checkEffect[CompoundEffect](effect))

}

class CompoundEditorItem(e: CompoundEffect) extends BoxPanel(Orientation.Horizontal) with EditorEffectItem[CompoundEffect] {
  def effect = e
  
  val effectList = new BoxPanel(Orientation.Vertical) {
      border = Swing.EtchedBorder(Swing.Raised)
  }
  val addButton:Button = new AddEffectsButton(this,effectList,(gui,comp) => {
    effect.addEffect(comp)
    effectList.contents += gui
    refresh
  }, (c) => {
    c != effect.getClass && !effect.effects.map(_.getClass()).contains(c)
  })
  
  contents += effectList
  contents += new FlowPanel { contents += addButton } 
  
  effect.effects.foreach(e => {
    val gui = GUIManager.componentByID[EditorEffectItem[_]](e.id)
    gui match {
      case Some(g) => {
        effectList.contents += g
      }
      case None => {
        effectList.contents += GUIManager.editorItem(e)
      }
    }
  })
  refresh
  
}

class CompoundPlayerItem(e: CompoundEffect) extends TargetedPlayerEffectItem[CompoundEffect](e) {
  private def me = this
  def effectItem = bp
  border = Swing.EtchedBorder(Swing.Raised)
    
  private def bp = new BoxPanel(Orientation.Vertical) {
    border = Swing.EtchedBorder(Swing.Raised)
    
    for (e <- effect.effects) {
      val item = GUIManager.getGUI[EffectGUI](e.getClass).playerItem(e)
      item.asInstanceOf[PlayerEffectItem[_ <: Effect]] match {
        case e: TargetedPlayerEffectItem[_] => contents += e.effectItem
        case e => contents += e
      }
      item.refresh
    }
    refresh
  }
  
  
}