package org.excavator.boot.raft.counter

import java.nio.ByteBuffer

import com.alipay.remoting.{AsyncContext, BizContext}
import com.alipay.remoting.rpc.protocol.AsyncUserProcessor
import com.alipay.sofa.jraft.entity.Task
import com.alipay.sofa.jraft.rpc.{RpcContext, RpcProcessor}
import com.alipay.sofa.jraft.{Closure, Status}

class IncrementAndGetRequestProcessor(countServer: CounterServer) extends RpcProcessor[IncrementAndGetRequest]{
   def handleRequest(bizContext: BizContext, asyncContext: AsyncContext, request: IncrementAndGetRequest): Unit = {
    val response = new ValueResponse(0, true, "", "")
    val closure = new IncrementAndAddClosure(countServer, request, response , new Closure() {
      override def run(status: Status): Unit = {
        if(!status.isOk){
          response.setErrorMsg(status.getErrorMsg)
          response.setSuccess(false)
        }

        asyncContext.sendResponse(response)
      }
    })

    val task = new Task()
    task.setDone(closure)

    task.setData(ByteBuffer.wrap(Array(request.getDelta.toByte)))

    countServer.getNode.apply(task)
  }

  override def interest(): String = {
    IncrementAndGetRequest.getClass.getName
  }

  override def handleRequest(rpcCtx: RpcContext, request: IncrementAndGetRequest): Unit = {
    rpcCtx.sendResponse(request)
  }
}
