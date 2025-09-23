package com.didan.isosocketconnection.serversocket.handler;

import com.didan.isosocketconnection.serversocket.decoder.IsoMessageDecoder;
import com.didan.isosocketconnection.serversocket.processor.IsoMessageProcessor;
import com.didan.isosocketconnection.serversocket.util.MsgUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

/**
 * Class dùng để xử lý các thông điệp ISO nhận được từ client.
 */
@Slf4j
public class IsoMessageHandler extends SimpleChannelInboundHandler<ISOMsg> {

  private final IsoMessageDecoder decoder; // Dùng để giải mã và đóng gói các thông điệp ISO
  private final IsoMessageProcessor messageProcessor; // Dùng để xử lý các thông điệp ISO

  public IsoMessageHandler(GenericPackager packager) {
    this.decoder = new IsoMessageDecoder(packager);
    this.messageProcessor = new IsoMessageProcessor();
  }

  /**
   * Phương thức được gọi khi một kết nối mới được thiết lập.
   * @param ctx Ngữ cảnh của kênh kết nối.
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    log.info("Client connected: {}", ctx.channel().remoteAddress()); // Ghi log khi có client kết nối
    super.channelActive(ctx); // Gọi phương thức của lớp cha
  }

  /**
   * Phương thức được gọi khi một kết nối bị ngắt.
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("Client disconnected: {}", ctx.channel().remoteAddress()); // Ghi log khi có client ngắt kết nối
    super.channelInactive(ctx);
  }

  /**
   * Xử lý thông điệp ISO nhận được từ client.
   * @param ctx           the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
   *                      belongs to
   * @param msg           the message to handle
   * @throws Exception
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ISOMsg msg) throws Exception {
    try {
      log.info("Server received ISO message from: {}", ctx.channel().remoteAddress());

      // Process the message and generate response
      ISOMsg response = messageProcessor.processMessage(msg); // Xử lý thông điệp và tạo phản hồi

      if (response != null) {
        // Pack and send response
        byte[] responseData = decoder.pack(response); // Đóng gói thông điệp phản hồi
        ByteBuf responseBuf = ctx.channel().alloc().buffer().writeBytes(responseData); // Tạo ByteBuf để gửi phản hồi

        ctx.writeAndFlush(responseBuf).addListener(future -> { // Gửi phản hồi và lắng nghe kết quả
          if (future.isSuccess()) { // Kiểm tra nếu gửi thành công
            log.info("Response sent successfully to: {}", ctx.channel().remoteAddress());
          } else { // Nếu gửi thất bại
            log.error("Failed to send response to: {}", ctx.channel().remoteAddress(), future.cause());
          }
        });
      }
    } catch (Exception e) {
      log.error("Error processing ISO message", e);
    }
  }

  /**
   * Xử lý sự kiện người dùng, bao gồm cả sự kiện hết thời gian chờ.
   * @param ctx
   * @param evt
   * @throws Exception
   */
  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception { // Xử lý sự kiện người dùng
    if (evt instanceof IdleStateEvent) { // Kiểm tra nếu sự kiện là IdleStateEvent
      log.info("Client idle timeout: {}", ctx.channel().remoteAddress()); // Ghi log khi client không hoạt động trong thời gian quy định
      ctx.close();
    } else {
      super.userEventTriggered(ctx, evt); // Gọi phương thức của lớp cha nếu không phải sự kiện hết thời gian chờ
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
    log.error("Exception in server handler for: {}", ctx.channel().remoteAddress(), cause);
    ctx.close();
  }
}
