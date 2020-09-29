package org.excavator.boot.raft.counter

import com.alipay.sofa.jraft.conf.Configuration
import com.alipay.sofa.jraft.entity.PeerId
import com.alipay.sofa.jraft.option.NodeOptions

object CounterServerBootstrap {
  def main(args: Array[String]): Unit = {
    val dataPath = "/tmp/raft"
    val groupId = "counter"
    val serverIdStr = "127.0.0.1:"

    val initConfStr = "127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083"

    val nodeOptions = new NodeOptions
    nodeOptions.setElectionTimeoutMs(5000)
    nodeOptions.setDisableCli(false)
    nodeOptions.setSnapshotIntervalSecs(30)

    for(port <- 8081 until 8084) {
      val serverId = new PeerId()
      serverId.parse(serverIdStr+port)

      val initConf = new Configuration()
      initConf.parse(initConfStr)

      nodeOptions.setInitialConf(initConf)

      val counterServer = new CounterServer(dataPath+"/"+port, groupId, serverId, nodeOptions)
    }

  }

}
