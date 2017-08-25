package com.cmonkey.raft

import java.util.logging.Logger

import scala.concurrent.duration.TimeUnit

object Rfat {

  type Term = Long
  type Id = Long
  type Index = Long
  type RequestId = Long

  val NOT_VOTED: Id = -1
  val NO_LEADER: Id = -1
  val NO_TERM: Term = -1
  val NO_PING_SENT: Long = 0

  def now = System.currentTimeMillis()

  case class Duration(count: Int, unit: TimeUnit) extends Ordered[Duration]{
    lazy val toMills: Long = unit.toMicros(count)

    override def compare(that: Duration): Int = this.toMills.compareTo(that.toMills)
  }

  case class Config(peers: Seq[Id]){
    lazy val size = peers.size
    lazy val majority = size/2+1
  }

  trait Logging{
    def log = Logger.getGlobal
  }

}
