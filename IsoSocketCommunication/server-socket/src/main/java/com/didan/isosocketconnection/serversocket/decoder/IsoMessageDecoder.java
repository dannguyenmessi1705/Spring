package com.didan.isosocketconnection.serversocket.decoder;

import com.didan.isosocketconnection.serversocket.util.MsgUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.util.List;

/**
 * Class handle giải mã thông điệp ISO từ dữ liệu nhị phân.
 */
@Slf4j
public class IsoMessageDecoder extends ByteToMessageDecoder {

  private final MsgUtils msgUtils;

  public IsoMessageDecoder(GenericPackager isoPackager) {
    this.msgUtils = new MsgUtils(isoPackager);
  }

  /**
   * Phương thức giải mã dữ liệu nhị phân thành thông điệp ISO (Client và Server phải dùng chung).
   * @param ctx
   * @param in
   * @param out
   * @throws Exception
   */
  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    try {
      while (in.readableBytes() > 4) { // Kiểm tra nếu có ít nhất 4 byte để đọc độ dài thông điệp
        in.markReaderIndex(); // Đánh dấu vị trí hiện tại của con trỏ đọc

        // Read 4-byte length header
        int thousand = in.readByte() - 48; // Chuyển đổi từ byte ASCII sang số nguyên
        int hundred = in.readByte() - 48; // Chuyển đổi từ byte ASCII sang số nguyên
        int ten = in.readByte() - 48; // Chuyển đổi từ byte ASCII sang số nguyên
        int unit = in.readByte() - 48; // Chuyển đổi từ byte ASCII sang số nguyên
        int messageLength = thousand * 1000 + hundred * 100 + ten * 10 + unit; // Tính toán độ dài thông điệp

        if (in.readableBytes() < messageLength) { // Kiểm tra nếu không đủ byte để đọc toàn bộ thông điệp
          in.resetReaderIndex(); // Quay lại vị trí đã đánh dấu
          return;
        }

        byte[] messageData = new byte[messageLength]; // Tạo mảng byte để lưu trữ dữ liệu thông điệp
        in.readBytes(messageData); // Đọc dữ liệu thông điệp vào mảng byte

        log.info("Server received message length: {} - Data: {}", messageLength, new String(messageData));

        ISOMsg isoMsg = msgUtils.unpack(messageData); // Giải mã dữ liệu thành thông điệp ISO
        if (isoMsg != null) { // Kiểm tra nếu thông điệp không null
          out.add(isoMsg); // Thêm thông điệp vào danh sách đầu ra
        }
      }
    } catch (Exception e) {
      log.error("Error decoding ISO message", e);
    }
  }

  /**
   * Phương thức đóng gói thông điệp ISO thành mảng byte để gửi đi (Client và Server phải dùng chung).
   * @param isoMsg
   * @return
   */
  public byte[] pack(ISOMsg isoMsg) {
    try {
      return msgUtils.pack(isoMsg);
    } catch (Exception e) {
      log.error("Error packing ISO message", e);
      throw new RuntimeException(e);
    }
  }
}
