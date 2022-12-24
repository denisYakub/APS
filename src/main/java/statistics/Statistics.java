package statistics;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
public class Statistics {
  public static int countOfDevices;
  public static int countOfClients;
  public static double workTime;
  public static int sizeOfBuffer;
  public static double minimum;
  public static double maximum;
  public static double lambda; // Интенсивность потока событий
  private int devicesCount;
  private int clientsCount;
  private int bufferSize;
  private int totalOrdersCount;
  private int completedOrdersCount;
  private ArrayList<Double> devicesWorkTime; // Время работы каждого прибора
  private ArrayList<ClientStatistics> clientsStats;

  private Statistics(final int devicesCount, final int clientsCount) {
    this.devicesCount = devicesCount;
    this.clientsCount = clientsCount;
    this.bufferSize = sizeOfBuffer;
    this.totalOrdersCount = 0;
    this.completedOrdersCount = 0;
    //this.devicesWorkTime = new ArrayList<>(Collections.nCopies(devicesCount, 0.0));
    this.devicesWorkTime = new ArrayList<>(devicesCount);
    for (int i = 0; i < this.devicesCount; i++) {
      devicesWorkTime.add(i, 0.0);
    }
    //this.clientsStats = (ArrayList<ClientStatistics>) Stream.generate(ClientStatistics::new)
    //        .limit(clientsCount).collect(Collectors.toList());
    this.clientsStats = new ArrayList<>(clientsCount);
    for (int i = 0; i < this.clientsCount; i++) {
      clientsStats.add(i, new ClientStatistics());
    }
  }

  public static Statistics getInstance() {
    return new Statistics(countOfDevices, countOfClients);
  }

  public int getCanceledOrdersCount() {
    int sum = 0;
    for (ClientStatistics stat : clientsStats) {
      sum += stat.getCanceledOrdersCount();
    }
    return sum;
  }

  // Метод, вызываемый в момент создания заказа
  public void orderGenerated(final int clientId) {
    clientsStats.get(clientId).incrementGeneratedOrders();
    totalOrdersCount++;
  }

  // Метод, вызываемый в момент отмены заказа
  public void orderCanceled(final int clientId, final double usedTime) {
    clientsStats.get(clientId).incrementCanceledOrders();
    orderCompleted(clientId, usedTime, 0);
  }

  // Метод, вызываемый в момент завершения заказа
  public void orderCompleted(final int clientId, final double usedTime, final double processedTime) {
    clientsStats.get(clientId).addTotalTime(usedTime);
    clientsStats.get(clientId).addOnBufferTime(usedTime - processedTime);
    completedOrdersCount++;
  }

  // Увеличить время обработки клиента прибором
  public void addDeviceByClientTime(final int clientId, final double time) {
    clientsStats.get(clientId).addOnDeviceTime(time);
  }

  // Установить общее время работы конкретного прибора
  public void setTotalDeviceTime(final int deviceId, final double time) {
    devicesWorkTime.set(deviceId, devicesWorkTime.get(deviceId) + time);
  }
}
