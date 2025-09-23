package com.didan.isosocketconnection.clientsocket.connector;

import com.didan.isosocketconnection.clientsocket.common.MsgRequest;
import com.didan.isosocketconnection.clientsocket.constant.ISO8385Message;
import com.didan.isosocketconnection.clientsocket.decoder.IsoMessageDecoder;
import com.didan.isosocketconnection.clientsocket.handler.IsoMessageHandler;
import com.didan.isosocketconnection.clientsocket.handler.SynchronizeResponseHandler;
import com.didan.isosocketconnection.clientsocket.util.ConnectorUtils;
import com.didan.isosocketconnection.clientsocket.util.MsgUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NettyConnector {

  public static final Integer MAX_MESSAGE_CACHE = 99999999; // Thiet lap kich thuoc toi da cua cache
  private static Cache<Object, Object> messagesCache; // Cache de luu tru cac thong diep ISO
  private static final HashMap<String, List<NettyConnection>> lstNettyConnection = new HashMap<>(); // Danh sach cac ket noi Netty theo ten infa
  private final AtomicInteger atomicInt = new AtomicInteger(0); // Bien dem nguyen tu de quan ly so luong ket noi

  @Value("${netty.socket.connection.host}")
  private List<String> hosts; // Danh sach cac host de ket noi

  @Value("${netty.socket.connection.port}")
  private List<String> ports; // Danh sach cac port de ket noi

  protected GenericPackager getPackager() {
    return ConnectorUtils.readGenericManager("isodef.xml"); // Doc file cau hinh GenericPackager tu duong dan trong classpath
  }

  protected String getClassDecoder() {
    return IsoMessageDecoder.class.getCanonicalName(); // Tra ve ten lop giai ma thong diep ISO
  }

  protected String getClassHandler() {
    return IsoMessageHandler.class.getCanonicalName(); // Tra ve ten lop xu ly thong diep ISO
  }

  protected String getInfaName() {
    return this.getClass().getSimpleName(); // Tra ve ten lop hien tai
  }

  protected List<String> getListHost() {
    return hosts; // Tra ve danh sach cac host
  }

  protected List<String> getListPort() {
    return ports; // Tra ve danh sach cac port
  }

  @PostConstruct // Phuong thuc duoc goi sau khi bean duoc khoi tao
  private void init() {
    log.info("Begin init NettyConnector Config {} {} {}", getInfaName(), getListHost(), getListPort()); // In log bat dau khoi tao NettyConnector
    try {
      messagesCache = Caffeine.newBuilder()
          .maximumSize(MAX_MESSAGE_CACHE)
          .expireAfterWrite(Duration.ofMinutes(2))
          .evictionListener((key, value, cause) -> log.info("Message with key {} is removed from cache due to {}", key, cause))
          .build(); // Khoi tao cache voi kich thuoc toi da va thoi gian het han

      for (int i = 0; i < getListHost().size(); i++) { // Vong lap qua danh sach cac host
        String name = getInfaName();
        NettyConnection nettyConnection = new NettyConnection(
            getListHost().get(i),
            getListPort().get(i),
            name,
            getClassHandler(),
            getClassDecoder(),
            getPackager()); // Khoi tao ket noi Netty moi voi cac tham so cau hinh
        nettyConnection.setActive(true); // Dat trang thai ket noi la hoat dong
        List<NettyConnection> listCurrent = lstNettyConnection.get(name); // Lay danh sach ket noi hien tai theo ten infa
        if (listCurrent != null) {
          listCurrent.add(nettyConnection); // Neu danh sach ton tai, them ket noi moi vao danh sach
        } else {
          listCurrent = new ArrayList<>();
          listCurrent.add(nettyConnection); // Neu danh sach khong ton tai, tao moi va them ket noi vao danh sach
          lstNettyConnection.put(name, listCurrent); // Luu danh sach ket noi vao hashmap theo ten infa
        }
      }
    } catch (Exception ex) {
      log.error("Can't init NettyConnector", ex); // In log loi neu co ngoai le xay ra trong qua trinh khoi tao
    }
  }

  /**
   * Lay ket noi tot nhat tu danh sach ket noi theo ten infa
   *
   * @param connName
   * @return
   */
  protected NettyConnection getBestConnection(String connName) {
    try {
      log.info("Get best connection for {}", connName); // In log dang lay ket noi tot nhat cho ten infa
      List<NettyConnection> lstNetty = lstNettyConnection.get(connName); // Lay danh sach ket noi theo ten infa
      if (lstNetty == null) {
        log.warn("No connections found for {}", connName); // In log canh bao neu khong tim thay ket noi nao cho ten infa
        return null;
      }
      NettyConnection nettyConnection = null;
      int temp = this.atomicInt.incrementAndGet(); // Tang bien dem nguyen tu len 1
      if (temp == 2147483647) {
        this.atomicInt.set(0); // Neu bien dem dat gia tri toi da, dat lai ve 0
      }
      int idx = temp % lstNetty.size(); // Tinh vi tri ket noi trong danh sach bang phep chia lay du
      if ((lstNetty.get(idx)).isActive()) { // Kiem tra ket noi tai vi tri do co hoat dong khong
        nettyConnection = lstNetty.get(idx); // Neu hoat dong, chon ket noi do
      } else {
        for (int i = 0; i < lstNetty.size(); ++i) { // Neu ket noi tai vi tri do khong hoat dong, tim kiem trong danh sach
          int newIndex = (idx + i) % lstNetty.size(); // Tinh vi tri moi bang phep chia lay du
          if ((lstNetty.get(newIndex)).isActive()) { // Kiem tra ket noi tai vi tri moi co hoat dong khong
            nettyConnection = lstNetty.get(newIndex); // Chon ket noi hoat dong dau tien tim thay
            break;
          }
        }
      }
      return nettyConnection; // Tra ve ket noi tot nhat tim thay
    } catch (Exception ex) {
      log.error("Error getting best connection for " + connName, ex); // In log loi neu co ngoai le xay ra trong qua trinh lay ket noi
      return null;
    }
  }

  /**
   * Xu ly yeu cau ISO va gui den ket noi tot nhat
   * @param msg
   * @param connName
   * @param handler
   */
  public void onRequest(ISOMsg msg, String connName, SynchronizeResponseHandler handler) {
    try {
      (new MsgUtils()).print(msg, connName); // In thong diep ISO ra log
      MsgRequest req = new MsgRequest(null, msg, handler); // Tao doi tuong yeu cau thong diep
      req.setConnectionName(connName); // Dat ten ket noi cho yeu cau
      NettyConnection nettyConnection = this.getBestConnection(connName); // Lay ket noi tot nhat
      log.info("Put request: {}", msg.getString(ISO8385Message.REFERENCE_NUMBER)); // In log yeu cau duoc gui
      messagesCache.put(msg.getString(ISO8385Message.REFERENCE_NUMBER), req); // Luu yeu cau vao cache voi khoa la so tham chieu
      if (nettyConnection != null && nettyConnection.isActive()) {
        nettyConnection.onSender(req); // Neu ket noi ton tai va hoat dong, gui yeu cau qua ket noi do
      } else {
        log.error("No active connections available for {}", connName); // In log loi neu khong co ket noi hoat dong nao
        throw  new RuntimeException("No active connections available for " + connName);
      }
    } catch (Exception ex) {
      log.error("Error sending request for " + connName, ex); // In log loi neu co ngoai le xay ra trong qua trinh gui yeu cau
      throw new RuntimeException("Error sending request for " + connName, ex);
    }
  }

  public static void onResponse(ISOMsg msg) {
    try {
      String reqId = msg.getString(ISO8385Message.REFERENCE_NUMBER); // Lay so tham chieu tu thong diep ISO
      log.info("Get ISO response for RequestID {}", reqId); // In log da nhan duoc phan hoi cho so tham chieu
      MsgRequest request = (MsgRequest) messagesCache.getIfPresent(reqId); // Lay yeu cau tu cache bang so tham chieu
      if (request != null) {
        SynchronizeResponseHandler handler = request.getHandler(); // Lay bo xu ly phan hoi tu yeu cau
        handler.handleResponse(msg); // Goi bo xu ly de xu ly thong diep phan hoi
      } else {
        log.warn("No request found in cache for RequestID: {}", reqId); // In log canh bao neu khong tim thay yeu cau trong cache
        (new MsgUtils()).print(msg, "SERVER"); // In thong diep ISO ra log voi ten "SERVER"
      }
    } catch (Exception ex) {
      log.error("Error processing response", ex); // In log loi neu co ngoai le xay ra trong qua trinh xu ly phan hoi
      throw new RuntimeException("Error processing response", ex);
    }
  }
}
