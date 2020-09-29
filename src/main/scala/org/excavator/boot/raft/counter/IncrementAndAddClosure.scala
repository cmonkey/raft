package org.excavator.boot.raft.counter

import com.alipay.sofa.jraft.{Closure, Status}

// 用于Leader 服务端的接收
class IncrementAndAddClosure(val counterServer:CounterServer,
                             val request:IncrementAndGetRequest,
                             val response:ValueResponse,
                             val done:Closure /* 网络应答callback*/) extends Closure{
  override def run(status: Status): Unit = {

  }
}
