package org.excavator.boot.raft.counter

import scala.beans.BeanProperty

case class IncrementAndGetRequest(@BeanProperty delta:Long) extends Serializable
