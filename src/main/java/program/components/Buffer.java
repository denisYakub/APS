package program.components;

import lombok.Getter;
import program.Controller;
import program.Order;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;

@Getter
public class Buffer {
  @NotNull
  private final ArrayList<Order> orders;
  private Order canceledOrder;
  private final int capacity;
  LinkedList<Integer> indexesQueue;
  private int insertIndex;
  private int fetchIndex;
  private int size;
  private final Boolean[] elementsToDelete;

  public Buffer(final int capacity) {
    this.capacity = capacity;
    this.size = 0;
    this.insertIndex = 0;
    this.fetchIndex = 0;
    orders = new ArrayList<>(capacity);
    for (int i = 0; i < capacity; i++) {
      orders.add(null);
    }
    indexesQueue = new LinkedList<>();
    elementsToDelete = new Boolean[capacity];
    for (int i = 0; i < capacity; i++) {
      elementsToDelete[i] = false;
    }
    canceledOrder = new Order(0, "-", 0);
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public boolean isFull() {
    return size == capacity;
  }

  public Integer findMinIdCustomer(){
    int minIdCustomer = Integer.MAX_VALUE;
    for (Order order : orders) {
      if (order != null && minIdCustomer > order.clientId()) {
        minIdCustomer = order.clientId();
      }
    }
    return minIdCustomer;
  }

  public Integer findMaxIdCustomer(){
    int minIdCustomer = Integer.MIN_VALUE;
    for (Order order : orders) {
      if (order != null && minIdCustomer < order.clientId()) {
        minIdCustomer = order.clientId();
      }
    }
    return minIdCustomer;
  }

  public void addOrder(@NotNull final Order order) {
    if (isFull()) {
      canceledOrder = order;
      makeStatistics(canceledOrder, orders.get(orders.size()-1));
//      makeStatistics(canceledOrder, order);
    } else {
      canceledOrder = new Order(0, "-", 0);
      while (orders.get(insertIndex) != null) {
        insertIndex = (insertIndex + 1) % capacity;
      }
      orders.set(insertIndex, order);
      insertIndex = 0;
      size++;
    }
  }

  public Order getOrder() {
    if (isEmpty()) {
      throw new RuntimeException("Buffer is empty!");
    }
    if (isElementInQueueToDelete()) {
      double minStartTime = Double.MAX_VALUE;
      int indexToDelete = Integer.MAX_VALUE;
      for (int i = 0; i < orders.size(); i++) {
        if (elementsToDelete[i] && minStartTime > orders.get(i).startTime()) {
          minStartTime = orders.get(i).startTime();
          indexToDelete = i;
        }
      }

      Order request = orders.get(indexToDelete);
      orders.set(indexToDelete, null);
      elementsToDelete[indexToDelete] = false;
      for (int index = indexToDelete + 1; index < capacity; index++){
        if (orders.get(index) != null) {
          orders.set(index-1, orders.get(index));
          orders.set(index, null);
          if (elementsToDelete[index]){
            elementsToDelete[index-1] =true;
            elementsToDelete[index] = false;
          }
        }
      }
      size--;
      return request;
    }
    else{
      Integer minIdCustomer = findMinIdCustomer();
      for (int i = 0; i < orders.size(); i++) {
        if (orders.get(i) != null && orders.get(i).clientId() == minIdCustomer) {
          elementsToDelete[i] = true;
        }
      }
      if (isElementInQueueToDelete()) {
        double minStartTime = Double.MAX_VALUE;
        int indexToDelete = Integer.MAX_VALUE;
        for (int i = 0; i < orders.size(); i++) {
          if (elementsToDelete[i] && minStartTime > orders.get(i).startTime()) {
            minStartTime = orders.get(i).startTime();
            indexToDelete = i;
          }
        }

        Order request = orders.get(indexToDelete);
        orders.set(indexToDelete, null);
        elementsToDelete[indexToDelete] = false;
        for (int index = indexToDelete + 1; index < capacity; index++){
          if (orders.get(index) != null) {
            orders.set(index-1, orders.get(index));
            orders.set(index, null);
            if (elementsToDelete[index]){
              elementsToDelete[index-1] =true;
              elementsToDelete[index] = false;
            }
          }
        }
        size--;
        return request;
      }
    }
    throw new RuntimeException("Buffer is empty!");
  }

  private void makeStatistics(@NotNull final Order canceledOrder, @NotNull final Order order) {
    Controller.statisticsPub.orderCanceled(canceledOrder.clientId(),
            canceledOrder.startTime() - order.startTime());
//    Controller.statisticsPub.orderCanceled(canceledOrder.clientId(),
//            order.startTime() - canceledOrder.startTime());
  }

  boolean isElementInQueueToDelete() {
    for (Boolean aBoolean : elementsToDelete) {
      if (aBoolean) {
        return true;
      }
    }
    return false;
  }
}