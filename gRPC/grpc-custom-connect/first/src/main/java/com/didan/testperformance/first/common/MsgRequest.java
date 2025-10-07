package com.didan.testperformance.first.common;

import com.didan.testperformance.first.config.socket.handler.SynchronizeResponseHandler;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jpos.iso.ISOMsg;

@NoArgsConstructor
@Getter
@Setter
public class MsgRequest {

  private Channel channel;
  private ISOMsg isoMsg;
  private SynchronizeResponseHandler handler;
  private byte[] msgBytes;
  private String connectionName;

  public MsgRequest(Channel channel, ISOMsg isoMsg, SynchronizeResponseHandler handler) {
    this.channel = channel;
    this.isoMsg = isoMsg;
    this.handler = handler;
  }
}
