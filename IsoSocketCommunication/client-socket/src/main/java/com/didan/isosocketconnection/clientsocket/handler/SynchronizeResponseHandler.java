package com.didan.isosocketconnection.clientsocket.handler;

import javax.print.attribute.standard.MediaSize.ISO;
import lombok.extern.slf4j.Slf4j;
import org.jpos.iso.ISOMsg;

/**
 * Xử lý phản hồi đồng bộ từ kênh ISO.
 */
@Slf4j
public class SynchronizeResponseHandler {
  private boolean isDone = false; // Trạng thái hoàn thành
  private ISOMsg responseData; // Dữ liệu phản hồi
  private long waitTimeout; // Thời gian chờ tối đa (ms)

  public SynchronizeResponseHandler(long waitTimeout) {
    this.waitTimeout = waitTimeout;
  }

  /**
   * Ham xử lý phản hồi từ kênh ISO.
   * @param resObj
   */
  public synchronized void handleResponse(ISOMsg resObj) {
    this.responseData = resObj;
    this.isDone = true;
    notifyAll(); // Thông báo tất cả các luồng đang chờ
  }

  /**
   * Kiểm tra trạng thái hoàn thành.
   * @return
   */
  public synchronized boolean isDone() {
    return isDone;
  }

  /**
   * Đặt trạng thái hoàn thành.
   */
  public synchronized void setDone() {
    isDone = true;
  }

  /**
   * Lấy dữ liệu phản hồi nếu đã hoàn thành.
   * @return
   */
  public synchronized ISOMsg getResponseData() {
    if (isDone()) {
      return responseData;
    } else {
      return null;
    }
  }

  public synchronized ISOMsg getResponse() {
    try {
      long oldNow = System.currentTimeMillis(); // Thời điểm bắt đầu chờ
      long endwait = oldNow + waitTimeout - 1; // Thời điểm kết thúc chờ
      long now = oldNow; // Thời điểm hiện tại
      while (!isDone() && endwait >= now) { // Chờ đến khi hoàn thành hoặc hết thời gian chờ
        log.debug("[Waiter] Handler going to WAIT state for handler! MAXIMUM WAIT: [{}] ms", waitTimeout);
        wait(waitTimeout); // Chờ
        now = System.currentTimeMillis(); // Cập nhật thời điểm hiện tại
      }
      log.debug("[Waiter] Handler going back to live for handler! ELAPSED: [{}] ms", System.currentTimeMillis() - oldNow);
      return getResponseData(); // Trả về dữ liệu phản hồi
    } catch (InterruptedException e) {
      log.error(e.getMessage(), e);
      Thread.currentThread().interrupt();
      throw new RuntimeException(e.getMessage());
    }
  }
}
