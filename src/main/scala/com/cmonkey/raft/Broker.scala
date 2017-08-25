package com.cmonkey.raft

import java.util.concurrent.{ArrayBlockingQueue, BlockingQueue, Executors, TimeUnit}

import com.cmonkey.raft.Rfat._

trait Broker {

  def send(pdu: AddressedPDU)

  def receive(timeout: Duration): Option[AddressedPDU]

}

class AsyncBroker[T] (config: Config, timeout: Duration) extends  Logging{

  private val megs = new scala.collection.mutable.HashMap[Id, BlockingQueue[AddressedPDU]]

  private[raft] val threadPool = Executors.newFixedThreadPool(config.peers.size)

  def addPeer(id: Id, timeout: Duration): Peer[T] = {
    val repo = new Repository[T]()

    val peer = new Peer[T](id, config, timeout) {
      override def getEntries(start: Index, end: Index): Seq[LogEntry[T]] = {
        repo.getEntries(start,end)
      }

      override def putEntries(entries: Seq[LogEntry[T]]): Unit = {
        repo.putEntries(entries)
      }

      override def containsEntry(entryKey: Entry): Boolean = {
        repo.containsEntry(entryKey)
      }

      override def send(pdu: AddressedPDU): Unit = offer(pdu)

      override def receive(timeout: Duration): Option[AddressedPDU] = {
        val head = poll(id, timeout)

        Option(head)
      }

      override def close(): Unit = {
        shutdonw
      }
    }

    megs.put(peer.id, new ArrayBlockingQueue[AddressedPDU](1))
    threadPool.submit(peer)

    return peer
  }

  def offer(pdu: AddressedPDU): Unit = {
    val maybe: Option[BlockingQueue[AddressedPDU]] = megs.get(pdu.target)
    if(maybe.isEmpty)
      throw new IllegalStateException("No peer with id " + pdu.target)
    maybe.get.offer(pdu, timeout.count, timeout.unit)
  }

  def poll(id: Id, timeout: Duration):AddressedPDU = {
    val maybe: Option[BlockingQueue[AddressedPDU]] = megs.get(id)

    maybe.get.poll(timeout.count, timeout.unit)
  }

  def shutdonw: Unit ={
    threadPool.shutdown()
    threadPool.awaitTermination(1, TimeUnit.MINUTES)
  }
}
