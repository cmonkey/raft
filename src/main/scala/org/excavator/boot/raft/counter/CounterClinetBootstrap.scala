package org.excavator.boot.raft.counter

import java.util.concurrent.CountDownLatch

import com.alipay.sofa.jraft.RouteTable
import com.alipay.sofa.jraft.conf.Configuration
import com.alipay.sofa.jraft.entity.PeerId
import com.alipay.sofa.jraft.option.CliOptions
import com.alipay.sofa.jraft.rpc.InvokeCallback
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl

object CounterClientBootstrap {

  def incrementAndGet(cliClientService: CliClientServiceImpl, leader: PeerId, delta: Int, latch: CountDownLatch) = {
    val request = new IncrementAndGetRequest(delta)

    cliClientService.getRpcClient.invokeAsync(leader.getEndpoint, request, new InvokeCallback() {
      override def complete(result: Any, err: Throwable): Unit = {
        latch.countDown()
        System.out.println(result);
      }
    }, 5000)

  }

  def main(args: Array[String]): Unit = {
    val groupId = "counter"
    val initConfStr = "127.0.0.1:8081,127.0.0.1:8082,127.0.0.1:8083"

    val conf = new Configuration()
    conf.parse(initConfStr)

    RouteTable.getInstance().updateConfiguration(groupId, conf)

    val cliClientService = new CliClientServiceImpl
    cliClientService.init(new CliOptions)

    RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000)

    val leader = RouteTable.getInstance().selectLeader(groupId)

    val n = 1000
    val latch = new CountDownLatch(n)

    for(i <- 0 until n){
      incrementAndGet(cliClientService, leader, i, latch)
    }

    latch.await()
  }
}
