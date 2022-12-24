package GUI.actions;

import GUI.Waveform;
import program.Controller;
import program.Order;
import program.events.Event;
import program.events.Type;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;

// Нажатие на кнопку "Следующий шаг"

public class NextStep extends AbstractAction {
  public static Event event = null;
  static double time = 0;
  @NotNull
  private final Controller controller;
  @NotNull
  private final DefaultTableModel bufferTable;
  @NotNull
  private final DefaultTableModel resultsTable;
  @NotNull
  private final DefaultTableModel devicesTable;
  @NotNull
  private final DefaultTableModel canselTable;
  @NotNull
  private final Waveform waveform;

  public NextStep(@NotNull final Controller controller,
                  @NotNull final DefaultTableModel bufferTable,
                  @NotNull final DefaultTableModel resultsTable,
                  @NotNull final DefaultTableModel devicesTable,
                  @NotNull final DefaultTableModel canselTable,
                  @NotNull final Waveform waveform) {
    this.controller = controller;
    this.bufferTable = bufferTable;
    this.resultsTable = resultsTable;
    this.devicesTable = devicesTable;
    this.canselTable = canselTable;
    this.waveform = waveform;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    for (int i = 0; i < controller.getBuffer().getCapacity(); i++) {

    }
    controller.stepMode();
    event = controller.getCurrentEvent();
    time = controller.getCurrentTime();
    System.out.println("Controller event, time = " + time + ", event = " + event.eventType + ", event id = " + event.id
            + "event order_id = " + event.orderId);

    for (int i = 0; i < controller.getBuffer().getCapacity(); i++) {
      bufferTable.setValueAt("", i, 1);
      Order order = controller.getBuffer().getOrders().get(i);
      if (order == null) {
        bufferTable.setValueAt("", i, 1);
      } else {
        if (controller.getBuffer().getElementsToDelete()[i]){
          bufferTable.setValueAt(order.orderId() + " пакет", i, 1);
        } else {
          bufferTable.setValueAt(order.orderId(), i, 1);
        }
      }
    }
    // bufferTable.setValueAt("~~>", controller.getBuffer().getInsertIndex(), 1);

    canselTable.setValueAt(controller.getBuffer().getCanceledOrder().orderId(), 0,0);

    for (int i = 0; i < controller.getDevices().size(); i++) {
      Order order = controller.getDevices().get(i).getCurrentOrder();
      if (order == null) {
        devicesTable.setValueAt("", i, 1);
      } else {
        devicesTable.setValueAt(order.orderId(), i, 1);
      }
    }
    String element;
    if (event.eventType == Type.GENERATED) {
      int currentIndex = controller.getBuffer().getInsertIndex()-1;
      if (controller.getBuffer().getInsertIndex() == 0)
      {
        currentIndex = controller.getBuffer().getCapacity()-1;
      }
      event.setOrderId(controller.getBuffer().getOrders().get(controller.getBuffer().getSize()-1).orderId());
      element = "Пользователь №" + event.id;
    } else {
      element = "Прибор №" + event.id;
    }
    if (event.id == -1) {
      if (controller.getBuffer().isFull()) {
        element = "Буфер переполнен";
        event.setEventType(Type.ERROR);
      } else {
        element = "Все приборы заняты";
        event.setEventType(Type.WARNING);
      }
    }

    if (controller.getBuffer().getCanceledOrder().orderId().equals("-")) {
      resultsTable.addRow(new Object[]{
              controller.getCurrentTime(),
              element,
              event.eventType,
              event.getOrderId(),
              controller.getStatistics().getCompletedOrdersCount(),
              controller.getStatistics().getCanceledOrdersCount()});
    } else {
      resultsTable.addRow(new Object[]{
              controller.getCurrentTime(),
              element,
              event.eventType,
              controller.getBuffer().getCanceledOrder().orderId(),
              controller.getStatistics().getCompletedOrdersCount(),
              controller.getStatistics().getCanceledOrdersCount()});
    }
    waveform.actionPerformed(e);
  }
}
