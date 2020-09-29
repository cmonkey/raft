package org.excavator.boot.raft.counter

import com.alipay.remoting.{AsyncContext, BizContext}
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor

class IncrementAndGetRequestProcessor(countServer: CounterServer) extends AsyncUserProcessor[IncrementAndGetRequest]{
  override def handleRequest(bizContext: BizContext, asyncContext: AsyncContext, t: IncrementAndGetRequest): Unit = {
  }

  override def interest(): String = {
    IncrementAndGetRequest.getClass.getName
  }
}
