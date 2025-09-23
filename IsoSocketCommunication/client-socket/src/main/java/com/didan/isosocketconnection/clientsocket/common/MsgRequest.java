package com.didan.isosocketconnection.clientsocket.common;

import com.didan.isosocketconnection.clientsocket.handler.SynchronizeResponseHandler;
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
