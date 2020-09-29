package org.excavator.boot.raft.counter

import java.util.concurrent.atomic.AtomicLong

import com.alipay.sofa.jraft
import com.alipay.sofa.jraft.Status
import com.alipay.sofa.jraft.core.StateMachineAdapter

class CounterStateMachine extends StateMachineAdapter{
  private val value = new AtomicLong(0)
  override def onApply(iter: jraft.Iterator): Unit = {

    while(iter.hasNext){
      var delta = 0L
      var closure:IncrementAndAddClosure = null

      if(iter.done() != null){
        closure = iter.done().asInstanceOf[IncrementAndAddClosure]
        delta = closure.getRequest().getDelta
      }else{
        val data = iter.getData
        // convert data to delat
      }
      val prev = value.get()
      val updated = value.addAndGet(delta)

      if(null != closure){
        closure.getResponse().value = updated
        closure.getResponse().success = true
        closure.run(Status.OK)
      }

      iter.next()
    }
  }
}
