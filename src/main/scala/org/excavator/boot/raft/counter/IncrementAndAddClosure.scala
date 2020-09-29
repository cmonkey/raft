package org.excavator.boot.raft.counter

import com.alipay.sofa.jraft.{Closure, Status}

// 用于Leader 服务端的接收
class IncrementAndAddClosure(val counterServer:CounterServer,
                             var request:IncrementAndGetRequest,
                             val response:ValueResponse,
                             val done:Closure /* 网络应答callback*/) extends Closure{
  override def run(status: Status): Unit = {
    if(done != null){
      done.run(status)
    }
  }

  def getRequest() = request

  def setRequest(request: IncrementAndGetRequest) = this.request = request

  def getResponse() = response
}
