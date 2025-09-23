package com.didan.isosocketconnection.clientsocket.connector;

import com.didan.isosocketconnection.clientsocket.common.MsgRequest;
import com.didan.isosocketconnection.clientsocket.constant.ISO8385Constant;
import com.didan.isosocketconnection.clientsocket.constant.ISO8385Message;
import com.didan.isosocketconnection.clientsocket.decoder.IsoMessageDecoder;
import com.didan.isosocketconnection.clientsocket.handler.IsoMessageHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.packager.GenericPackager;

@Slf4j
public class NettyConnection {

  private final Bootstrap bootstrap = new Bootstrap(); // Doi tuong Bootstrap cua Netty dung de khoi tao ket noi
  private SocketAddress socketAddress; // Dia chi server
  private Channel channel; // Doi tuong channel
  private Timer timer; // Timer de quan ly ket noi
  private IsoMessageDecoder decoder; // Doi tuong decoder de giai ma tin nhan
  private final String host; // Dia chi IP hoac ten mien cua server
  private final String port; // Cong ket noi cua server
  @Setter
  @Getter
  private boolean isActive = false; // Trang thai ket noi

  /**
   * Khoi tao ket noi Netty
   *
   * @param host
   * @param port
   * @param connector
   * @param classHandler
   * @param classDecoder
   * @param packager
   */
  public NettyConnection(String host, String port, String connector, String classHandler, String classDecoder, GenericPackager packager) {
    this.host = host;
    this.port = port;
    initConnection(host, port, connector, classHandler, classDecoder, packager);
  }

  /**
   * Khoi tao ket noi
   *
   * @param host
   * @param port
   * @param connector
   * @param classHandler
   * @param classDecoder
   * @param packager
   */
  private void initConnection(String host, String port, String connector, String classHandler, String classDecoder, GenericPackager packager) {
    try {
      Constructor<?> cDecoder = Class.forName(classDecoder).getConstructor(GenericPackager.class); // Lay constructor cua class decoder voi tham so la GenericPackager
      this.decoder = (IsoMessageDecoder) cDecoder.newInstance(packager); // Khoi tao doi tuong decoder

      Constructor<?> cHandler = Class.forName(classHandler).getConstructor(String.class, IsoMessageDecoder.class); // Lay constructor cua class handler voi tham so la String va IsoMessageDecoder

      socketAddress = new InetSocketAddress(host, Integer.parseInt(port)); // Khoi tao dia chi server
      bootstrap.group(new NioEventLoopGroup(ISO8385Constant.NUMBER_NIO_EVENT_THREADS)); // khai báo kết nối có một EventLoopGroup dùng để xử lý các sự kiện mạng
      bootstrap.channel(NioSocketChannel.class); // Chỉ định loại kênh sử dụng để kết nối (ở đây là kênh NIO thuoc Non-blocking I/O)
      bootstrap.option(ChannelOption.SO_KEEPALIVE, true) // Cấu hình tùy chọn kênh (ở đây là giữ kết nối luôn hoạt động)
          .option(ChannelOption.SO_BACKLOG, 1024) // Cấu hình tùy chọn kênh (ở đây là số lượng kết nối tối đa trong hàng đợi)
          .option(ChannelOption.SO_REUSEADDR, true) // Cấu hình tùy chọn kênh (ở đây là cho phép tái sử dụng địa chỉ)
          .option(ChannelOption.TCP_NODELAY, true) // Cấu hình tùy chọn kênh (ở đây là tắt chế độ Nagle để gửi dữ liệu ngay lập tức)
          .option(ChannelOption.SO_RCVBUF, 64 * 1024) // Cấu hình tùy chọn kênh (ở đây là kích thước bộ đệm nhận)
          .option(ChannelOption.SO_SNDBUF, 64 * 1024); // Cấu hình tùy chọn kênh (ở đây là kích thước bộ đệm gửi)
      bootstrap.handler(new ChannelInitializer<SocketChannel>() {
        /*
                    Khởi tạo chuỗi Channel Handle gắn với 1 kết nối
                    DataTransferDecoder: Decode byte nhận được => iso
                    IdleStateHandler: Handle detect khi kết nối rơi vào trang thái Idle (30s không đoc, không ghi)
                    ClientHandler: Nhận và xử lý ban tin iso, xử lý khi rơi kết nối idle.
         */
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
          socketChannel.pipeline().addLast((IsoMessageDecoder) cDecoder.newInstance(packager)); // Thêm decoder vào pipeline của kênh
          socketChannel.pipeline().addFirst(new IdleStateHandler(0L, 0L, 30L, TimeUnit.SECONDS)); // Thêm IdleStateHandler vào pipeline của kênh để phát hiện trạng thái nhàn rỗi (30 giây không đọc, không ghi)
          socketChannel.pipeline().addLast("ResponseHandler", (IsoMessageHandler) cHandler.newInstance(connector, decoder)); // Thêm handlervào pipeline của kênh để xử lý tin nhắn và các sự kiện khác
        }
      });

      timer = new Timer(); // Khoi tao doi tuong timer

      scheduleConnect(10); // bat dau ket noi sau 10 giay
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
    }
  }

  /**
   * Liên tục cố gắng kết nối đến server
   * @param millis
   */
  private void scheduleConnect(long millis) {
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        doConnect();
      }
    }, millis); // Lên lịch thực hiện nhiệm vụ kết nối sau khoảng thời gian millis
  }

  private void doConnect() {
    try {
      log.info("Connecting to :{}", socketAddress.toString());
      ChannelFuture f = bootstrap.connect(socketAddress); // Khởi tạo kết nối, tuy nhiên do bất đồng bộ bên chưa biết thành công hay không.
      // Kết quả của kết nối được trả về qua đói tượng ChannelFuture.
      f.addListener(new ChannelFutureListener() {
        /**
         * Xử lý kết quả của kết nối khi kết nối hoàn thành
         * @param channelFuture
         * @throws Exception
         */
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
          if (!channelFuture.isSuccess()) { // Neu ket noi that bai
            isActive = false;
            log.info("Reconnect fail to :{}", socketAddress.toString());
            channelFuture.channel().close();
            Thread.sleep(15000); // Chờ một thời gian trước khi kết nối lại
            bootstrap.connect(socketAddress).addListener(this); // Khởi tạo lại kết nối add callback này cho kết nối mới tạo ra. Nếu kết nối vãn không thành công => Tiếp lục lặp lại kết nối đến khi nào thành công.
          } else { // Neu ket noi thanh cong
            channel = channelFuture.channel(); // Lưu trữ kênh kết nối
            // Đăng ký một listener để xử lý khi kết nối bị mất
            channel.closeFuture().addListener(f -> {
              log.info("Connection lost to :{}", socketAddress.toString());
              channel.close(); // Đóng kênh kết nối
              scheduleConnect(5); // Lên lịch kết nối lại sau 5 giây
            });
            log.info("Connect to {} OK !!!", socketAddress.toString());
            isActive = true;
          }
        }
      });
    } catch (Exception ex) {
      log.error("Cannot connect to :{}", socketAddress.toString(), ex);
      scheduleConnect(1000); // Lên lịch kết nối lại sau 1 giây nếu có lỗi xảy ra trong quá trình kết nối
    }
  }

  /**
   * Gửi thông điệp ISO đến server
   * @param request
   */
  public void onSender(MsgRequest request) {
    try {
      log.info("Send ISO message at: {}:{}", host, port);
      byte[] output = decoder.pack(request.getIsoMsg()); // Mã hóa thông điệp ISO thành mảng byte
      ByteBuf buf = channel.alloc().buffer().writeBytes(output); // Tạo một ByteBuf và ghi mảng byte vào đó
      ChannelFuture future = channel.writeAndFlush(buf); // Gửi dữ liệu qua kênh
      future.addListener(f -> { // Thêm một listener để xử lý kết quả gửi dữ liệu
        if (f.isSuccess()) {
          log.info("Send ISO message at: {}:{}, {}", host, port, new String(output));
        } else {
          log.info("Send ISO message fail at: {}:{}, {}", host, port, new String(output));
          log.info("Cause: {}", f.cause().getMessage());
          log.error("Error: {}", f.cause().getMessage(), f.cause());
        }
      });
    } catch (Exception ex) {
      log.error("Cannot send ISO message at: {}:{}", host, port, ex);
      throw ex;
    }
  }
}
