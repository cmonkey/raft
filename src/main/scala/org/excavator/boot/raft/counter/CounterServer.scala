package org.excavator.boot.raft.counter

import java.io.File

import com.alipay.remoting.rpc
import com.alipay.sofa.jraft.core.TimerManager
import com.alipay.sofa.jraft.entity.PeerId
import com.alipay.sofa.jraft.option.NodeOptions
import com.alipay.sofa.jraft.rpc.impl.BoltRpcServer
import com.alipay.sofa.jraft.rpc.{RaftRpcServerFactory, RpcServer}
import com.alipay.sofa.jraft.{Node, RaftGroupService}
import org.apache.commons.io.FileUtils

class CounterServer(dataPath:String, groupId:String, serverId:PeerId, nodeOptions: NodeOptions) {

  // raft 服务端框架
  private var raftGroupService:RaftGroupService  = null
  // raft 节点
  private var node:Node = null
  // 业务状态机
  private var fsm:CounterStateMachine = null


  // init data path
  FileUtils.forceMkdir(new File(dataPath))

  // init global time
  val timerManager = new TimerManager(50)

  val remoteRpc = new rpc.RpcServer(serverId.getPort)

  //remoteRpc.registerUserProcessor(new GetValueRequestProcessor(this))
  //remoteRpc.registerUserProcessor(new IncrementAndGetRequestProcessor(this))

  val rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint)
  rpcServer.registerProcessor(new IncrementAndGetRequestProcessor(this))
  //RaftRpcServerFactory.addRaftRequestProcessors(rpcServer)


  this.fsm = new CounterStateMachine

  nodeOptions.setFsm(this.fsm)

  nodeOptions.setLogUri(dataPath + File.separator + "log")

  nodeOptions.setRaftMetaUri(dataPath + File.separator + "raft_meta")

  nodeOptions.setSnapshotUri(dataPath + File.separator + "snapshot")

  this.raftGroupService = new RaftGroupService(groupId,serverId, nodeOptions, rpcServer)

  this.node = this.raftGroupService.start()

  def getFsm = this.fsm

  def getNode = this.node

  def getRaftGroupService() = this.raftGroupService

  def redirect() = {
    val response = new ValueResponse(0, false, "", "")
    if(null != node){
      val leader = node.getLeaderId
      if(null != leader){
        response.redirect = leader.toString
      }
    }

    response
  }

}
