package com.cmonkey.raft

import com.cmonkey.raft.Rfat.{Index, Term}

import scala.collection.mutable.ArrayBuffer

case class Entry(index: Index, term: Term) extends Ordered[Entry]{
  override def compare(that: Entry) = {
    val comp = this.term.compareTo(that.term)
    if(0 == comp) this.index.compare(that.index) else comp
  }
}
case class LogEntry[T](id: Entry, value: T)

trait LogRepository[T]{
  def getEntries(start: Index, end: Index): Seq[LogEntry[T]]
  def getEntry(entryKey: Index): LogEntry[T] = getEntries(entryKey, entryKey+1)(0)

  def containsEntry(entryKey: Entry): Boolean

  def putEntries(entries: Seq[LogEntry[T]]): Unit
  def putEntry(entry: LogEntry[T]) = putEntries(Seq(entry))
}

class Repository[T] extends LogRepository[T]{
  private[raft] var log = new ArrayBuffer[LogEntry[T]]

  override def getEntries(start: Index, end: Index): Seq[LogEntry[T]] = {
    if(log.size < end)
      throw new IllegalStateException()
    log.slice(start.toInt, end.toInt)
  }

  override def containsEntry(entryKey: Entry): Boolean = {
    val pos = entryKey.index.toInt
    if(log.size <= pos)
      false
    log(pos).id.term == entryKey.term
  }

  override def putEntries(entries: Seq[LogEntry[T]]): Unit = {
    val index = entries.head.id.index.toInt
    val delta = index - log.size
    delta match {
      case _ if delta < 0 => log = log.slice(0, index)
    }
  }
}
