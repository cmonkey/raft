package org.excavator.boot.raft.counter

import scala.beans.BeanProperty

case class ValueResponse(@BeanProperty
                         var value:Long, //成功情况下返回的最新value
                         @BeanProperty
                         var success:Boolean, // 是否成功
                         @BeanProperty
                         redirect:String, // 发生了重新选举，需要跳转的新leader节点
                         @BeanProperty // 失败情况下的错误信息
                         errorMsg:String // 失败情况下的错误信息
                        ) extends Serializable
