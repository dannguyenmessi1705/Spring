package com.didan.isosocketconnection.clientsocket.decoder;

import com.didan.isosocketconnection.clientsocket.util.MsgUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

@Slf4j
public class IsoMessageDecoder extends ByteToMessageDecoder {

  private MsgUtils msgUtils;

  public IsoMessageDecoder(GenericPackager isoPackage) {
    this.msgUtils = new MsgUtils(isoPackage);
  }

  @Override
  protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
    doDecode(channelHandlerContext, byteBuf, list); // Gọi phương thức doDecode để xử lý giải mã
  }

  public void doDecode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
    try {
      while (byteBuf.readableBytes() > 4) { // Kiểm tra nếu còn dữ liệu để đọc
        byteBuf.markReaderIndex(); // Đánh dấu vị trí hiện tại của con trỏ đọc

        int thousand = byteBuf.readByte() - 48; // Đọc byte hàng nghìn và chuyển đổi từ ASCII sang số
        int hundred = byteBuf.readByte() - 48; // Đọc byte hàng trăm và chuyển đổi từ ASCII sang số
        int ten = byteBuf.readByte() - 48; // Đọc byte hàng chục và chuyển đổi từ ASCII sang số
        int unit = byteBuf.readByte() - 48; // Đọc byte hàng đơn vị và chuyển đổi từ ASCII sang số
        int size = thousand * 1000 + hundred * 100 + ten * 10 + unit; // Tính toán kích thước dữ liệu

        if (byteBuf.readableBytes() < size) { // Kiểm tra nếu dữ liệu còn lại không đủ để đọc
          byteBuf.resetReaderIndex(); // Quay lại vị trí đã đánh dấu
          return; // Dừng xử lý để chờ thêm dữ liệu
        }

        ByteBuf slice = byteBuf.readSlice(size); // Lấy một phần dữ liệu có kích thước đã tính toán
        byte[] data = new byte[size];
        slice.readBytes(data); // Đọc dữ liệu vào mảng byte

        log.info(String.format("Message receipt length %d - Data: %s", data.length, new String(data))); // Ghi log thông tin nhận được
        list.add(unpack(data)); // Giải mã dữ liệu và thêm vào danh sách kết quả

      }
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex); // Ghi log lỗi nếu có ngoại lệ xảy ra
    }
  }

  public byte[] pack(ISOMsg obj) {
    try {
      return msgUtils.pack(obj); // Gói gọn ISOMsg thành mảng byte
    } catch (Exception ex) {
      log.error("Can not pack message: ", ex); // Ghi log lỗi nếu không thể gói gọn
      throw new RuntimeException(ex.getMessage(), ex);
    }
  }

  public ISOMsg unpack(byte[] data) {
    try {
      return msgUtils.unpack(data); // Giải mã mảng byte thành ISOMsg
    } catch (Exception ex) {
      log.error("Can not unpack message: ", ex); // Ghi log lỗi nếu không thể giải mã
      return null;
    }
  }
}
