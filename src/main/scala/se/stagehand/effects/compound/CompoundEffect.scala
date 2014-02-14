package se.stagehand.effects.compound

import se.stagehand.lib.scripting._
import scala.xml.Elem
import scala.swing._
import scala.xml.Node
import se.stagehand.lib.scripting.network.NetworkedTarget

/**
 * Effect that collects several targeted effects and gathers their arguments, so they are triggered as one.
 */
class CompoundEffect(id:Int) extends Effect(id) with Targets {
  def this() = this(ID.unique)
  
  def componentName = "Compound Effect"
    
  private var _effects:List[Effect] = List()
  
  def addEffect(e:Effect) {
    if (conflicts(e)) {
        throw new IllegalArgumentException("Effect " + e + " has conflicting run arguments with an existing effect." )
      } else {
        sourceArgs.foreach(x => e.addArg(x._1,x._2))
        _effects = e :: _effects
      }
  }
  def removeEffect(e:Effect) {
    sourceArgs.foreach(x => e.removeArg(x._1))
    _effects = _effects
  }
  
  def effects = _effects
  /**
   * Get all stored effects that have targets.
   */
  def effectsWithTargets = effects.filter(_.isInstanceOf[Effect with Targets]).map(_.asInstanceOf[Effect with Targets])
   
  def requirements = {
    effectsWithTargets.map(_.requirements).flatten.distinct.toSet
  }
  override def runArgs = {
    effectsWithTargets.map(_.runArgs).flatten.toMap ++ super.runArgs
  }
  
  /**
   * Override addTarget to make sure NetworkedTargets get added as they should.
   */
  override def addTarget(tar: Target) {
    tar match {
      case t:NetworkedTarget => {
        t.connect
      }
      case _ => {}
    }
    super.addTarget(tar)
  }
  
  /*
  override def addTarget(tar: Target) {
    effectsWithTargets.foreach(x => {
        x.addTarget(tar)
    })
  }
  override def removeTarget(tar: Target) {
    effectsWithTargets.foreach(x => {
      x.removeTarget(tar)
    })
  }*/
  
  /**
   * Check if an effect conflicts with another, already added effect.
   * Two effects conflict if they share a requirement. 
   */
  def conflicts(effect: Effect):Boolean = effect match {
    case e: Effect with Targets => {
	    e.runArgs.keys.foreach(x => {
	      if (requirements.contains(x)) {
	        return true
	      }
	    })
	    
	    return false
    }
    case _ => return false 
    
  }
  
  override def readInstructions(in: Node) {
    super.readInstructions(in)
    val fxXML = (in \ "effects") \ "id"
    val fx = fxXML.map(x => ID.fetch[Effect](x.text.toInt))
    
    fx.foreach(addEffect)
  }

  override def generateInstructions = {
    implicit var xml = super.generateInstructions
    xml = addChild(effectsXML)
    
    xml
  }
  private def effectsXML = {
    <effects>{_effects.map(_.idXML)}</effects>
  }

} 