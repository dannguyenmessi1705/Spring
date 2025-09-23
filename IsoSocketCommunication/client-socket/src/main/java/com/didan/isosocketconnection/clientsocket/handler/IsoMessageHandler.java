package com.didan.isosocketconnection.clientsocket.handler;

import com.didan.isosocketconnection.clientsocket.connector.NettyConnector;
import com.didan.isosocketconnection.clientsocket.constant.ISO8385Message;
import com.didan.isosocketconnection.clientsocket.decoder.IsoMessageDecoder;
import com.didan.isosocketconnection.clientsocket.util.DataUtils;
import com.didan.isosocketconnection.clientsocket.util.DateUtils;
import com.didan.isosocketconnection.clientsocket.util.LogUtils;
import com.didan.isosocketconnection.clientsocket.util.MsgUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import java.net.InetSocketAddress;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;

@Slf4j
/**
 * Xử lý các thông điệp ISO nhận được từ kênh.
 */
public class IsoMessageHandler extends SimpleChannelInboundHandler<ISOMsg> {

  private final String connectorName;
  private final IsoMessageDecoder decoder;

  public IsoMessageHandler(String connectorName, IsoMessageDecoder decoder) {
    super();
    this.connectorName = connectorName;
    this.decoder = decoder;
  }

  @Override
  /**
   * Xử lý thông điệp ISO khi nhận được.
   */
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, ISOMsg isoMsg) {
    try {
      log.info("Channel Read");
      (new MsgUtils()).print(isoMsg, connectorName);
      // Xử lý thông điệp ISO ở đây
      if (!"0810".equals(isoMsg.getMTI())) {
        // Xử lý thông điệp không phải là ping (MTI khác 0810) do 0810 là thông điệp ping
        NettyConnector.onResponse(isoMsg); // Gọi phương thức xử lý phản hồi
      } else {
        log.info("Read ping response message");
      }
    } catch (Exception ex) {
      log.error("Error in process Handler", ex);
    }
  }

  /**
   * Xử lý ngoại lệ xảy ra trong quá trình xử lý kênh.
   * @param ctx
   * @param cause
   * @throws Exception
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error("Exception in process Handler", cause);
    ctx.close();
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      String hostAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress(); // Lấy địa chỉ IP từ xa
      int portAddress = ((InetSocketAddress) ctx.channel().remoteAddress()).getPort(); // Lấy cổng từ xa
      log.info("Send ping request - [{}:{}]", hostAddress, portAddress);
      ISOMsg iso = new ISOMsg();
      iso.setMTI("0810");
      Date transDate = new Date();
      iso.set(ISO8385Message.TRANSMISSION_DATETIME, DateUtils.dateToString(transDate, "GMT", "MMddHHmmss")); // Đặt trường ngày giờ truyền
      iso.set(ISO8385Message.SYSTEM_TRACE_AUDIT_NUMBER, "170502"); // Đặt trường số theo dõi hệ thống
      iso.set(ISO8385Message.TELCO_CODE, "170502"); // Đặt mã viễn thông
      iso.set(ISO8385Message.REFERENCE_NUMBER, DataUtils.genField37(iso, transDate)); // Đặt số tham chiếu 37
      iso.set(70, "301"); // Đặt trường 70 với giá trị "301"

      String add = ctx.channel().remoteAddress().toString(); // Lấy địa chỉ kênh từ xa
      LogUtils.logISOPayload(add, iso); // Ghi log thông điệp ISO

      ByteBuf buf = ctx.channel().alloc().buffer().writeBytes(decoder.pack(iso)); // Đóng gói thông điệp ISO thành ByteBuf
      ChannelFuture future = ctx.channel().writeAndFlush(buf); // Gửi thông điệp qua kênh
      future.addListener(future1 -> { // Thêm listener để xử lý kết quả gửi
        if (future1.isSuccess()) {
          log.info("{} Class MessageHandler with method userEventTriggered for operationComplete is success", add);
        } else {
          log.info("{} Class MessageHandler with method userEventTriggered for operationComplete is fail", add);
        }
      });
    }
  }
}
